package tracker

internal enum class Event(val key: String) {
  RESPONSE_RECOMMENDATION("response_recommendation"),
  RESPONSE_LIKE("response_like"),
  USER_ACCOUNT_PROVIDED("user_account_provided")
}
