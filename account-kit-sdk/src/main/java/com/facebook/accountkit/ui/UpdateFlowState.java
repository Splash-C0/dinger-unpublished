package com.facebook.accountkit.ui;

public enum UpdateFlowState {
  NONE,
  PHONE_NUMBER_INPUT,
  SENDING_CODE,
  SENT_CODE,
  CODE_INPUT,
  VERIFYING_CODE,
  VERIFIED,
  CODE_INPUT_ERROR,
  PHONE_NUMBER_INPUT_ERROR;

  private UpdateFlowState() {
  }

  static UpdateFlowState getBackState(UpdateFlowState fromState) {
    UpdateFlowState toState;
    switch (fromState) {
      case SENDING_CODE:
      case SENT_CODE:
      case CODE_INPUT:
      case PHONE_NUMBER_INPUT_ERROR:
        toState = PHONE_NUMBER_INPUT;
        break;
      case VERIFYING_CODE:
      case CODE_INPUT_ERROR:
      case NONE:
      case PHONE_NUMBER_INPUT:
      case VERIFIED:
      default:
        toState = NONE;
    }

    return toState;
  }
}
