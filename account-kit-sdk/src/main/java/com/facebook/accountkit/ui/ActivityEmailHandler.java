package com.facebook.accountkit.ui;

import android.os.Handler;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.EmailLoginModel;
import com.facebook.accountkit.EmailLoginTracker;
import com.facebook.accountkit.LoginResult;

final class ActivityEmailHandler extends ActivityHandler {
  public static final Creator<ActivityEmailHandler> CREATOR = new Creator<ActivityEmailHandler>() {
    public ActivityEmailHandler createFromParcel(Parcel source) {
      return new ActivityEmailHandler(source);
    }

    public ActivityEmailHandler[] newArray(int size) {
      return new ActivityEmailHandler[size];
    }
  };

  public ActivityEmailHandler(@NonNull AccountKitConfiguration configuration) {
    super(configuration);
  }

  protected ActivityEmailHandler(Parcel parcel) {
    super(parcel);
  }

  public EmailLoginTracker getLoginTracker(final AccountKitActivity activity) {
    if (this.getEmailTracker() == null) {
      this.tracker = new EmailLoginTracker() {
        protected void onStarted(EmailLoginModel loginModel) {
          if (activity.getContentController() instanceof SendingCodeContentController) {
            activity.pushState(LoginFlowState.SENT_CODE, (StateStackManager.OnPushListener) null);
          }
        }

        protected void onAccountVerified(EmailLoginModel loginModel) {
          ContentController contentController = activity.getContentController();
          if (contentController instanceof SendingCodeContentController) {
            activity.pushState(LoginFlowState.ACCOUNT_VERIFIED, (StateStackManager.OnPushListener) null);
          }
        }

        protected void onSuccess(EmailLoginModel loginModel) {
          ContentController contentController = activity.getContentController();
          if (contentController instanceof EmailVerifyContentController || contentController instanceof VerifyingCodeContentController) {
            activity.pushState(LoginFlowState.VERIFIED, (StateStackManager.OnPushListener) null);
            activity.setFinalAuthState(loginModel.getFinalAuthState());
            activity.setAccessToken(loginModel.getAccessToken());
            activity.setAuthorizationCode(loginModel.getCode());
            activity.setLoginResult(LoginResult.SUCCESS);
            AccessToken accessToken = loginModel.getAccessToken();
            if (accessToken != null) {
              activity.setTokenRefreshIntervalInSeconds(accessToken.getTokenRefreshIntervalSeconds());
            }

            (new Handler()).postDelayed(new Runnable() {
              public void run() {
                finishActivity();
              }
            }, 2000L);
          }
        }

        protected void onError(AccountKitException exception) {
          activity.pushError(exception.getError());
        }

        protected void onCancel(EmailLoginModel loginModel) {
          activity.setNewLoginFlowManagerAndHandler((LoginFlowManager) null);
        }

        private void finishActivity() {
          activity.sendResult();
        }
      };
    }

    return this.getEmailTracker();
  }

  public void onEmailLoginComplete(AccountKitActivity activity, EmailLoginFlowManager emailManager, String email) {
    activity.pushState(LoginFlowState.SENDING_CODE, (StateStackManager.OnPushListener) null);
    emailManager.setEmail(email);
    emailManager.logInWithEmail(this.configuration.getResponseType(), this.configuration.getInitialAuthState());
  }

  public void onEmailVerifyRetry(final AccountKitActivity activity) {
    AccountKit.cancelLogin();
    activity.popBackStack(LoginFlowState.EMAIL_INPUT, new StateStackManager.OnPopListener() {
      public void onContentPopped() {
        ActivityEmailHandler.this.emailVerifySetRetry(activity);
      }
    });
  }

  private void emailVerifySetRetry(AccountKitActivity activity) {
    ContentController contentController = activity.getContentController();
    if (contentController instanceof EmailLoginContentController) {
      ((EmailLoginContentController) contentController).setRetry();
    }
  }

  public void onSentCodeComplete(AccountKitActivity activity) {
    if (activity.getContentController() instanceof SentCodeContentController) {
      activity.pushState(LoginFlowState.EMAIL_VERIFY, (StateStackManager.OnPushListener) null);
    }
  }

  public void onAccountVerifiedComplete(AccountKitActivity accountKitActivity) {
    accountKitActivity.pushState(LoginFlowState.CONFIRM_ACCOUNT_VERIFIED, (StateStackManager.OnPushListener) null);
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
  }

  private EmailLoginTracker getEmailTracker() {
    return (EmailLoginTracker) this.tracker;
  }
}
