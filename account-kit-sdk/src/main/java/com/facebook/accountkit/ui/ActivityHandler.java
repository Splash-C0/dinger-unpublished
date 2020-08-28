package com.facebook.accountkit.ui;

import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.accountkit.Tracker;

public abstract class ActivityHandler implements Parcelable {
  protected static final long COMPLETION_UI_DURATION_MS = 2000L;
  protected final AccountKitConfiguration configuration;
  protected Tracker tracker;

  ActivityHandler(AccountKitConfiguration configuration) {
    this.configuration = configuration;
  }

  protected ActivityHandler(Parcel parcel) {
    this.configuration = (AccountKitConfiguration) parcel.readParcelable(AccountKitConfiguration.class.getClassLoader());
  }

  public abstract Tracker getLoginTracker(AccountKitActivity var1);

  public abstract void onSentCodeComplete(AccountKitActivity var1);

  public abstract void onAccountVerifiedComplete(AccountKitActivity var1);

  void onConfirmSeamlessLogin(AccountKitActivity activity, LoginFlowManager loginFlowManager) {
    activity.pushState(LoginFlowState.CONFIRM_INSTANT_VERIFICATION_LOGIN, (StateStackManager.OnPushListener) null);
    loginFlowManager.confirmSeamlessLogin();
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.configuration, flags);
  }
}
