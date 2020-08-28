package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
internal class RecommendationUserPhotoEntity(
    @PrimaryKey
    var id: String,
    var url: String) {
  companion object {
    val NONE = RecommendationUserPhotoEntity(id = "", url = "")
  }
}
