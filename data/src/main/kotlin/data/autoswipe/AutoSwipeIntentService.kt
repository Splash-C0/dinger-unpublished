package data.autoswipe

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.isOnNotMeteredInternet
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import data.account.AppAccountAuthenticator
import data.tinder.recommendation.GetRecommendationsAction
import data.tinder.recommendation.RecommendationUserResolver
import domain.autoswipe.FromErrorPostAutoSwipeUseCase
import domain.like.DomainLikedRecommendationAnswer
import domain.recommendation.DomainRecommendationUser
import iaps.PurchaseManager
import iaps.SKU_ACTIVATION_STATE_ACTIVATED
import org.stoyicker.dinger.data.R
import reporter.CrashReporter
import retrofit2.HttpException
import java.util.Date
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

internal class AutoSwipeIntentService : IntentService("AutoSwipe") {
  @Inject
  lateinit var crashReporter: CrashReporter

  @Inject
  lateinit var defaultSharedPreferences: SharedPreferences

  @Inject
  lateinit var getRecommendationsAction: GetRecommendationsAction

  @Inject
  lateinit var processRecommendationActionFactory: ProcessRecommendationActionFactoryWrapper

  @Inject
  lateinit var recommendationResolver: RecommendationUserResolver

  @Inject
  lateinit var reportHandler: AutoSwipeReportHandler

  @Inject
  lateinit var appAccountAuthenticator: AppAccountAuthenticator
  private var reScheduled = false
  private var ongoingActions = emptySet<Action<*>>()
  private lateinit var dontKillMyAppHuaweiWakeLock: PowerManager.WakeLock

  init {
    AutoSwipeComponentHolder.autoSwipeComponent.inject(this)
  }

  override fun onHandleIntent(intent: Intent?) {
    reportHandler.clearAllNotifications()
    reportHandler.buildPlaceHolder(this, crashReporter).apply {
      startForeground(id, delegate)
    }
    acquireDontKillMyAppHuaweiLock()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      getSystemService(ShortcutManager::class.java)?.reportShortcutUsed("autoswipe")
    }
    LikeBatchTracker.apply {
      init(this@AutoSwipeIntentService)
      if (!batchAdmitsMore()) {
        scheduleBecauseBatchLimitReached()
        return
      }
    }
    if (PurchaseManager.getAutoswipeSubscriptionState() != SKU_ACTIVATION_STATE_ACTIVATED) {
      scheduleBecauseSubscriptionNotEnabled()
      reportHandler.showNeedsSubscription(this)
      return
    }
    if (defaultSharedPreferences.getBoolean(
            getString(R.string.preference_key_autoswipe_enabled), defaultSharedPreferences.getBoolean(getString(R.string.preference_key_autoswipe_enabled_backup), true)) &&
        (!defaultSharedPreferences.getBoolean(getString(R.string.preference_key_wifi_only), false)
            || isOnNotMeteredInternet())) {
      try {
        startAutoSwipe()
      } catch (e: Exception) {
        scheduleBecauseError(e)
      }
    } else {
      silentReschedule()
    }
  }

  override fun onDestroy() {
    if (!reScheduled) {
      silentReschedule()
    }
    super.onDestroy()
  }

  abstract class Action<in Callback> {
    protected val commonDelegate by lazy { CommonResultDelegate(this) }

    abstract fun execute(owner: AutoSwipeIntentService, callback: Callback)

    abstract fun dispose()
  }

  class CommonResultDelegate(private val action: Action<*>) {
    fun onComplete(autoSwipeIntentService: AutoSwipeIntentService) {
      autoSwipeIntentService.clearAction(action)
    }

    fun onError(error: Throwable, autoSwipeIntentService: AutoSwipeIntentService) {
      if (error is HttpException && error.code() == 401) {
        onComplete(autoSwipeIntentService)
      } else {
        if (autoSwipeIntentService.appAccountAuthenticator.getApiToken() != null) {
          autoSwipeIntentService.scheduleBecauseError(error)
        } else {
          autoSwipeIntentService.scheduleBecauseError()
        }
        autoSwipeIntentService.clearAction(action)
      }
    }
  }

  private fun startAutoSwipe() = Unit.also {
    getRecommendationsAction.apply {
      ongoingActions += (this)
      execute(this@AutoSwipeIntentService,
          object : GetRecommendationsAction.Callback {
            override fun onRecommendationsReceived(
                recommendations: List<DomainRecommendationUser>) {
              if (recommendations.isEmpty()) {
                scheduleBecauseError(IllegalStateException("Empty recommendation list"))
              } else {
                processRecommendations(recommendations)
              }
            }
          })
    }
  }

  private fun processRecommendations(recommendations: List<DomainRecommendationUser>) {
    val latch = CountDownLatch(recommendations.size)
    var processedThisRun = 0 // This will be used to avoid rescheduling immediately if no likes happened
    var limitedUntil: Long? = null
    for (recommendation in recommendations) {
      LikeBatchTracker.addLike()
      processRecommendationActionFactory.delegate(recommendation).apply {
        ongoingActions += (this)
        execute(this@AutoSwipeIntentService,
            object : ProcessRecommendationAction.Callback {
              override fun onRecommendationProcessed(
                  answer: DomainLikedRecommendationAnswer, liked: Boolean) =
                  saveRecommendationToDatabase(
                      recommendation = recommendation,
                      liked = liked,
                      matched = answer.matched).also {
                    if (liked) {
                      reportHandler.addLikeAnswer(answer)
                    }
                    ++processedThisRun
                    latch.countDown()
                    if (limitedUntil == null && answer.rateLimitedUntilMillis != null) {
                      limitedUntil = answer.rateLimitedUntilMillis
                    }
                  }

              override fun onRecommendationProcessingFailed() =
                  saveRecommendationToDatabase(
                      recommendation,
                      liked = false,
                      matched = false).also {
                    // Assume the rest of the batch will fail too,
                    // is probably the most likely thing to happen
                    while (latch.count > 0) {
                      latch.countDown()
                    }
                  }
            })
      }
      // The latch may be done because either we swept all recommendations,
      // case in which this is not needed, or we failed to swipe one thus
      // assumed the next ones in the batch would fail and forcefully signalled
      // the latch
      if (latch.count == 0L) {
        break
      }
    }
    latch.await()
    if (limitedUntil != null) {
      scheduleBecauseLimited(limitedUntil!!)
    } else if (LikeBatchTracker.batchAdmitsMore()) {
      if (processedThisRun > 0) {
        scheduleBecauseMoreAvailable()
      } else {
        if (appAccountAuthenticator.getApiToken() != null) {
          scheduleBecauseError(Error(getString(R.string.autoswipe_notification_error_no_recs_processed)))
        } else {
          scheduleBecauseError()
        }
      }
    } else {
      scheduleBecauseBatchLimitReached()
    }
  }

  private fun saveRecommendationToDatabase(
      recommendation: DomainRecommendationUser, liked: Boolean, matched: Boolean) {
    recommendationResolver.insert(DomainRecommendationUser(
        bio = recommendation.bio,
        distanceMiles = recommendation.distanceMiles,
        commonFriends = recommendation.commonFriends,
        commonFriendCount = recommendation.commonFriendCount,
        commonLikes = recommendation.commonLikes,
        commonLikeCount = recommendation.commonLikeCount,
        id = recommendation.id,
        birthDate = recommendation.birthDate,
        name = recommendation.name,
        instagram = recommendation.instagram,
        teaser = recommendation.teaser,
        spotifyThemeTrack = recommendation.spotifyThemeTrack,
        gender = recommendation.gender,
        birthDateInfo = recommendation.birthDateInfo,
        contentHash = recommendation.contentHash,
        groupMatched = recommendation.groupMatched,
        sNumber = recommendation.sNumber,
        liked = liked,
        matched = matched,
        photos = recommendation.photos,
        jobs = recommendation.jobs,
        schools = recommendation.schools,
        teasers = recommendation.teasers))
  }

  private fun scheduleBecauseMoreAvailable() {
    reportHandler.showReport(
        this,
        crashReporter,
        null,
        null,
        AutoSwipeReportHandler.RESULT_MORE_AVAILABLE)
    ImmediatePostAutoSwipeAction().apply {
      ongoingActions += this
      execute(this@AutoSwipeIntentService, Unit)
      reScheduled = true
    }
    releaseResourcesAndDetachNotification()
  }

  private fun scheduleBecauseLimited(notBeforeMillis: Long) {
    FromRateLimitedPostAutoSwipeAction(notBeforeMillis).apply {
      ongoingActions += this
      execute(this@AutoSwipeIntentService, Unit)
    }
    reportHandler.showReport(
        this,
        crashReporter,
        notBeforeMillis,
        null,
        AutoSwipeReportHandler.RESULT_RATE_LIMITED)
    reScheduled = true
    LikeBatchTracker.closeBatch()
    releaseResourcesAndDetachNotification()
  }

  private fun scheduleBecauseBatchLimitReached() {
    val notBeforeMillis = Date().time + 1000 * 60 * 60 * 2L //2h from now
    FromRateLimitedPostAutoSwipeAction(notBeforeMillis).apply {
      ongoingActions += this
      execute(this@AutoSwipeIntentService, Unit)
    }
    reportHandler.showReport(
        this,
        crashReporter,
        notBeforeMillis,
        null,
        AutoSwipeReportHandler.RESULT_BATCH_CLOSED)
    reScheduled = true
    LikeBatchTracker.closeBatch()
    releaseResourcesAndDetachNotification()
  }

  private fun silentReschedule() = scheduleBecauseError()

  private fun scheduleBecauseError(error: Throwable? = null) {
    FromErrorPostAutoSwipeAction().apply {
      ongoingActions += this
      execute(this@AutoSwipeIntentService, Unit)
    }
    if (error != null) {
      crashReporter.report(error)
      reportHandler.showReport(
          this,
          crashReporter,
          FromErrorPostAutoSwipeUseCase.interval(this),
          error.localizedMessage,
          AutoSwipeReportHandler.RESULT_ERROR)
      releaseResourcesAndDetachNotification()
    } else {
      releaseResourcesAndRemoveNotification()
    }
    reScheduled = true
    LikeBatchTracker.closeBatch()
  }

  private fun scheduleBecauseSubscriptionNotEnabled() {
    FromSubscriptionNotActivatedPostAutoSwipeAction().apply {
      ongoingActions += this
      execute(this@AutoSwipeIntentService, Unit)
    }
    releaseResourcesAndRemoveNotification()
    reScheduled = true
    LikeBatchTracker.closeBatch()
  }

  private fun releaseResourcesAndRemoveNotification() {
    dismissOngoingActions()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      stopForeground(Service.STOP_FOREGROUND_REMOVE)
    } else {
      stopForeground(true)
    }
    releaseDontKillMyAppHuaweiLock()
  }

  private fun releaseResourcesAndDetachNotification() {
    dismissOngoingActions()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      stopForeground(Service.STOP_FOREGROUND_DETACH)
    } else {
      stopForeground(false)
    }
    releaseDontKillMyAppHuaweiLock()
  }

  private fun dismissOngoingActions() {
    ongoingActions.forEach { it.dispose() }
    ongoingActions = emptySet()
  }

  private fun clearAction(action: Action<*>) = action.apply {
    dispose()
    ongoingActions -= this
  }

  @SuppressLint("InvalidWakeLockTag")
  private fun acquireDontKillMyAppHuaweiLock() =
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && Build.MANUFACTURER.equals(OEM_HUAWEI)) {
        dontKillMyAppHuaweiWakeLock = getSystemService(PowerManager::class.java).newWakeLock(
            PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG_DONT_KILL_MY_APP_HUAWEI)
      } else Unit

  private fun releaseDontKillMyAppHuaweiLock() =
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && Build.MANUFACTURER.equals(OEM_HUAWEI)) {
        dontKillMyAppHuaweiWakeLock.release()
      } else Unit

  companion object {
    fun callingIntent(context: Context) = Intent(context, AutoSwipeIntentService::class.java)
  }
}

private const val OEM_HUAWEI = "Huawei"
private const val WAKE_LOCK_TAG_DONT_KILL_MY_APP_HUAWEI = "LocationManagerService"
