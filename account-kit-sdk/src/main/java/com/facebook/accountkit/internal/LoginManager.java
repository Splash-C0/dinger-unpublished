package com.facebook.accountkit.internal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.LoginModel;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.NotificationChannel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

final class LoginManager {
  private static final String TAG = LoginManager.class.getName();
  private static final String LOGOUT_PATH = "logout/";
  private static final String SAVED_LOGIN_MODEL = "accountkitLoginModel";
  private final AccessTokenManager accessTokenManager;
  private final LocalBroadcastManager localBroadcastManager;
  private final InternalLogger logger;
  private volatile Activity currentActivity;
  @Nullable
  private volatile LoginController currentLoginController;
  private volatile boolean isActivityAvailable = false;
  private String requestInstanceToken;
  private SeamlessLoginClient seamlessLoginClient;
  private String seamlessLoginToken;
  private long seamlessLoginExpirationMillis;

  LoginManager(InternalLogger internalLogger, AccessTokenManager accessTokenManager, @NonNull LocalBroadcastManager localBroadcastManager) {
    this.accessTokenManager = accessTokenManager;
    this.localBroadcastManager = localBroadcastManager;
    this.logger = internalLogger;
    this.resetRequestInstanceToken();
  }

  void continueWithCode(String code) {
    PhoneLoginModelImpl loginModel = this.getCurrentPhoneNumberLogInModel();
    if (loginModel != null) {
      try {
        loginModel.setConfirmationCode(code);
        this.handle(loginModel);
      } catch (AccountKitException var4) {
        if (Utility.isDebuggable(AccountKitController.getApplicationContext())) {
          throw var4;
        }

        this.logger.logLoginModel("ak_confirmation_code_set", loginModel);
      }

    }
  }

  void continueSeamlessLogin() {
    LoginModelImpl loginModel = this.getCurrentLogInModel();
    if (loginModel != null) {
      try {
        this.handle(loginModel);
      } catch (AccountKitException var3) {
        if (Utility.isDebuggable(AccountKitController.getApplicationContext())) {
          throw var3;
        }

        this.logger.logLoginModel("ak_seamless_pending", loginModel);
      }

    }
  }

  void onActivityCreate(Activity activity, Bundle savedInstanceState) {
    this.isActivityAvailable = true;
    this.currentActivity = activity;
    this.logger.onActivityCreate(savedInstanceState);
    if (savedInstanceState != null) {
      LoginModelImpl loginModel = (LoginModelImpl) savedInstanceState.getParcelable("accountkitLoginModel");
      if (loginModel != null) {
        this.startWith(loginModel);
      }
    }

  }

  void onActivityDestroy(Activity activity) {
    if (this.currentActivity == activity) {
      this.isActivityAvailable = false;
      this.currentLoginController = null;
      this.currentActivity = null;
      AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask((AccountKitGraphRequestAsyncTask) null);
    }
  }

  void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    if (this.currentActivity == activity) {
      this.logger.saveInstanceState(outState);
      if (this.currentLoginController != null) {
        outState.putParcelable("accountkitLoginModel", this.currentLoginController.getLoginModel());
      }

    }
  }

  InternalLogger getLogger() {
    return this.logger;
  }

  void handle(LoginModelImpl loginModel) {
    if (this.currentLoginController != null) {
      Validate.loginModelsEqual(loginModel, this.currentLoginController.getLoginModel());
      Utility.assertUIThread();
      switch (loginModel.getStatus()) {
        case PENDING:
          this.currentLoginController.onPending();
          break;
        case ACCOUNT_VERIFIED:
          this.currentLoginController.onAccountVerified();
          break;
        case ERROR:
          this.currentLoginController.onError(loginModel.getError());
          break;
        case CANCELLED:
          this.currentLoginController.onCancel();
        case SUCCESS:
        case EMPTY:
      }

    }
  }

  private void onLoginStart(LoginModelImpl loginModel) {
    this.logger.logLoginModel("ak_login_start", loginModel);
  }

  void onLoginVerify(LoginModelImpl loginModel) {
    this.logger.logLoginModel("ak_login_verify", loginModel);
  }

  void onSeamlessLoginPending(LoginModelImpl loginModel) {
    this.logger.logLoginModel("ak_seamless_pending", loginModel);
  }

  void onLoginComplete(LoginModelImpl loginModel) {
    this.logger.logLoginModel("ak_login_complete", loginModel);
  }

  EmailLoginModelImpl getCurrentEmailLogInModel() {
    if (this.currentLoginController == null) {
      return null;
    } else {
      LoginModelImpl loginModel = this.currentLoginController.getLoginModel();
      return !(loginModel instanceof EmailLoginModelImpl) ? null : (EmailLoginModelImpl) loginModel;
    }
  }

  PhoneLoginModelImpl getCurrentPhoneNumberLogInModel() {
    if (this.currentLoginController == null) {
      return null;
    } else {
      LoginModelImpl loginModel = this.currentLoginController.getLoginModel();
      return !(loginModel instanceof PhoneLoginModelImpl) ? null : (PhoneLoginModelImpl) loginModel;
    }
  }

  @Nullable
  private LoginModelImpl getCurrentLogInModel() {
    return this.currentLoginController == null ? null : this.currentLoginController.getLoginModel();
  }

  void cancelLogin() {
    Utility.assertUIThread();
    this.resetRequestInstanceToken();
    if (this.currentLoginController != null) {
      this.currentLoginController.onCancel();
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask((AccountKitGraphRequestAsyncTask) null);
      this.currentLoginController = null;
    }

    AccountKitGraphRequestAsyncTask currentAsyncTask = AccountKitGraphRequestAsyncTask.getCurrentAsyncTask();
    if (currentAsyncTask != null) {
      currentAsyncTask.cancel(true);
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask((AccountKitGraphRequestAsyncTask) null);
    }

  }

  String getRequestInstanceToken() {
    return this.requestInstanceToken;
  }

  void initializeLogin() {
    this.seamlessLoginToken = null;
    this.seamlessLoginClient = new SeamlessLoginClient(AccountKitController.getApplicationContext(), AccountKit.getApplicationId(), this.logger);
    if (this.seamlessLoginClient.start()) {
      SeamlessLoginClient.CompletedListener callback = new SeamlessLoginClient.CompletedListener() {
        public void completed(Bundle result) {
          LoginManager.this.seamlessLoginCompleted(result);
        }
      };
      this.seamlessLoginClient.setCompletedListener(callback);
    }
  }

  private void seamlessLoginCompleted(Bundle result) {
    if (result != null) {
      this.seamlessLoginExpirationMillis = result.getLong("com.facebook.platform.extra.EXPIRES_SECONDS_SINCE_EPOCH") * 1000L;
      this.seamlessLoginToken = result.getString("com.facebook.platform.extra.SEAMLESS_LOGIN_TOKEN");
    }

  }

  String getSeamlessLoginToken() {
    if (this.seamlessLoginExpirationMillis < System.currentTimeMillis()) {
      this.seamlessLoginToken = null;
    }

    return this.seamlessLoginToken;
  }

  String getSeamlessLoginTokenRegardlessTimeOut() {
    return this.seamlessLoginToken;
  }

  boolean isSeamlessLoginRunning() {
    return this.seamlessLoginToken == null && this.seamlessLoginClient != null && this.seamlessLoginClient.isRunning();
  }

  EmailLoginModelImpl logInWithEmail(@NonNull String email, @NonNull String responseType, @Nullable String initialAuthState) {
    Utility.assertUIThread();
    this.cancelExisting();
    EmailLoginModelImpl loginModel = new EmailLoginModelImpl(email, responseType);
    EmailLoginController loginHandler = new EmailLoginController(this.accessTokenManager, this, loginModel);
    loginHandler.logIn(initialAuthState);
    this.onLoginStart(loginModel);
    this.currentLoginController = loginHandler;
    return loginModel;
  }

  PhoneLoginModelImpl logInWithPhoneNumber(@NonNull PhoneNumber phoneNumber, @NonNull NotificationChannel notificationChannel, @NonNull String responseType, @Nullable String initialAuthState) {
    return this.logInWithPhoneNumber(phoneNumber, notificationChannel, responseType, initialAuthState, false);
  }

  PhoneLoginModelImpl logInWithPhoneNumber(@NonNull PhoneNumber phoneNumber, @NonNull NotificationChannel notificationChannel, @NonNull String responseType, @Nullable String initialAuthState, boolean testSmsWithInfobip) {
    Utility.assertUIThread();
    if (notificationChannel == NotificationChannel.SMS || notificationChannel == NotificationChannel.WHATSAPP) {
      this.cancelCurrentRequest();
    }

    PhoneLoginModelImpl loginModel = new PhoneLoginModelImpl(phoneNumber, notificationChannel, responseType);
    loginModel.setTestSmsWithInfobip(testSmsWithInfobip);
    PhoneLoginController loginHandler = new PhoneLoginController(this.accessTokenManager, this, loginModel);
    loginHandler.logIn(initialAuthState);
    this.onLoginStart(loginModel);
    this.currentLoginController = loginHandler;
    return loginModel;
  }

  void logOut() {
    this.logOut((AccountKitCallback) null);
    this.accessTokenManager.setCurrentAccessToken((AccessToken) null);
  }

  void logOut(@Nullable final AccountKitCallback<Void> callback) {
    AccessToken accessToken = AccountKit.getCurrentAccessToken();
    if (accessToken == null) {
      Log.w(TAG, "No access token: cannot log out");
      if (callback != null) {
        callback.onSuccess((Void) null);
      }

    } else {
      AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
        public void onCompleted(AccountKitGraphResponse response) {
          if (response.getError() != null) {
            Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
            if (callback != null) {
              callback.onError((AccountKitError) error.first);
            }
          } else {
            LoginManager.this.accessTokenManager.setCurrentAccessToken((AccessToken) null);
            if (callback != null) {
              callback.onSuccess((Void) null);
            }
          }

        }
      };
      AccountKitGraphRequest graphRequest = new AccountKitGraphRequest(accessToken, "logout/", (Bundle) null, false, HttpMethod.POST);
      AccountKitGraphRequest.executeAsync(graphRequest, requestCallback);
    }
  }

  void clearLogIn() {
    this.currentLoginController = null;
  }

  private void cancelExisting() {
    if (this.currentLoginController != null) {
      LoginModelImpl loginModel = this.currentLoginController.getLoginModel();
      loginModel.setStatus(LoginStatus.CANCELLED);
      this.currentLoginController.onCancel();
    }
  }

  boolean isActivityAvailable() {
    return this.isActivityAvailable;
  }

  LocalBroadcastManager getLocalBroadcastManager() {
    return this.localBroadcastManager;
  }

  boolean isLoginInProgress() {
    return this.currentLoginController != null;
  }

  private void cancelCurrentRequest() {
    this.currentLoginController = null;
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
    AccountKitGraphRequestAsyncTask.setCurrentAsyncTask((AccountKitGraphRequestAsyncTask) null);
  }

  void cancel(LoginModel loginModel) {
    this.seamlessLoginToken = null;
    if (this.currentLoginController != null) {
      if (Utility.areObjectsEqual(loginModel, this.currentLoginController.getLoginModel())) {
        this.cancelCurrentRequest();
      }
    }
  }

  private void startWith(@NonNull LoginModelImpl loginModel) {
    Utility.assertUIThread();
    if (loginModel instanceof EmailLoginModelImpl) {
      this.currentLoginController = new EmailLoginController(this.accessTokenManager, this, (EmailLoginModelImpl) loginModel);
    } else {
      if (!(loginModel instanceof PhoneLoginModelImpl)) {
        throw new AccountKitException(AccountKitError.Type.ARGUMENT_ERROR, InternalAccountKitError.INVALID_LOGIN_TYPE, loginModel.getClass().getName());
      }

      this.currentLoginController = new PhoneLoginController(this.accessTokenManager, this, (PhoneLoginModelImpl) loginModel);
    }

    this.handle(loginModel);
  }

  void getCurrentAccount(final AccountKitCallback<Account> callback) {
    final AccessToken accessToken = AccountKit.getCurrentAccessToken();
    if (accessToken == null) {
      Log.w(TAG, "No access token: cannot retrieve account");
      callback.onError(new AccountKitError(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.CANNOT_RETRIEVE_ACCESS_TOKEN_NO_ACCOUNT));
    } else {
      AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
        public void onCompleted(AccountKitGraphResponse response) {
          if (response.getError() != null) {
            Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
            callback.onError((AccountKitError) error.first);
          } else {
            JSONObject result = response.getResponseObject();
            if (result == null) {
              callback.onError(new AccountKitError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND));
            } else {
              try {
                String accountId = result.getString("id");
                JSONObject emailBundle = result.optJSONObject("email");
                String email = null;
                if (emailBundle != null) {
                  email = emailBundle.getString("address");
                }

                JSONObject phoneNumberBundle = result.optJSONObject("phone");
                String nationalPhoneNumber = null;
                String countryCode = null;
                if (phoneNumberBundle != null) {
                  nationalPhoneNumber = phoneNumberBundle.getString("national_number");
                  countryCode = phoneNumberBundle.getString("country_prefix");
                }

                if (countryCode == null && nationalPhoneNumber == null && email == null) {
                  callback.onError(new AccountKitError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_ACCOUNT_FOUND));
                  return;
                }

                if (countryCode == null && nationalPhoneNumber != null || countryCode != null && nationalPhoneNumber == null) {
                  callback.onError(new AccountKitError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_ACCOUNT_FOUND));
                  return;
                }

                PhoneNumber phoneNumber = null;
                if (countryCode != null) {
                  phoneNumber = new PhoneNumber(countryCode, nationalPhoneNumber, (String) null);
                }

                AccessToken currentAccessToken = AccountKit.getCurrentAccessToken();
                if (currentAccessToken != null && accessToken.equals(currentAccessToken)) {
                  LoginManager.this.accessTokenManager.refreshCurrentAccessToken(currentAccessToken);
                }

                Account account = new Account(accountId, phoneNumber, email);
                callback.onSuccess(account);
              } catch (JSONException var12) {
                callback.onError(new AccountKitError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT));
              }

            }
          }
        }
      };
      AccountKitGraphRequest graphRequest = new AccountKitGraphRequest(accessToken, accessToken.getAccountId(), (Bundle) null, false, HttpMethod.GET);
      AccountKitGraphRequest.executeAsync(graphRequest, requestCallback);
    }
  }

  private void resetRequestInstanceToken() {
    this.requestInstanceToken = UUID.randomUUID().toString();
  }
}
