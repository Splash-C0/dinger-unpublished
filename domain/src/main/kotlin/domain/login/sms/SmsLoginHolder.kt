package domain.login.sms

object SmsLoginHolder {
  lateinit var smsLogin: SmsLogin

  fun smsLogin(it: SmsLogin) {
    smsLogin = it
  }
}
