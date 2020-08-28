package data.tinder.updates

import com.squareup.moshi.Json

internal class UpdateRequest(
    @field:Json(name = "last_activity_date") val lastActivityDate: String = BEGINNING_OF_TIME)

private const val BEGINNING_OF_TIME = "1970-01-01T00:00:00Z"
