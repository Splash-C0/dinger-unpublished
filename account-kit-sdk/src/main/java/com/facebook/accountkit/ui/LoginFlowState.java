package com.facebook.accountkit.ui;

public enum LoginFlowState {
  NONE,
  PHONE_NUMBER_INPUT,
  EMAIL_INPUT,
  SENDING_CODE,
  SENT_CODE,
  CODE_INPUT,
  ACCOUNT_VERIFIED,
  CONFIRM_INSTANT_VERIFICATION_LOGIN,
  CONFIRM_ACCOUNT_VERIFIED,
  EMAIL_VERIFY,
  VERIFYING_CODE,
  VERIFIED,
  RESEND,
  ERROR;

  private LoginFlowState() {
  }

  static LoginFlowState getBackState(LoginFlowState fromState) {
    LoginFlowState toState;
    switch (fromState) {
      case SENDING_CODE:
      case SENT_CODE:
      case CODE_INPUT:
      case ACCOUNT_VERIFIED:
      case CONFIRM_ACCOUNT_VERIFIED:
      case CONFIRM_INSTANT_VERIFICATION_LOGIN:
        toState = PHONE_NUMBER_INPUT;
        break;
      case EMAIL_VERIFY:
        toState = EMAIL_INPUT;
        break;
      case RESEND:
        toState = CODE_INPUT;
        break;
      case NONE:
      case PHONE_NUMBER_INPUT:
      case EMAIL_INPUT:
      case VERIFIED:
      case VERIFYING_CODE:
      case ERROR:
      default:
        toState = NONE;
    }

    return toState;
  }
}
