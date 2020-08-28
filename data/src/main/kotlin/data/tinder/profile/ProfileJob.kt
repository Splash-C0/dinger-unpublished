package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfileJob private constructor(
    @field:Json(name = "title")
    val title: ProfileJobTitle?)
