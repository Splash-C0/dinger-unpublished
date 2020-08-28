package com.facebook.accountkit.ui;

import com.facebook.accountkit.internal.AccountKitController;

final class PhoneSentCodeContentController extends SentCodeContentController {
  PhoneSentCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  protected void logImpression() {
    AccountKitController.Logger.logUISentCode(true, LoginType.PHONE);
  }
}
