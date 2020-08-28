package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
internal class RecommendationLikeEntity(
    @PrimaryKey
    var id: String,
    var name: String)
