package com.facebook.accountkit.internal;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

final class EmailLoginController extends LoginController<EmailLoginModelImpl> {
  private static final int SECONDS_TO_MILLIS = 1000;
  private static final String PARAMETER_EMAIL = "email";
  private static final String TAG = EmailLoginController.class.getName();

  EmailLoginController(AccessTokenManager accessTokenManager, LoginManager loginManager, EmailLoginModelImpl loginModel) {
    super(accessTokenManager, loginManager, loginModel);
  }

  protected String getCredentialsType() {
    return "email";
  }

  protected String getLoginStateChangedIntentName() {
    return "com.facebook.accountkit.sdk.ACTION_EMAIL_LOGIN_STATE_CHANGED";
  }

  public void logIn(@Nullable String initialAuthState) {
    AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
      public void onCompleted(AccountKitGraphResponse response) {
        LoginManager loginManager = EmailLoginController.this.getLoginManager();
        if (loginManager != null) {
          try {
            if (response.getError() != null) {
              Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
              EmailLoginController.this.onError((AccountKitError) error.first);
              return;
            }

            JSONObject result = response.getResponseObject();
            if (result == null) {
              EmailLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND);
              return;
            }

            String privacyPolicy = result.optString("privacy_policy");
            if (!Utility.isNullOrEmpty(privacyPolicy)) {
              ((EmailLoginModelImpl) EmailLoginController.this.loginModel).putField("privacy_policy", privacyPolicy);
            }

            String termsOfService = result.optString("terms_of_service");
            if (!Utility.isNullOrEmpty(termsOfService)) {
              ((EmailLoginModelImpl) EmailLoginController.this.loginModel).putField("terms_of_service", termsOfService);
            }

            String expiresInString;
            long expiresIn;
            try {
              boolean canSkipCode = result.getBoolean("can_attempt_seamless_login");
              expiresInString = result.getString("expires_at");
              expiresIn = Long.parseLong(expiresInString) * 1000L;
              if (canSkipCode && expiresIn > System.currentTimeMillis()) {
                ((EmailLoginModelImpl) EmailLoginController.this.loginModel).setStatus(LoginStatus.ACCOUNT_VERIFIED);
                return;
              }
            } catch (JSONException var17) {
            }

            try {
              String loginAttemptCode = result.getString("login_request_code");
              ((EmailLoginModelImpl) EmailLoginController.this.loginModel).setLoginCode(loginAttemptCode);
              expiresInString = result.getString("expires_in_sec");
              expiresIn = Long.parseLong(expiresInString);
              ((EmailLoginModelImpl) EmailLoginController.this.loginModel).setExpiresInSeconds(expiresIn);
              String intervalSecondsString = result.getString("interval_sec");
              int intervalSeconds = Integer.parseInt(intervalSecondsString);
              ((EmailLoginModelImpl) EmailLoginController.this.loginModel).setInterval(intervalSeconds);
              ((EmailLoginModelImpl) EmailLoginController.this.loginModel).setStatus(LoginStatus.PENDING);
              loginManager.handle(EmailLoginController.this.loginModel);
            } catch (NumberFormatException | JSONException var16) {
              EmailLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT);
            }
          } finally {
            EmailLoginController.this.broadcastLoginStateChange();
          }

        }
      }
    };
    Bundle parameters = new Bundle();
    Utility.putNonNullString(parameters, "email", ((EmailLoginModelImpl) this.loginModel).getEmail());
    Utility.putNonNullString(parameters, "redirect_uri", Utility.getRedirectURL());
    Utility.putNonNullString(parameters, "state", initialAuthState);
    Utility.putNonNullString(parameters, "response_type", ((EmailLoginModelImpl) this.loginModel).getResponseType());
    Utility.putNonNullString(parameters, "fields", "terms_of_service,privacy_policy");
    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null && !loginManager.isSeamlessLoginRunning()) {
      Utility.putNonNullString(parameters, "fb_user_token", loginManager.getSeamlessLoginToken());
    }

    ((EmailLoginModelImpl) this.loginModel).setInitialAuthState(initialAuthState);
    AccountKitGraphRequest graphRequest = this.buildGraphRequest("start_login", parameters);
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
    AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(graphRequest, requestCallback);
    AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
  }

  public void onCancel() {
    ((EmailLoginModelImpl) this.loginModel).setStatus(LoginStatus.CANCELLED);
    this.broadcastLoginStateChange();
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
  }

  public void onPending() {
    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null) {
      if (loginManager.isActivityAvailable()) {
        EmailLoginController.LoginModelCodeCallback loginCallback = new EmailLoginController.LoginModelCodeCallback((EmailLoginModelImpl) this.loginModel);
        Runnable poll = this.createPolling((EmailLoginModelImpl) this.loginModel, loginCallback);
        if (poll != null) {
          (new Handler()).postDelayed(poll, (long) (((EmailLoginModelImpl) this.loginModel).getInterval() * 1000));
        }
      }
    }
  }

  public void onAccountVerified() {
    Validate.loginModelInProgress(this.loginModel);
    LoginManager loginManager = this.getLoginManager();
    if (loginManager != null) {
      loginManager.onSeamlessLoginPending(this.loginModel);
      LoginController<EmailLoginModelImpl>.AccountVerifedCallback requestCallback = new LoginController.AccountVerifedCallback(loginManager);
      Bundle parameters = new Bundle();
      Utility.putNonNullString(parameters, "fb_user_token", loginManager.getSeamlessLoginTokenRegardlessTimeOut());
      Utility.putNonNullString(parameters, "email", ((EmailLoginModelImpl) this.loginModel).getEmail());
      Utility.putNonNullString(parameters, "response_type", ((EmailLoginModelImpl) this.loginModel).getResponseType());
      Utility.putNonNullString(parameters, "state", ((EmailLoginModelImpl) this.loginModel).getInitialAuthState());
      AccountKitGraphRequest request = this.buildGraphRequest("instant_verification_login", parameters);
      AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
      AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(request, requestCallback);
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
    }
  }

  @Nullable
  private Runnable createPolling(final EmailLoginModelImpl loginModel, final AccountKitGraphRequest.Callback callback) {
    LoginManager loginManager = this.getLoginManager();
    if (loginManager == null) {
      return null;
    } else {
      final String requestInstanceToken = loginManager.getRequestInstanceToken();
      return new Runnable() {
        public void run() {
          Utility.assertUIThread();
          if (this.checkLoginManager()) {
            Bundle parameters = new Bundle();
            Utility.putNonNullString(parameters, "email", loginModel.getEmail());
            AccountKitGraphRequest graphRequest = EmailLoginController.this.buildGraphRequest("poll_login", parameters);
            AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
            AccountKitGraphRequest.Callback callbackWrapper = new AccountKitGraphRequest.Callback() {
              public void onCompleted(AccountKitGraphResponse response) {
                callback.onCompleted(response);
              }
            };
            AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(graphRequest, callbackWrapper);
            AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
          }
        }

        private boolean checkLoginManager() {
          LoginManager loginManager = EmailLoginController.this.getLoginManager();
          return loginManager != null && requestInstanceToken.equals(loginManager.getRequestInstanceToken()) && loginManager.isLoginInProgress();
        }
      };
    }
  }

  private class LoginModelCodeCallback implements AccountKitGraphRequest.Callback {
    final EmailLoginModelImpl loginModel;

    LoginModelCodeCallback(EmailLoginModelImpl loginModel) {
      this.loginModel = loginModel;
    }

    public void onCompleted(AccountKitGraphResponse response) {
      Utility.assertUIThread();
      LoginManager loginManager = EmailLoginController.this.getLoginManager();
      if (loginManager != null) {
        if (loginManager.isActivityAvailable() && loginManager.isLoginInProgress()) {
          boolean var15 = false;

          label255:
          {
            label256:
            {
              label257:
              {
                label258:
                {
                  try {
                    var15 = true;
                    if (response.getError() != null) {
                      Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
                      EmailLoginController.this.onError((AccountKitError) error.first);
                      var15 = false;
                      break label255;
                    }

                    JSONObject result = response.getResponseObject();
                    if (result == null) {
                      EmailLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND);
                      var15 = false;
                      break label257;
                    }

                    try {
                      String status = result.getString("status");
                      String intervalSecondsString;
                      if (!status.equals("pending")) {
                        String accessToken;
                        if (Utility.areObjectsEqual(this.loginModel.getResponseType(), "token")) {
                          accessToken = result.getString("access_token");
                          intervalSecondsString = result.getString("id");
                          String tokenRefreshIntervalString = result.getString("token_refresh_interval_sec");
                          long tokenRefreshIntervalInSeconds = Long.parseLong(tokenRefreshIntervalString);
                          AccessToken token = new AccessToken(accessToken, intervalSecondsString, AccountKit.getApplicationId(), tokenRefreshIntervalInSeconds, new Date());
                          EmailLoginController.this.accessTokenManager.setCurrentAccessToken(token);
                          String finalAuthState = result.optString("state");
                          this.loginModel.setFinalAuthState(finalAuthState);
                          this.loginModel.setAccessToken(token);
                          this.loginModel.setStatus(LoginStatus.SUCCESS);
                          var15 = false;
                        } else {
                          accessToken = result.getString("code");
                          this.loginModel.setCode(accessToken);
                          intervalSecondsString = result.optString("state");
                          this.loginModel.setFinalAuthState(intervalSecondsString);
                          this.loginModel.setStatus(LoginStatus.SUCCESS);
                          var15 = false;
                        }
                        break label256;
                      }

                      Runnable poll = EmailLoginController.this.createPolling(this.loginModel, EmailLoginController.this.new LoginModelCodeCallback(this.loginModel));
                      if (poll == null) {
                        var15 = false;
                        break label258;
                      }

                      intervalSecondsString = result.getString("interval_sec");
                      int intervalSeconds = Integer.parseInt(intervalSecondsString);
                      this.loginModel.setInterval(intervalSeconds);
                      String expiresInString = result.getString("expires_in_sec");
                      long expiresIn = Long.parseLong(expiresInString);
                      this.loginModel.setExpiresInSeconds(expiresIn);
                      if (expiresIn >= (long) this.loginModel.getInterval()) {
                        if (!loginManager.isActivityAvailable() && !loginManager.isLoginInProgress()) {
                          var15 = false;
                          break label256;
                        }

                        (new Handler()).postDelayed(poll, (long) (this.loginModel.getInterval() * 1000));
                        var15 = false;
                        break label256;
                      }

                      EmailLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.EXPIRED_EMAIL_REQUEST);
                      var15 = false;
                    } catch (NumberFormatException | JSONException var16) {
                      EmailLoginController.this.onError(AccountKitError.Type.LOGIN_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT);
                      var15 = false;
                      break label256;
                    }
                  } finally {
                    if (var15) {
                      if (this.loginModel != null) {
                        switch (this.loginModel.getStatus()) {
                          case SUCCESS:
                          case ERROR:
                            loginManager.onLoginComplete(this.loginModel);
                            EmailLoginController.this.broadcastLoginStateChange();
                            loginManager.clearLogIn();
                        }
                      }

                    }
                  }

                  if (this.loginModel != null) {
                    switch (this.loginModel.getStatus()) {
                      case SUCCESS:
                      case ERROR:
                        loginManager.onLoginComplete(this.loginModel);
                        EmailLoginController.this.broadcastLoginStateChange();
                        loginManager.clearLogIn();
                    }
                  }

                  return;
                }

                if (this.loginModel != null) {
                  switch (this.loginModel.getStatus()) {
                    case SUCCESS:
                    case ERROR:
                      loginManager.onLoginComplete(this.loginModel);
                      EmailLoginController.this.broadcastLoginStateChange();
                      loginManager.clearLogIn();
                  }
                }

                return;
              }

              if (this.loginModel != null) {
                switch (this.loginModel.getStatus()) {
                  case SUCCESS:
                  case ERROR:
                    loginManager.onLoginComplete(this.loginModel);
                    EmailLoginController.this.broadcastLoginStateChange();
                    loginManager.clearLogIn();
                }
              }

              return;
            }

            if (this.loginModel != null) {
              switch (this.loginModel.getStatus()) {
                case SUCCESS:
                case ERROR:
                  loginManager.onLoginComplete(this.loginModel);
                  EmailLoginController.this.broadcastLoginStateChange();
                  loginManager.clearLogIn();
              }
            }

            return;
          }

          if (this.loginModel != null) {
            switch (this.loginModel.getStatus()) {
              case SUCCESS:
              case ERROR:
                loginManager.onLoginComplete(this.loginModel);
                EmailLoginController.this.broadcastLoginStateChange();
                loginManager.clearLogIn();
            }
          }

        } else {
          Log.w(EmailLoginController.TAG, "Warning: Callback issues while activity not available.");
        }
      }
    }
  }
}
