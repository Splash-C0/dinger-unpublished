package com.facebook.accountkit.internal;

import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;

final class Validate {
  private static final String NO_INTERNET_PERMISSION_REASON = "No internet permissions granted for the app, please add <uses-permission android:name=\"android.permission.INTERNET\" /> to your AndroidManifest.xml.";

  Validate() {
  }

  static void checkInternetPermissionAndThrow(@NonNull Context context) {
    if (context.checkCallingOrSelfPermission("android.permission.INTERNET") == -1) {
      throw new IllegalStateException("No internet permissions granted for the app, please add <uses-permission android:name=\"android.permission.INTERNET\" /> to your AndroidManifest.xml.");
    }
  }

  static void sdkInitialized() {
    if (!AccountKit.isInitialized()) {
      throw new AccountKitException(AccountKitError.Type.INITIALIZATION_ERROR, InternalAccountKitError.SDK_NOT_INITIALIZED);
    }
  }

  static void loginModelInProgress(LoginModelImpl login) {
    if (login == null) {
      throw new AccountKitException(AccountKitError.Type.ARGUMENT_ERROR, InternalAccountKitError.NO_LOGIN_ATTEMPT_IN_PROGRESS);
    }
  }

  static void loginModelsEqual(LoginModelImpl login1, LoginModelImpl login2) {
    if (Utility.notEquals(login1, login2)) {
      throw new AccountKitException(AccountKitError.Type.ARGUMENT_ERROR, InternalAccountKitError.DIFFERENT_LOGIN_ATTEMPT_IN_PROGRESS);
    }
  }

  static void isEquals(Object o1, Object o2, String type) {
    if (Utility.notEquals(o1, o2)) {
      throw new AccountKitException(AccountKitError.Type.ARGUMENT_ERROR, InternalAccountKitError.NOT_EQUAL_OBJECTS, type);
    }
  }
}
