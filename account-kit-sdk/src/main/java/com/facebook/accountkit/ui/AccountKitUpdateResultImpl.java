package com.facebook.accountkit.ui;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitUpdateResult;

class AccountKitUpdateResultImpl implements AccountKitUpdateResult {
  public static final Creator<AccountKitUpdateResultImpl> CREATOR = new Creator<AccountKitUpdateResultImpl>() {
    public AccountKitUpdateResultImpl createFromParcel(Parcel source) {
      return new AccountKitUpdateResultImpl(source);
    }

    public AccountKitUpdateResultImpl[] newArray(int size) {
      return new AccountKitUpdateResultImpl[size];
    }
  };
  private final boolean cancelled;
  @Nullable
  private final AccountKitError error;
  private final String finalAuthorizationState;

  public AccountKitUpdateResultImpl(String finalAuthorizationState, AccountKitError error, boolean cancelled) {
    this.cancelled = cancelled;
    this.error = error;
    this.finalAuthorizationState = finalAuthorizationState;
  }

  private AccountKitUpdateResultImpl(Parcel parcel) {
    this.finalAuthorizationState = parcel.readString();
    this.error = (AccountKitError) parcel.readParcelable(AccountKitError.class.getClassLoader());
    this.cancelled = parcel.readByte() == 1;
  }

  @Nullable
  public String getFinalAuthorizationState() {
    return this.finalAuthorizationState;
  }

  @Nullable
  public AccountKitError getError() {
    return this.error;
  }

  public boolean wasCancelled() {
    return this.error == null;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.finalAuthorizationState);
    dest.writeParcelable(this.error, flags);
    dest.writeByte((byte) (this.cancelled ? 1 : 0));
  }
}
