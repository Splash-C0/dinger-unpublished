package com.facebook.accountkit.ui;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.custom.R.string;

class LoginConfirmationCodeContentController extends ConfirmationCodeContentController {
  private LoginConfirmationCodeContentController.OnCompleteListener onCompleteListener;

  LoginConfirmationCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof PrivacyPolicyFragment) {
      this.bottomFragment = (PrivacyPolicyFragment) fragment;
      this.bottomFragment.setOnCompleteListener(this.getOnCompleteListener());
      this.updateNextButton();
    }
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.setHeaderFragment(LoginConfirmationCodeContentController.TitleFragment.create(this.configuration.getUIManager(), string.com_accountkit_confirmation_code_title));
    }

    return this.headerFragment;
  }

  public void setHeaderFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    if (fragment instanceof LoginConfirmationCodeContentController.TitleFragment) {
      this.headerFragment = (LoginConfirmationCodeContentController.TitleFragment) fragment;
      this.headerFragment.setOnCompleteListener(this.getOnCompleteListener());
    }
  }

  public void setTopFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof ConfirmationCodeContentController.TopFragment) {
      this.topFragment = (ConfirmationCodeContentController.TopFragment) fragment;
      this.topFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      ConfirmationCodeContentController.TopFragment.OnConfirmationCodeChangedListener onConfirmationCodeChangedListener = new ConfirmationCodeContentController.TopFragment.OnConfirmationCodeChangedListener() {
        public void onConfirmationCodeChanged() {
          LoginConfirmationCodeContentController.this.updateNextButton();
        }
      };
      this.topFragment.setOnConfirmationCodeChangedListener(onConfirmationCodeChangedListener);
      this.topFragment.setOnCompleteListener(this.getOnCompleteListener());
    }
  }

  void setNotificationChannel(NotificationChannel notificationChannel) {
    if (this.headerFragment != null) {
      ((LoginConfirmationCodeContentController.TitleFragment) this.headerFragment).setNotificationChannel(notificationChannel);
    }
  }

  private LoginConfirmationCodeContentController.OnCompleteListener getOnCompleteListener() {
    if (this.onCompleteListener == null) {
      this.onCompleteListener = new LoginConfirmationCodeContentController.OnCompleteListener();
    }

    return this.onCompleteListener;
  }

  public static final class TitleFragment extends ConfirmationCodeContentController.TitleFragment {
    @Nullable
    private NotificationChannel notificationChannel;

    public TitleFragment() {
    }

    public static LoginConfirmationCodeContentController.TitleFragment create(UIManager uiManager, int titleResourceId, @Nullable String... args) {
      LoginConfirmationCodeContentController.TitleFragment titleFragment = new LoginConfirmationCodeContentController.TitleFragment();
      titleFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
      titleFragment.setTitleResourceId(titleResourceId, args);
      return titleFragment;
    }

    void setNotificationChannel(NotificationChannel notificationChannel) {
      this.notificationChannel = notificationChannel;
      this.setPhoneNumberView();
    }

    void setPhoneNumberView() {
      if (this.isAdded()) {
        if (this.notificationChannel != null) {
          switch (this.notificationChannel) {
            case FACEBOOK:
              if (this.retry) {
                this.setTitleResourceId(string.com_accountkit_verify_confirmation_code_title, new String[0]);
              } else {
                this.setTitleResourceId(string.com_accountkit_facebook_code_entry_title, new String[0]);
              }
              break;
            default:
              if (this.phoneNumber == null) {
                return;
              }

              String phoneNumberString = this.phoneNumber.toRtlSafeString();
              String titleString;
              if (this.retry) {
                titleString = this.getString(string.com_accountkit_verify_confirmation_code_title_colon) + "\n" + phoneNumberString;
              } else {
                titleString = this.getString(string.com_accountkit_enter_code_sent_to, new Object[]{phoneNumberString});
              }

              SpannableString titleSpan = new SpannableString(titleString);
              ClickableSpan clickSpan = new ClickableSpan() {
                public void onClick(View widget) {
                  if (TitleFragment.this.onCompleteListener != null) {
                    TitleFragment.this.onCompleteListener.onEdit(widget.getContext());
                  }

                }

                public void updateDrawState(TextPaint ds) {
                  super.updateDrawState(ds);
                  ds.setColor(ViewUtility.getButtonColor(TitleFragment.this.getActivity(), TitleFragment.this.getUIManager()));
                  ds.setUnderlineText(false);
                }
              };
              int start = titleSpan.toString().indexOf(phoneNumberString);
              int end = start + phoneNumberString.length();
              titleSpan.setSpan(clickSpan, start, end, 33);
              this.titleView.setText(titleSpan);
              this.titleView.setMovementMethod(LinkMovementMethod.getInstance());
          }

        }
      }
    }
  }

  private class OnCompleteListener implements PrivacyPolicyFragment.OnCompleteListener, ConfirmationCodeContentController.TitleFragment.OnCompleteListener {
    private OnCompleteListener() {
    }

    public void onNext(Context context, String buttonName) {
      if (LoginConfirmationCodeContentController.this.topFragment != null && LoginConfirmationCodeContentController.this.bottomFragment != null) {
        String confirmationCode = LoginConfirmationCodeContentController.this.topFragment.getConfirmationCode();
        Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_CONFIRMATION_CODE_COMPLETE).putExtra(LoginFlowBroadcastReceiver.EXTRA_CONFIRMATION_CODE, confirmationCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
      }
    }

    public void onRetry(Context context) {
      Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_CONFIRMATION_CODE_RETRY);
      LoginConfirmationCodeContentController.this.setRetry(false);
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void onEdit(Context context) {
      if (LoginConfirmationCodeContentController.this.headerFragment != null) {
        LoginConfirmationCodeContentController.this.headerFragment.setRetryWithoutViewUpdate(false);
      }

      Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_RESEND);
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
  }
}
