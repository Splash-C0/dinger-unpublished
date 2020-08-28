package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserJob private constructor(
    @field:Json(name = "company")
    val company: RecommendationUserJobCompany?,
    @field:Json(name = "title")
    val title: RecommendationUserJobTitle?) {
  companion object {
    fun createId(company: RecommendationUserJobCompany?, title: RecommendationUserJobTitle?) =
        "${company?.name}_${title?.name}"
  }
}
