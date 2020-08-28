package com.facebook.accountkit;

import android.content.Intent;

import com.facebook.accountkit.internal.LoginStatus;

import java.util.Collections;
import java.util.List;

public abstract class EmailLoginTracker extends Tracker {
  public static final String ACTION_EMAIL_LOGIN_STATE_CHANGED = "com.facebook.accountkit.sdk.ACTION_EMAIL_LOGIN_STATE_CHANGED";

  public EmailLoginTracker() {
  }

  protected abstract void onStarted(EmailLoginModel var1);

  protected abstract void onSuccess(EmailLoginModel var1);

  protected abstract void onError(AccountKitException var1);

  protected abstract void onCancel(EmailLoginModel var1);

  protected List<String> getActionsStateChanged() {
    return Collections.singletonList("com.facebook.accountkit.sdk.ACTION_EMAIL_LOGIN_STATE_CHANGED");
  }

  protected abstract void onAccountVerified(EmailLoginModel var1);

  protected void onReceive(Intent intent) {
    EmailLoginModel loginModel = (EmailLoginModel) intent.getParcelableExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_MODEL");
    LoginStatus status = (LoginStatus) intent.getSerializableExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_STATUS");
    if (loginModel != null && status != null) {
      switch (status) {
        case PENDING:
          this.onStarted(loginModel);
          break;
        case ACCOUNT_VERIFIED:
          this.onAccountVerified(loginModel);
          break;
        case SUCCESS:
          this.onSuccess(loginModel);
          break;
        case CANCELLED:
          this.onCancel(loginModel);
          break;
        case ERROR:
          AccountKitError error = (AccountKitError) intent.getParcelableExtra("com.facebook.accountkit.sdk.EXTRA_LOGIN_ERROR");
          if (error != null) {
            this.onError(new AccountKitException(error));
          }
      }

    }
  }
}
