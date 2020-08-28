package com.facebook.accountkit.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.custom.R.drawable;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.AccountKitController;

import java.util.List;
import java.util.concurrent.TimeUnit;

final class ResendContentController extends ContentControllerBase {
  private static final LoginFlowState LOGIN_FLOW_STATE;

  static {
    LOGIN_FLOW_STATE = LoginFlowState.RESEND;
  }

  private ResendContentController.BottomFragment bottomFragment;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private TitleFragmentFactory.TitleFragment footerFragment;
  private TitleFragmentFactory.TitleFragment headerFragment;
  private StaticContentFragmentFactory.StaticContentFragment textFragment;
  private StaticContentFragmentFactory.StaticContentFragment topFragment;

  ResendContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(new ResendContentController.BottomFragment());
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof ResendContentController.BottomFragment) {
      this.bottomFragment = (ResendContentController.BottomFragment) fragment;
      this.bottomFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.bottomFragment.setOnCompleteListener(new ResendContentController.BottomFragment.OnCompleteListener() {
        public void onEdit(Context context) {
          Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_RESEND);
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        public void onResend(Context context) {
          Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_RESEND);
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        public void onFacebookNotification(Context context) {
          Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_RESEND_FACEBOOK_NOTIFICATION);
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        public void onSwitchNotification(Context context, PhoneNumber phoneNumber, NotificationChannel notificationChannel) {
          Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_RESEND_SWITCH).putExtra(LoginFlowBroadcastReceiver.EXTRA_PHONE_NUMBER, phoneNumber).putExtra(LoginFlowBroadcastReceiver.EXTRA_NOTIFICATION_CHANNEL, notificationChannel);
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
      });
    }
  }

  public ContentFragment getCenterFragment() {
    if (this.centerFragment == null) {
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
    }

    return this.centerFragment;
  }

  public void setCenterFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof StaticContentFragmentFactory.StaticContentFragment) {
      this.centerFragment = (StaticContentFragmentFactory.StaticContentFragment) fragment;
    }
  }

  @Nullable
  public View getFocusView() {
    return null;
  }

  public TitleFragmentFactory.TitleFragment getFooterFragment() {
    if (this.footerFragment == null) {
      this.setFooterFragment(TitleFragmentFactory.create(this.configuration.getUIManager()));
    }

    return this.footerFragment;
  }

  public void setFooterFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    this.footerFragment = fragment;
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.setHeaderFragment(ResendContentController.HeaderFragment.create(this.configuration.getUIManager(), string.com_accountkit_resend_title));
    }

    return this.headerFragment;
  }

  public void setHeaderFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    this.headerFragment = fragment;
  }

  public LoginFlowState getLoginFlowState() {
    return LOGIN_FLOW_STATE;
  }

  public ContentFragment getTextFragment() {
    if (this.textFragment == null) {
      this.textFragment = StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState());
    }

    return this.textFragment;
  }

  public void setTextFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof StaticContentFragmentFactory.StaticContentFragment) {
      this.textFragment = (StaticContentFragmentFactory.StaticContentFragment) fragment;
    }
  }

  public ContentFragment getTopFragment() {
    if (this.topFragment == null) {
      this.setTopFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
    }

    return this.topFragment;
  }

  public void setTopFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof StaticContentFragmentFactory.StaticContentFragment) {
      this.topFragment = (StaticContentFragmentFactory.StaticContentFragment) fragment;
    }
  }

  protected void logImpression() {
    AccountKitController.Logger.logUIResend(true);
  }

  public void setPhoneNumber(@Nullable PhoneNumber phoneNumber) {
    if (this.bottomFragment != null) {
      this.bottomFragment.setPhoneNumber(phoneNumber);
    }

  }

  void setNotificationChannels(List<NotificationChannel> notificationChannels) {
    if (this.bottomFragment != null) {
      this.bottomFragment.setNotificationChannels(notificationChannels);
    }

  }

  void setPhoneLoginType(NotificationChannel phoneLoginType) {
    if (this.bottomFragment != null) {
      this.bottomFragment.setPhoneLoginType(phoneLoginType);
    }

  }

  void setResendTime(long resendTime) {
    if (this.bottomFragment != null) {
      this.bottomFragment.setResendTime(resendTime);
    }

  }

  public static final class BottomFragment extends ContentFragment {
    private static final String TAG = ResendContentController.BottomFragment.class.getSimpleName();
    private static final long MILLIS_PER_SECOND;
    private static final String FACEBOOK_NOTIFICATION_CHANNEL;
    private static final String SMS_NOTIFICATION_CHANNEL;
    private static final String RESEND_TIME_KEY;

    static {
      MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1L);
      FACEBOOK_NOTIFICATION_CHANNEL = TAG + ".FACEBOOK_NOTIFICATION_CHANNEL";
      SMS_NOTIFICATION_CHANNEL = TAG + ".SMS_NOTIFICATION_CHANNEL";
      RESEND_TIME_KEY = TAG + ".RESEND_TIME_KEY";
    }

    private Handler countDownHandler;
    private TextView verifyPhoneNumberView;
    private PhoneNumber phoneNumber;
    private NotificationChannel phoneLoginType;
    private float scale;
    private ResendContentController.BottomFragment.OnCompleteListener onCompleteListener;

    public BottomFragment() {
    }

    private float dpToPx(float dp) {
      return dp * this.scale + 0.5F;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_resend_bottom, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      this.countDownHandler = new Handler();
    }

    LoginFlowState getLoginFlowState() {
      return ResendContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    public boolean areFacebookNotificationsEnabled() {
      return this.getViewState().getBoolean(FACEBOOK_NOTIFICATION_CHANNEL);
    }

    public boolean isSmsEnabled() {
      return this.getViewState().getBoolean(SMS_NOTIFICATION_CHANNEL);
    }

    public void setPhoneLoginType(NotificationChannel phoneLoginType) {
      this.phoneLoginType = phoneLoginType;
    }

    public void setNotificationChannels(List<NotificationChannel> notificationChannels) {
      this.getViewState().putBoolean(FACEBOOK_NOTIFICATION_CHANNEL, notificationChannels.contains(NotificationChannel.FACEBOOK));
      this.getViewState().putBoolean(SMS_NOTIFICATION_CHANNEL, notificationChannels.contains(NotificationChannel.SMS));
      this.updateNotificationViews();
    }

    public long getResendTime() {
      return this.getViewState().getLong(RESEND_TIME_KEY);
    }

    public void setResendTime(long resendTime) {
      this.getViewState().putLong(RESEND_TIME_KEY, resendTime);
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.scale = this.getResources().getDisplayMetrics().density;
      View resendButton = view.findViewById(id.com_accountkit_resend_button);
      this.verifyPhoneNumberView = (TextView) view.findViewById(id.com_accountkit_accountkit_verify_number);
      if (resendButton != null) {
        resendButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            if (BottomFragment.this.onCompleteListener != null) {
              BottomFragment.this.onCompleteListener.onResend(v.getContext());
            }

          }
        });
      }

      TextView facebookNotificationTextView = (TextView) view.findViewById(id.com_accountkit_send_in_fb_button);
      SpannableString fbNotificationSpannableString = new SpannableString(this.getString(string.com_accountkit_button_send_code_through_fb));
      ClickableSpan fbNotificationClickableSpan = new ClickableSpan() {
        public void onClick(View widget) {
          if (BottomFragment.this.onCompleteListener != null) {
            BottomFragment.this.onCompleteListener.onFacebookNotification(widget.getContext());
          }

        }

        public void updateDrawState(TextPaint ds) {
          super.updateDrawState(ds);
          ds.setColor(ViewUtility.getButtonColor(BottomFragment.this.getActivity(), BottomFragment.this.getUIManager()));
          ds.setUnderlineText(false);
        }
      };
      fbNotificationSpannableString.setSpan(fbNotificationClickableSpan, 0, fbNotificationSpannableString.toString().length(), 33);
      SpannableStringBuilder fbNotificationSpanStrBuilder = new SpannableStringBuilder();
      fbNotificationSpanStrBuilder.append(fbNotificationSpannableString).append("\n").append(this.getString(string.com_accountkit_button_send_code_through_fb_details));
      facebookNotificationTextView.setText(fbNotificationSpanStrBuilder);
      facebookNotificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
      this.updateViewStates();
    }

    private void updateCheckInboxPrompt() {
      TextView checkMessage = (TextView) this.getView().findViewById(id.com_accountkit_check_inbox_prompt);
      int checkStringId;
      int checkDrawableId;
      if (NotificationChannel.WHATSAPP.equals(this.phoneLoginType)) {
        checkDrawableId = drawable.ic_whatsapp_icon;
        checkStringId = string.com_accountkit_resend_check_whatsapp;
      } else {
        checkDrawableId = drawable.ic_message_icon;
        checkStringId = string.com_accountkit_resend_check_sms;
      }

      Drawable checkInboxPromptDrawable = ContextCompat.getDrawable(this.getActivity(), checkDrawableId);
      checkInboxPromptDrawable.setBounds(0, 0, (int) this.dpToPx(20.0F), (int) this.dpToPx(20.0F));
      checkMessage.setCompoundDrawables(checkInboxPromptDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
      checkMessage.setCompoundDrawablePadding((int) this.dpToPx(10.0F));
      if (VERSION.SDK_INT >= 17) {
        checkMessage.setCompoundDrawablesRelative(checkInboxPromptDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
      }

      SpannableString resendString = new SpannableString(this.getString(string.com_accountkit_resend_check_enter_code));
      ClickableSpan enterCode = new ClickableSpan() {
        public void onClick(View widget) {
          BottomFragment.this.getFragmentManager().popBackStackImmediate();
        }

        public void updateDrawState(TextPaint ds) {
          super.updateDrawState(ds);
          ds.setColor(ViewUtility.getButtonColor(BottomFragment.this.getActivity(), BottomFragment.this.getUIManager()));
          ds.setUnderlineText(false);
        }
      };
      resendString.setSpan(enterCode, 0, resendString.toString().length(), 33);
      SpannableStringBuilder checkMessageSpanStrBuilder = new SpannableStringBuilder();
      checkMessageSpanStrBuilder.append(this.getString(checkStringId)).append("\n").append(resendString);
      checkMessage.setText(checkMessageSpanStrBuilder);
      checkMessage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateAlternateFallbackVerification() {
      TextView alternateFallbackMethod = (TextView) this.getView().findViewById(id.com_accountkit_switch_method);
      int alternateFallbackMethodTextId;
      int alternateFallbackMethodTextDetailId;
      int alternateFallbackMethodDrawableId;
      final NotificationChannel alternateFallbackNotificationChannel;
      if (NotificationChannel.WHATSAPP.equals(this.phoneLoginType)) {
        alternateFallbackMethodTextId = string.com_accountkit_resend_switch_sms;
        alternateFallbackMethodTextDetailId = string.com_accountkit_resend_switch_sms_detail;
        alternateFallbackMethodDrawableId = drawable.ic_message_icon;
        alternateFallbackNotificationChannel = NotificationChannel.SMS;
      } else {
        alternateFallbackMethodTextId = string.com_accountkit_resend_switch_whatsapp;
        alternateFallbackMethodTextDetailId = string.com_accountkit_resend_switch_whatsapp_detail;
        alternateFallbackMethodDrawableId = drawable.ic_whatsapp_icon;
        alternateFallbackNotificationChannel = NotificationChannel.WHATSAPP;
      }

      Drawable alternateFallbackMethodIcon = ContextCompat.getDrawable(this.getActivity(), alternateFallbackMethodDrawableId);
      alternateFallbackMethodIcon.setBounds(0, 0, (int) this.dpToPx(20.0F), (int) this.dpToPx(20.0F));
      alternateFallbackMethod.setCompoundDrawables(alternateFallbackMethodIcon, (Drawable) null, (Drawable) null, (Drawable) null);
      alternateFallbackMethod.setCompoundDrawablePadding((int) this.dpToPx(15.0F));
      if (VERSION.SDK_INT >= 17) {
        alternateFallbackMethod.setCompoundDrawablesRelative(alternateFallbackMethodIcon, (Drawable) null, (Drawable) null, (Drawable) null);
      }

      SpannableString alternateFallbackMethodSpannable = new SpannableString(this.getString(alternateFallbackMethodTextId));
      ClickableSpan alternateFallbackMethodClickableSpan = new ClickableSpan() {
        public void onClick(View widget) {
          if (BottomFragment.this.onCompleteListener != null) {
            BottomFragment.this.onCompleteListener.onSwitchNotification(widget.getContext(), BottomFragment.this.phoneNumber, alternateFallbackNotificationChannel);
          }

        }

        public void updateDrawState(TextPaint ds) {
          super.updateDrawState(ds);
          ds.setColor(ViewUtility.getButtonColor(BottomFragment.this.getActivity(), BottomFragment.this.getUIManager()));
          ds.setUnderlineText(false);
        }
      };
      alternateFallbackMethodSpannable.setSpan(alternateFallbackMethodClickableSpan, 0, alternateFallbackMethodSpannable.toString().length(), 33);
      SpannableStringBuilder alternateFallbackMethodSpanStrBuilder = new SpannableStringBuilder();
      alternateFallbackMethodSpanStrBuilder.append(alternateFallbackMethodSpannable).append("\n").append(this.getString(alternateFallbackMethodTextDetailId));
      alternateFallbackMethod.setText(alternateFallbackMethodSpanStrBuilder);
      alternateFallbackMethod.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
      this.phoneNumber = phoneNumber;
      this.updatePhoneNumberView();
    }

    public void setOnCompleteListener(@Nullable ResendContentController.BottomFragment.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    public void onStart() {
      super.onStart();
      this.updateViewStates();
    }

    private void updateViewStates() {
      this.updatePhoneNumberView();
      this.updateAlternateFallbackVerification();
      this.updateCheckInboxPrompt();
      this.updateNotificationViews();
      if (NotificationChannel.SMS.equals(this.phoneLoginType)) {
        this.updateResendView();
      } else {
        View view = this.getView();
        Button resendButton = (Button) view.findViewById(id.com_accountkit_resend_button);
        resendButton.setText(string.com_accountkit_button_resend_whatsapp);
      }

    }

    private void updatePhoneNumberView() {
      if (this.isAdded() && this.phoneNumber != null) {
        SpannableString ss = new SpannableString(this.getString(string.com_accountkit_code_change_number));
        ClickableSpan clickSpan = new ClickableSpan() {
          public void onClick(View widget) {
            if (BottomFragment.this.onCompleteListener != null) {
              BottomFragment.this.onCompleteListener.onEdit(widget.getContext());
            }

          }

          public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(ViewUtility.getButtonColor(BottomFragment.this.getActivity(), BottomFragment.this.getUIManager()));
            ds.setUnderlineText(false);
          }
        };
        ss.setSpan(clickSpan, 0, ss.toString().length(), 33);
        SpannableStringBuilder verifyNummberSpanStrBuilder = new SpannableStringBuilder();
        verifyNummberSpanStrBuilder.append(this.getString(string.com_accountkit_code_sent_to_verify)).append("\n").append(this.phoneNumber.toRtlSafeString()).append(". ").append(ss);
        this.verifyPhoneNumberView.setText(verifyNummberSpanStrBuilder);
        this.verifyPhoneNumberView.setMovementMethod(LinkMovementMethod.getInstance());
      }
    }

    private void updateNotificationViews() {
      View view = this.getView();
      if (view != null) {
        view.findViewById(id.com_accountkit_send_in_fb_button).setVisibility(this.areFacebookNotificationsEnabled() ? 0 : 8);
        view.findViewById(id.com_accountkit_switch_method).setVisibility(this.isSmsEnabled() ? 0 : 8);
      }
    }

    private void updateResendView() {
      View view = this.getView();
      if (view != null) {
        View resendView = view.findViewById(id.com_accountkit_resend_button);
        if (resendView != null) {
          if (!NotificationChannel.WHATSAPP.equals(this.phoneLoginType)) {
            final Button resendButton = (Button) resendView;
            final long timeUntilResend = this.getResendTime();
            this.countDownHandler.post(new Runnable() {
              public void run() {
                if (BottomFragment.this.isAdded()) {
                  long timeLeftSeconds = TimeUnit.MILLISECONDS.toSeconds(timeUntilResend - System.currentTimeMillis());
                  if (timeLeftSeconds > 0L) {
                    resendButton.setText(BottomFragment.this.getString(string.com_accountkit_button_resend_code_countdown, new Object[]{timeLeftSeconds}));
                    BottomFragment.this.countDownHandler.postDelayed(this, ResendContentController.BottomFragment.MILLIS_PER_SECOND);
                    resendButton.setEnabled(false);
                  } else {
                    resendButton.setText(string.com_accountkit_button_resend_sms_code);
                    resendButton.setEnabled(true);
                  }

                }
              }
            });
          }
        }
      }
    }

    public void onPause() {
      super.onPause();
      this.countDownHandler.removeCallbacksAndMessages((Object) null);
    }

    public interface OnCompleteListener {
      void onEdit(Context var1);

      void onResend(Context var1);

      void onFacebookNotification(Context var1);

      void onSwitchNotification(Context var1, PhoneNumber var2, NotificationChannel var3);
    }
  }

  public static final class HeaderFragment extends TitleFragmentFactory.TitleFragment {
    public HeaderFragment() {
    }

    public static ResendContentController.HeaderFragment create(@NonNull UIManager uiManager, int titleResourceId, @Nullable String... args) {
      ResendContentController.HeaderFragment titleFragment = new ResendContentController.HeaderFragment();
      titleFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
      titleFragment.setTitleResourceId(titleResourceId, args);
      return titleFragment;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.titleView.setGravity(16);
    }
  }
}
