package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
internal open class RecommendationUserCommonFriendEntity(
    @PrimaryKey
    var id: String,
    var name: String,
    var degree: String) {
  companion object {
    val NONE = RecommendationUserCommonFriendEntity(id = "", name = "", degree = "")
  }
}
