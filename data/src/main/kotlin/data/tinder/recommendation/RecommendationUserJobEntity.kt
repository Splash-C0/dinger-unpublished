package data.tinder.recommendation

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
internal class RecommendationUserJobEntity(
    @PrimaryKey
    var id: String,
    @Embedded(prefix = "company_")
    var company: RecommendationUserJobCompany?,
    @Embedded(prefix = "title_")
    var title: RecommendationUserJobTitle?)
