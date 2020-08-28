package app.entryscreen.login.sms

internal interface TinderSmsVerifyOneTimePasswordView {
  fun setRunning()

  fun setStale()

  fun setError(error: Throwable)

  fun setOtpLength(otpLength: Int)

  fun setOtpViewListener(f: (String) -> Unit)
}
