package com.facebook.accountkit;

import android.os.Parcelable;

import androidx.annotation.Nullable;

public interface AccountKitUpdateResult extends Parcelable {
  String RESULT_KEY = "account_kit_update_result";

  @Nullable
  AccountKitError getError();

  @Nullable
  String getFinalAuthorizationState();

  boolean wasCancelled();

  public static enum UpdateResult {
    SUCCESS,
    CANCELLED;

    private UpdateResult() {
    }
  }
}
