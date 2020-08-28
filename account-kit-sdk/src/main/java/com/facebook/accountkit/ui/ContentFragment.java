package com.facebook.accountkit.ui;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;

abstract class ContentFragment extends LoginFragment {
  ContentFragment() {
  }

  abstract LoginFlowState getLoginFlowState();

  abstract boolean isKeyboardFragment();

  @Nullable
  protected GoogleApiClient getGoogleApiClient() {
    Activity activity = this.getActivity();
    if (activity != null && activity instanceof AccountKitActivity) {
      AccountKitActivity accountKitActivity = (AccountKitActivity) activity;
      return accountKitActivity.getGoogleApiClient();
    } else {
      return null;
    }
  }

  @Nullable
  protected LoginFlowState getCurrentState() {
    Activity activity = this.getActivity();
    if (activity != null && activity instanceof AccountKitActivity) {
      AccountKitActivity accountKitActivity = (AccountKitActivity) activity;
      return accountKitActivity.getCurrentState();
    } else {
      return null;
    }
  }
}
