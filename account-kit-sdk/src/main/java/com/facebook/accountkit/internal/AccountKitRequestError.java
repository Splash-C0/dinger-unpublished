package com.facebook.accountkit.internal;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;

final class AccountKitRequestError {
  public static final int INVALID_ERROR_CODE = -1;
  public static final int INVALID_HTTP_STATUS_CODE = -1;
  private final int errorCode;
  private final int subErrorCode;
  private final String errorType;
  private final String errorMessage;
  private final String userErrorMessage;
  private final AccountKitException exception;
  private final int requestStatusCode;

  public AccountKitRequestError(int requestStatusCode, int errorCode, int subErrorCode, String errorType, String errorMessage, String userErrorMessage, AccountKitException exception) {
    this.requestStatusCode = requestStatusCode;
    this.errorCode = errorCode;
    this.errorType = errorType;
    this.errorMessage = errorMessage;
    this.subErrorCode = subErrorCode;
    this.userErrorMessage = userErrorMessage;
    if (exception != null) {
      this.exception = new AccountKitServiceException(this, exception);
    } else {
      this.exception = new AccountKitServiceException(this, AccountKitError.Type.SERVER_ERROR, new InternalAccountKitError(errorCode, errorMessage));
    }

  }

  public AccountKitRequestError(AccountKitException exception) {
    this(-1, exception.getError().getDetailErrorCode(), -1, (String) null, (String) null, (String) null, exception);
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public String getErrorMessage() {
    return this.errorMessage != null ? this.errorMessage : this.exception.getLocalizedMessage();
  }

  public String getErrorType() {
    return this.errorType;
  }

  public AccountKitException getException() {
    return this.exception;
  }

  public int getRequestStatusCode() {
    return this.requestStatusCode;
  }

  public int getSubErrorCode() {
    return this.subErrorCode;
  }

  public String getUserErrorMessage() {
    return this.userErrorMessage;
  }

  public String toString() {
    return "{HttpStatus: " + this.requestStatusCode + ", errorCode: " + this.errorCode + ", errorType: " + this.errorType + ", errorMessage: " + this.getErrorMessage() + "}";
  }
}
