package com.facebook.accountkit.internal;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;

final class AccountKitServiceException extends AccountKitException {
  private static final long serialVersionUID = 1L;
  private final AccountKitRequestError error;

  public AccountKitServiceException(AccountKitRequestError error, AccountKitError.Type accountKitErrorType, InternalAccountKitError internalError) {
    super(accountKitErrorType, internalError);
    this.error = error;
  }

  public AccountKitServiceException(AccountKitRequestError error, AccountKitException ex) {
    super(ex.getError());
    this.error = error;
  }

  public final AccountKitRequestError getRequestError() {
    return this.error;
  }

  public final String toString() {
    return "{AccountKitServiceException: httpResponseCode: " + this.error.getRequestStatusCode() + ", errorCode: " + this.error.getErrorCode() + ", errorType: " + this.error.getErrorType() + ", message: " + this.error.getErrorMessage() + "}";
  }
}
