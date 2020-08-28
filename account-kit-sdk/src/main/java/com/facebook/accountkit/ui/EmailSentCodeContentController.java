package com.facebook.accountkit.ui;

import com.facebook.accountkit.internal.AccountKitController;

final class EmailSentCodeContentController extends SentCodeContentController {
  EmailSentCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  protected void logImpression() {
    AccountKitController.Logger.logUISentCode(true, LoginType.EMAIL);
  }
}
