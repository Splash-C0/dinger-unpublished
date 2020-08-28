package data.tinder.recommendation

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(Recommendation.GENDER_MALE, Recommendation.GENDER_FEMALE)
annotation class Gender
