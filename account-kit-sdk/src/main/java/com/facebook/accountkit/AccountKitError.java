package com.facebook.accountkit;

import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.accountkit.internal.InternalAccountKitError;

public final class AccountKitError implements Parcelable {
  public static final Creator<AccountKitError> CREATOR = new Creator<AccountKitError>() {
    public AccountKitError createFromParcel(Parcel source) {
      return new AccountKitError(source);
    }

    public AccountKitError[] newArray(int size) {
      return new AccountKitError[size];
    }
  };
  private final AccountKitError.Type errorType;
  private final InternalAccountKitError internalError;

  public AccountKitError(AccountKitError.Type errorType) {
    this((AccountKitError.Type) errorType, (InternalAccountKitError) null);
  }

  public AccountKitError(AccountKitError.Type errorType, InternalAccountKitError internalError) {
    this.errorType = errorType;
    this.internalError = internalError;
  }

  private AccountKitError(Parcel parcel) {
    this.errorType = AccountKitError.Type.values()[parcel.readInt()];
    this.internalError = (InternalAccountKitError) parcel.readParcelable(InternalAccountKitError.class.getClassLoader());
  }

  public int getDetailErrorCode() {
    return this.internalError == null ? -1 : this.internalError.getCode();
  }

  public AccountKitError.Type getErrorType() {
    return this.errorType;
  }

  public String getUserFacingMessage() {
    return this.internalError == null ? null : this.internalError.getUserFacingMessage();
  }

  public String toString() {
    return this.errorType + ": " + this.internalError;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.errorType.ordinal());
    dest.writeParcelable(this.internalError, flags);
  }

  public int describeContents() {
    return 0;
  }

  public static enum Type {
    NETWORK_CONNECTION_ERROR(100, "A request failed due to a network error"),
    SERVER_ERROR(200, "Server generated an error"),
    LOGIN_INVALIDATED(300, "The request timed out"),
    INTERNAL_ERROR(400, "An internal consistency error has occurred"),
    INITIALIZATION_ERROR(500, "Initialization error"),
    ARGUMENT_ERROR(600, "Invalid argument provided"),
    UPDATE_INVALIDATED(700, "The update request timed out");

    private final int code;
    private final String message;

    private Type(int code, String message) {
      this.code = code;
      this.message = message;
    }

    public String getMessage() {
      return this.message;
    }

    public int getCode() {
      return this.code;
    }

    public String toString() {
      return this.code + ": " + this.message;
    }
  }
}
