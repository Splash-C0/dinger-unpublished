package com.facebook.accountkit;

import com.facebook.accountkit.internal.InternalAccountKitError;

public class AccountKitException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final AccountKitError error;

  public AccountKitException(AccountKitError error) {
    super(error.getErrorType().getMessage());
    this.error = error;
  }

  public AccountKitException(AccountKitError.Type errorType, InternalAccountKitError internalError) {
    super(errorType.getMessage());
    this.error = new AccountKitError(errorType, internalError);
  }

  public AccountKitException(AccountKitError.Type errorType, InternalAccountKitError internalError, String message) {
    super(String.format(errorType.getMessage(), message));
    this.error = new AccountKitError(errorType, internalError);
  }

  public AccountKitException(AccountKitError.Type errorType, InternalAccountKitError internalError, Throwable throwable) {
    super(errorType.getMessage(), throwable);
    this.error = new AccountKitError(errorType, internalError);
  }

  public AccountKitException(AccountKitError.Type errorType, Throwable throwable) {
    super(errorType.getMessage(), throwable);
    this.error = new AccountKitError(errorType);
  }

  public AccountKitError getError() {
    return this.error;
  }

  public String toString() {
    return this.error.toString();
  }
}
