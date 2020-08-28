package com.facebook.accountkit.ui;

final class ActivityErrorHandler {
  private ActivityErrorHandler() {
  }

  static void onErrorRestart(AccountKitActivity activity, LoginFlowState returnState) {
    ContentController contentController = activity.getContentController();
    if (contentController != null && contentController instanceof LoginErrorContentController) {
      activity.onContentControllerDismissed(contentController);
    }

    activity.popBackStack(returnState, (StateStackManager.OnPopListener) null);
  }
}
