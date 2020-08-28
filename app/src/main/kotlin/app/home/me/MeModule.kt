package app.home.me

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import app.crash.CrashReporterModule
import app.home.HomeScreenScope
import app.home.me.profile.GetProfileCoordinator
import app.home.me.profile.ProfileView
import app.home.me.profile.ProfileViewImpl
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import org.stoyicker.dinger.R
import reporter.CrashReporter
import javax.inject.Named
import javax.inject.Qualifier

@Module(includes = [CrashReporterModule::class])
internal class MeModule {
  @Provides
  @HomeScreenScope
  fun profileImage(root: View) = root.findViewById<ImageView>(R.id.image_profile)!!

  @Provides
  @HomeScreenScope
  @TitleLabel
  fun titleLabel(root: View) = root.findViewById<TextView>(R.id.label_title)!!

  @Provides
  @HomeScreenScope
  @OccupationLabel
  fun occupationLabel(root: View) = root.findViewById<TextView>(R.id.label_occupation)!!

  @Provides
  @HomeScreenScope
  @LocationLabel
  fun locationLabel(root: View) = root.findViewById<TextView>(R.id.label_location)!!

  @Provides
  @HomeScreenScope
  @BioLabel
  fun bioLabel(root: View) = root.findViewById<TextView>(R.id.label_bio)!!

  @Provides
  @HomeScreenScope
  @DistanceFilterLabel
  fun distanceFilterLabel(root: View) = root.findViewById<TextView>(R.id.label_distance_filter)!!

  @Provides
  @HomeScreenScope
  @DistanceFilter
  fun distanceFilter(root: View) = root.findViewById<ProgressBar>(R.id.distance_filter)!!

  @Provides
  @HomeScreenScope
  @AgeFilterLabel
  fun ageFilterLabel(root: View) = root.findViewById<TextView>(R.id.label_age_filter)!!

  @Provides
  @HomeScreenScope
  @AgeFilterMin
  fun ageFilterMin(root: View) = root.findViewById<ProgressBar>(R.id.age_filter_min)!!

  @Provides
  @HomeScreenScope
  @AgeFilterMax
  fun ageFilterMax(root: View) = root.findViewById<ProgressBar>(R.id.age_filter_max)!!

  @Provides
  @HomeScreenScope
  @Progress
  fun progress(root: View) = root.findViewById<ContentLoadingProgressBar>(R.id.progress)!!

  @Provides
  @HomeScreenScope
  fun profileView(
      profileImage: ImageView,
      @TitleLabel titleLabel: TextView,
      @OccupationLabel occupationLabel: TextView,
      @LocationLabel locationLabel: TextView,
      @BioLabel bioLabel: TextView,
      @DistanceFilterLabel distanceFilterLabel: TextView,
      @DistanceFilter distanceFilter: ProgressBar,
      @AgeFilterLabel ageFilterLabel: TextView,
      @AgeFilterMin ageFilterMin: ProgressBar,
      @AgeFilterMax ageFilterMax: ProgressBar,
      @Progress progress: ContentLoadingProgressBar): ProfileView = ProfileViewImpl(
      profileImage,
      titleLabel,
      occupationLabel,
      locationLabel,
      bioLabel,
      distanceFilterLabel,
      distanceFilter,
      ageFilterLabel,
      ageFilterMin,
      ageFilterMax,
      progress)

  @Provides
  @HomeScreenScope
  fun getProfileCoordinator(
      view: ProfileView,
      @Named("io") asyncExecutionScheduler: Scheduler,
      @Named("main") postExecutionScheduler: Scheduler,
      crashReporter: CrashReporter) =
      GetProfileCoordinator(view, asyncExecutionScheduler, postExecutionScheduler, crashReporter)
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class TitleLabel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class OccupationLabel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class LocationLabel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class BioLabel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class DistanceFilterLabel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class DistanceFilter

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class AgeFilterLabel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class Progress

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class AgeFilterMin

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class AgeFilterMax
