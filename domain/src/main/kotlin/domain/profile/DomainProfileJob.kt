package domain.profile

data class DomainProfileJob(val title: DomainProfileJobTitle?) {
  companion object {
    val NONE = DomainProfileJob(null)
  }
}
