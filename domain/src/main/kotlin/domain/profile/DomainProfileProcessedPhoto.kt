package domain.profile

import androidx.annotation.Px

data class DomainProfileProcessedPhoto(
    @Px
    val widthPx: Int,
    val url: String,
    @Px
    val heightPx: Int)
