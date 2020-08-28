package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal class RecommendationUserSchoolEntity(
    @PrimaryKey
    var name: String,
    var id: String?)
