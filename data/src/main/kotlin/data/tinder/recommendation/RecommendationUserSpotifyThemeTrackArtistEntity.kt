package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
internal class RecommendationUserSpotifyThemeTrackArtistEntity(
    var name: String,
    @PrimaryKey
    var id: String)
