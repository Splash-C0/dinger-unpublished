package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
internal class RecommendationUserTeaserEntity(
    @PrimaryKey
    var id: String,
    var description: String,
    var type: String?) {
  companion object {
    fun createId(description: String, type: String?) = "${description}_$type"
  }
}
