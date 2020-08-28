package domain.profile

data class DomainProfilePhoto(
    val id: String,
    val url: String,
    val fileName: String,
    val extension: String,
    val fbId: String?,
    val main: Boolean,
    val selectRate: Float,
    val processedFiles: Iterable<DomainProfileProcessedPhoto>,
    val xDistancePercent: Double?,
    val xOffsetPercent: Double?,
    val yDistancePercent: Double?,
    val yOffsetPercent: Double?,
    val shape: String?)
