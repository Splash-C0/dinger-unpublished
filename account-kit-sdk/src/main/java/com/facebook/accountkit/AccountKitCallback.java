package com.facebook.accountkit;

public interface AccountKitCallback<RESULT> {
  void onSuccess(RESULT var1);

  void onError(AccountKitError var1);
}
