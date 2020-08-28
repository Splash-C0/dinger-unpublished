package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKitError;

interface UIManagerStub extends Parcelable {
  @Nullable
  Fragment getBodyFragment(LoginFlowState var1);

  @Nullable
  ButtonType getButtonType(LoginFlowState var1);

  @Nullable
  Fragment getFooterFragment(LoginFlowState var1);

  @Nullable
  Fragment getHeaderFragment(LoginFlowState var1);

  @Nullable
  TextPosition getTextPosition(LoginFlowState var1);

  void onError(AccountKitError var1);
}
