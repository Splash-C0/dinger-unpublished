package data

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import data.account.AccountComponentHolder
import data.account.AccountModule
import data.account.AppAccountAuthenticator
import data.account.DaggerAccountComponent
import data.autoswipe.AutoSwipeComponentHolder
import data.autoswipe.DaggerAutoSwipeComponent
import domain.alarm.AlarmHolder
import domain.alarm.AppAlarmManager
import domain.autoswipe.AutoSwipeHolder
import domain.autoswipe.AutoSwipeLauncherFactory
import domain.dislike.DislikeRecommendation
import domain.dislike.DislikeRecommendationHolder
import domain.like.LikeRecommendation
import domain.like.LikeRecommendationHolder
import domain.loggedincheck.LoggedInCheckHolder
import domain.login.AccountManagementHolder
import domain.login.facebook.FacebookLogin
import domain.login.facebook.FacebookLoginHolder
import domain.login.sms.SmsLogin
import domain.login.sms.SmsLoginHolder
import domain.logout.AutoSwipeServiceDestructor
import domain.logout.LogoutHolder
import domain.logout.StorageClear
import domain.matches.MatchedRecommendations
import domain.matches.MatchedRecommendationsHolder
import domain.profile.GetProfile
import domain.profile.GetProfileHolder
import domain.recommendation.GetRecommendation
import domain.recommendation.GetRecommendationHolder
import domain.seen.SeenRecommendation
import domain.seen.SeenRecommendationHolder
import domain.seen.SeenRecommendations
import domain.seen.SeenRecommendationsHolder
import reporter.CrashReporter
import javax.inject.Inject

/**
 * @see <a href="https://firebase.googleblog.com/2016/12/how-does-firebase-initialize-on-android.html">
 *     The Firebase Blog: How does Void initialize on Android</a>
 */
internal class InitializationContentProvider : ContentProvider() {
  @Inject
  lateinit var crashReporter: CrashReporter
  @Inject
  lateinit var facebookLoginImpl: FacebookLogin
  @Inject
  lateinit var smsLoginImpl: SmsLogin
  @Inject
  lateinit var accountManagerImpl: AppAccountAuthenticator
  @Inject
  lateinit var getRecommendationImpl: GetRecommendation
  @Inject
  lateinit var likeRecommendationImpl: LikeRecommendation
  @Inject
  lateinit var dislikeRecommendationImpl: DislikeRecommendation
  @Inject
  lateinit var alarmManagerImpl: AppAlarmManager
  @Inject
  lateinit var autoSwipeIntentFactoryImpl: AutoSwipeLauncherFactory
  @Inject
  lateinit var storageClearImpl: StorageClear
  @Inject
  lateinit var autoswipeServiceDestructor: AutoSwipeServiceDestructor
  @Inject
  lateinit var seenRecommendations: SeenRecommendations
  @Inject
  lateinit var getProfileImpl: GetProfile
  @Inject
  lateinit var matchedRecommendations: MatchedRecommendations
  @Inject
  lateinit var seenRecommendation: SeenRecommendation

  override fun onCreate(): Boolean {
    val rootModule = RootModule(context!!)
    val accountModule = AccountModule()
    AccountComponentHolder.accountComponent = DaggerAccountComponent.builder()
        .rootModule(rootModule)
        .accountModule(accountModule)
        .build()
    AutoSwipeComponentHolder.autoSwipeComponent = DaggerAutoSwipeComponent.builder()
        .rootModule(rootModule)
        .build()
    DaggerInitializationComponent.builder()
        .rootModule(rootModule)
        .accountModule(accountModule)
        .build()
        .inject(this)
    crashReporter.init(context!!)
    if (MissingSplitsManagerFactory.create(context!!).disableAppIfMissingRequiredSplits()) {
      crashReporter.report(Error("Install is missing required splits. App disabled."))
      return false
    }
    FacebookLoginHolder.facebookLogin(facebookLoginImpl)
    SmsLoginHolder.smsLogin(smsLoginImpl)
    AccountManagementHolder.accountManagement(accountManagerImpl)
    LoggedInCheckHolder.loggedInCheck(accountManagerImpl)
    GetRecommendationHolder.getRecommendation(getRecommendationImpl)
    LikeRecommendationHolder.likeRecommendation(likeRecommendationImpl)
    DislikeRecommendationHolder.dislikeRecommendation(dislikeRecommendationImpl)
    AlarmHolder.alarmManager(alarmManagerImpl)
    AutoSwipeHolder.autoSwipeIntentFactory(autoSwipeIntentFactoryImpl)
    LogoutHolder.alarmManager(alarmManagerImpl)
    LogoutHolder.autoswipeDestructor(autoswipeServiceDestructor)
    LogoutHolder.removeAccount(accountManagerImpl)
    LogoutHolder.storageClear(storageClearImpl)
    SeenRecommendationsHolder.seenRecommendations(seenRecommendations)
    GetProfileHolder.getProfile(getProfileImpl)
    MatchedRecommendationsHolder.matchedRecommendations(matchedRecommendations)
    SeenRecommendationHolder.seenRecommendation(seenRecommendation)
    return true
  }

  override fun insert(uri: Uri, values: ContentValues?) = null
  override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                     selectionArgs: Array<out String>?, sortOrder: String?) = null

  override fun update(uri: Uri, values: ContentValues?, selection: String?,
                      selectionArgs: Array<out String>?) = 0

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
  override fun getType(uri: Uri) = "vnd.android.cursor.item.none"
}
