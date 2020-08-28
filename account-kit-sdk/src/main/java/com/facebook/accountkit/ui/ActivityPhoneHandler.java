package com.facebook.accountkit.ui;

import android.os.Handler;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.LoginResult;
import com.facebook.accountkit.PhoneLoginModel;
import com.facebook.accountkit.PhoneLoginTracker;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.internal.AccountKitController;

final class ActivityPhoneHandler extends ActivityHandler {
  public static final Creator<ActivityPhoneHandler> CREATOR = new Creator<ActivityPhoneHandler>() {
    public ActivityPhoneHandler createFromParcel(Parcel source) {
      return new ActivityPhoneHandler(source);
    }

    public ActivityPhoneHandler[] newArray(int size) {
      return new ActivityPhoneHandler[size];
    }
  };
  private SmsTracker smsTracker;

  ActivityPhoneHandler(@NonNull AccountKitConfiguration configuration) {
    super(configuration);
  }

  private ActivityPhoneHandler(Parcel parcel) {
    super(parcel);
  }

  public PhoneLoginTracker getLoginTracker(final AccountKitActivity activity) {
    if (this.getPhoneTracker() == null) {
      this.tracker = new PhoneLoginTracker() {
        protected void onStarted(PhoneLoginModel loginModel) {
          ContentController contentController = activity.getContentController();
          if (contentController instanceof SendingCodeContentController || contentController instanceof VerifyingCodeContentController) {
            if (loginModel.getNotificationChannel() == NotificationChannel.SMS || loginModel.getNotificationChannel() == NotificationChannel.WHATSAPP) {
              ActivityPhoneHandler.this.startSmsTrackerIfPossible(activity);
            }

            if (contentController instanceof SendingCodeContentController) {
              activity.pushState(LoginFlowState.SENT_CODE, (StateStackManager.OnPushListener) null);
            } else {
              activity.popBackStack(LoginFlowState.CODE_INPUT, new StateStackManager.OnPopListener() {
                public void onContentPopped() {
                  ContentController contentController = activity.getContentController();
                  if (contentController instanceof LoginConfirmationCodeContentController) {
                    ((LoginConfirmationCodeContentController) contentController).setRetry(true);
                  }
                }
              });
            }

          }
        }

        protected void onAccountVerified(PhoneLoginModel loginModel) {
          ContentController contentController = activity.getContentController();
          if (contentController instanceof SendingCodeContentController) {
            activity.pushState(LoginFlowState.ACCOUNT_VERIFIED, (StateStackManager.OnPushListener) null);
          }
        }

        protected void onSuccess(PhoneLoginModel loginModel) {
          ContentController contentController = activity.getContentController();
          if (contentController instanceof LoginConfirmationCodeContentController || contentController instanceof VerifyingCodeContentController) {
            activity.pushState(LoginFlowState.VERIFIED, (StateStackManager.OnPushListener) null);
            activity.setAuthorizationCode(loginModel.getCode());
            activity.setAccessToken(loginModel.getAccessToken());
            activity.setLoginResult(LoginResult.SUCCESS);
            activity.setFinalAuthState(loginModel.getFinalAuthState());
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

        protected void onCancel(PhoneLoginModel loginModel) {
          activity.setNewLoginFlowManagerAndHandler((LoginFlowManager) null);
        }

        private void finishActivity() {
          activity.sendResult();
        }
      };
    }

    return this.getPhoneTracker();
  }

  void onConfirmationCodeInputComplete(final AccountKitActivity activity, PhoneLoginFlowManager loginFlowManager, final String phoneNumber, String confirmationCode) {
    activity.pushState(LoginFlowState.VERIFYING_CODE, (StateStackManager.OnPushListener) null);
    loginFlowManager.setConfirmationCode(confirmationCode);
  }

  void onConfirmationCodeRetry(AccountKitActivity activity) {
    activity.pushState(LoginFlowState.RESEND, this.getResendOnPushListener());
  }

  private StateStackManager.OnPushListener getResendOnPushListener() {
    final PhoneLoginModel phoneLoginModel = AccountKit.getCurrentPhoneNumberLogInModel();
    final PhoneNumber phoneNumber = phoneLoginModel != null ? phoneLoginModel.getPhoneNumber() : null;
    final NotificationChannel loginType = phoneLoginModel != null ? phoneLoginModel.getNotificationChannel() : null;
    return phoneNumber == null ? null : new StateStackManager.OnPushListener() {
      public void onContentControllerReady(ContentController contentController) {
        if (contentController instanceof ResendContentController) {
          ResendContentController resendContentController = (ResendContentController) contentController;
          resendContentController.setPhoneNumber(phoneNumber);
          resendContentController.setNotificationChannels(ActivityPhoneHandler.this.configuration.getNotificationChannels());
          resendContentController.setResendTime(phoneLoginModel.getResendTime());
          resendContentController.setPhoneLoginType(loginType);
        }

      }

      public void onContentPushed() {
      }
    };
  }

  void onPhoneLoginInputComplete(final AccountKitActivity activity, PhoneLoginFlowManager phoneManager, final PhoneNumber phoneNumber, NotificationChannel notificationChannel) {
    phoneManager.setNotificationChannel(notificationChannel);
    activity.pushState(LoginFlowState.SENDING_CODE, (StateStackManager.OnPushListener) null);
//    phoneManager.logInWithPhoneNumber(phoneNumber, notificationChannel, this.configuration.getResponseType(), this.configuration.getInitialAuthState(), this.configuration.getTestSmsWithInfobip());
  }

  void onResendSwitchLoginMethod(final AccountKitActivity activity, @Nullable final PhoneLoginFlowManager phoneManager, final PhoneNumber phoneNumber, final NotificationChannel notificationChannel) {
    if (phoneManager != null) {
      activity.multiPopBackStack(new StateStackManager.OnPopListener() {
        public void onContentPopped() {
          activity.popBackStack(LoginFlowState.SENT_CODE, new StateStackManager.OnPopListener() {
            public void onContentPopped() {
              activity.pushState(LoginFlowState.SENDING_CODE, (StateStackManager.OnPushListener) null);
              phoneManager.logInWithPhoneNumber(phoneNumber, notificationChannel, ActivityPhoneHandler.this.configuration.getResponseType(), ActivityPhoneHandler.this.configuration.getInitialAuthState(), ActivityPhoneHandler.this.configuration.getTestSmsWithInfobip());
            }
          });
        }
      });
    }
  }

  void onResend(AccountKitActivity activity) {
    AccountKit.cancelLogin();
    this.popToPhoneNumberInput(activity);
  }

  private void popToPhoneNumberInput(final AccountKitActivity activity) {
    ContentController contentController = activity.getContentController();
    if (contentController instanceof ResendContentController) {
      activity.multiPopBackStack(new StateStackManager.OnPopListener() {
        public void onContentPopped() {
          ActivityPhoneHandler.this.popToPhoneNumberInput(activity);
        }
      });
    } else if (contentController instanceof LoginConfirmationCodeContentController) {
      activity.popBackStack(LoginFlowState.PHONE_NUMBER_INPUT, new StateStackManager.OnPopListener() {
        public void onContentPopped() {
          ActivityPhoneHandler.this.resendSetRetry(activity);
        }
      });
    }

  }

  private void resendSetRetry(AccountKitActivity activity) {
    ContentController contentController = activity.getContentController();
    if (contentController instanceof PhoneLoginContentController) {
      ((PhoneLoginContentController) contentController).setRetry();
      contentController.onResume(activity);
    }
  }

  void onResendFacebookNotification(final AccountKitActivity activity, final PhoneLoginFlowManager phoneManager) {
    PhoneLoginModel phoneLoginModel = AccountKit.getCurrentPhoneNumberLogInModel();
    if (phoneLoginModel != null) {
      phoneManager.setNotificationChannel(NotificationChannel.FACEBOOK);
      final PhoneNumber phoneNumber = phoneLoginModel.getPhoneNumber();
      activity.multiPopBackStack(new StateStackManager.OnPopListener() {
        public void onContentPopped() {
          activity.popBackStack(LoginFlowState.SENT_CODE, new StateStackManager.OnPopListener() {
            public void onContentPopped() {
              activity.pushState(LoginFlowState.SENDING_CODE, (StateStackManager.OnPushListener) null);
              phoneManager.logInWithPhoneNumber(phoneNumber, NotificationChannel.FACEBOOK, ActivityPhoneHandler.this.configuration.getResponseType(), ActivityPhoneHandler.this.configuration.getInitialAuthState(), ActivityPhoneHandler.this.configuration.getTestSmsWithInfobip());
            }
          });
        }
      });
    }
  }

  StateStackManager.OnPushListener getConfirmationCodePushListener(final AccountKitActivity activity) {
    return new StateStackManager.OnPushListener() {
      public void onContentControllerReady(ContentController contentController) {
        if (contentController instanceof LoginConfirmationCodeContentController) {
          PhoneLoginModel phoneLoginModel = AccountKit.getCurrentPhoneNumberLogInModel();
          if (phoneLoginModel != null) {
            LoginConfirmationCodeContentController confirmationCodeContentController = (LoginConfirmationCodeContentController) contentController;
            confirmationCodeContentController.setPhoneNumber(phoneLoginModel.getPhoneNumber());
            confirmationCodeContentController.setNotificationChannel(phoneLoginModel.getNotificationChannel());
            confirmationCodeContentController.setDetectedConfirmationCode(ActivityPhoneHandler.this.getLoginTracker(activity).getCode());
          }
        }
      }

      public void onContentPushed() {
      }
    };
  }

  public void onSentCodeComplete(AccountKitActivity activity) {
    activity.pushState(LoginFlowState.CODE_INPUT, (StateStackManager.OnPushListener) null);
  }

  public void onAccountVerifiedComplete(AccountKitActivity activity) {
    activity.pushState(LoginFlowState.CONFIRM_ACCOUNT_VERIFIED, (StateStackManager.OnPushListener) null);
  }

  SmsTracker getSmsTracker() {
    return this.smsTracker;
  }

  void startSmsTrackerIfPossible(final AccountKitActivity activity) {
    if (SmsTracker.canTrackSms(AccountKitController.getApplicationContext())) {
      if (this.smsTracker == null) {
        this.smsTracker = new SmsTracker() {
          protected void confirmationCodeReceived(String code) {
            ContentController contentController = activity.getContentController();
            if (!(contentController instanceof SendingCodeContentController) && !(contentController instanceof SentCodeContentController)) {
              if (contentController instanceof LoginConfirmationCodeContentController) {
                ((LoginConfirmationCodeContentController) contentController).setDetectedConfirmationCode(code);
              }
            } else {
              ActivityPhoneHandler.this.getPhoneTracker().setCode(code);
            }

            ActivityPhoneHandler.this.smsTracker.stopTracking();
          }
        };
      }

      this.smsTracker.startTracking();
    }
  }

  void pauseSmsTracker() {
    if (this.smsTracker != null) {
      this.smsTracker.pauseTracking();
    }

  }

  void stopSmsTracker() {
    if (this.smsTracker != null) {
      this.smsTracker.stopTracking();
    }

  }

  boolean isSmsTracking() {
    return this.smsTracker != null && this.smsTracker.isTracking();
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
  }

  private PhoneLoginTracker getPhoneTracker() {
    return (PhoneLoginTracker) this.tracker;
  }
}
