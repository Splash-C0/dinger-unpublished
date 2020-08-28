package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfilePhoto private constructor(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "url")
    val url: String,
    @field:Json(name = "fileName")
    val fileName: String,
    @field:Json(name = "extension")
    val extension: String,
    @field:Json(name = "fbId")
    val fbId: String?,
    @field:Json(name = "main")
    val main: Boolean,
    @field:Json(name = "selectRate")
    val selectRate: Float,
    @field:Json(name = "processed_files")
    val processedFiles: Array<ProfileProcessedPhoto>?,
    @field:Json(name = "x_distance_percent")
    val xDistancePercent: Double?,
    @field:Json(name = "x_offset_percent")
    val xOffsetPercent: Double?,
    @field:Json(name = "y_distance_percent")
    val yDistancePercent: Double?,
    @field:Json(name = "y_offset_percent")
    val yOffsetPercent: Double?,
    @field:Json(name = "shape")
    val shape: String?)
