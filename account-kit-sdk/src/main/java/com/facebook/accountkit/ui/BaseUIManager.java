package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.InternalAccountKitError;

public class BaseUIManager implements UIManager, Parcelable {
  public static final int THEME_ID_NOT_SET = -1;
  public static final Creator<BaseUIManager> CREATOR = new Creator<BaseUIManager>() {
    public BaseUIManager createFromParcel(Parcel source) {
      return new BaseUIManager(source);
    }

    public BaseUIManager[] newArray(int size) {
      return new BaseUIManager[size];
    }
  };
  protected UIManager.UIManagerListener listener;
  @StyleRes
  private int themeId;
  private LoginFlowState flowState;
  private Fragment headerFragment;
  private Fragment bodyFragment;
  private Fragment footerFragment;

  public BaseUIManager(@StyleRes int themeId) {
    this.themeId = themeId;
    this.flowState = LoginFlowState.NONE;
  }

  protected BaseUIManager(Parcel source) {
    this.themeId = source.readInt();
    this.flowState = LoginFlowState.values()[source.readInt()];
  }

  @NonNull
  static Fragment getDefaultBodyFragment(UIManager uiManager, LoginFlowState flowState) {
    StaticContentFragmentFactory.StaticContentFragment bodyFragment;
    switch (flowState) {
      case ACCOUNT_VERIFIED:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_sent_code_center);
        break;
      case CONFIRM_ACCOUNT_VERIFIED:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState);
        break;
      case CODE_INPUT:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_confirmation_code_center);
        break;
      case EMAIL_INPUT:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_email_login_center);
        break;
      case EMAIL_VERIFY:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_email_verify_center);
        break;
      case ERROR:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_error_center);
        break;
      case PHONE_NUMBER_INPUT:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_phone_login_center);
        break;
      case SENDING_CODE:
      case CONFIRM_INSTANT_VERIFICATION_LOGIN:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_sending_code_center);
        break;
      case SENT_CODE:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_sent_code_center);
        break;
      case VERIFIED:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_verified_code_center);
        break;
      case VERIFYING_CODE:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState, layout.com_accountkit_fragment_verifying_code_center);
        break;
      case RESEND:
      default:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, flowState);
    }

    return bodyFragment;
  }

  static Fragment getDefaultBodyFragment(UIManager uiManager, UpdateFlowState flowState) {
    StaticContentFragmentFactory.StaticContentFragment bodyFragment;
    switch (flowState) {
      case CODE_INPUT:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_confirmation_code_center);
        break;
      case CODE_INPUT_ERROR:
      case PHONE_NUMBER_INPUT_ERROR:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_error_center);
        break;
      case PHONE_NUMBER_INPUT:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_phone_login_center);
        break;
      case SENDING_CODE:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_sending_code_center);
        break;
      case SENT_CODE:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_sent_code_center);
        break;
      case VERIFIED:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_verified_code_center);
        break;
      case VERIFYING_CODE:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE, layout.com_accountkit_fragment_verifying_code_center);
        break;
      default:
        bodyFragment = StaticContentFragmentFactory.create(uiManager, LoginFlowState.NONE);
    }

    return bodyFragment;
  }

  @NonNull
  static Fragment getDefaultFooterFragment(UIManager uiManager) {
    return TitleFragmentFactory.create(uiManager);
  }

  @NonNull
  static Fragment getDefaultHeaderFragment(UIManager uiManager, LoginFlowState state, LoginType loginType, @Nullable NotificationChannel notificationChannel) {
    int titleResourceId;
    label34:
    switch (state) {
      case ACCOUNT_VERIFIED:
        titleResourceId = string.com_accountkit_account_verified;
        break;
      case CONFIRM_ACCOUNT_VERIFIED:
      case CONFIRM_INSTANT_VERIFICATION_LOGIN:
        titleResourceId = string.com_accountkit_account_verified;
        break;
      case CODE_INPUT:
        titleResourceId = string.com_accountkit_confirmation_code_title;
        break;
      case EMAIL_INPUT:
        titleResourceId = string.com_accountkit_email_login_title;
        break;
      case EMAIL_VERIFY:
        titleResourceId = string.com_accountkit_email_verify_title;
        break;
      case ERROR:
        switch (loginType) {
          case PHONE:
            titleResourceId = string.com_accountkit_phone_error_title;
            break label34;
          default:
            titleResourceId = string.com_accountkit_error_title;
            break label34;
        }
      case PHONE_NUMBER_INPUT:
        titleResourceId = string.com_accountkit_phone_login_title;
        break;
      case SENDING_CODE:
        switch (loginType) {
          case PHONE:
            if (notificationChannel == NotificationChannel.FACEBOOK) {
              titleResourceId = string.com_accountkit_phone_sending_code_on_fb_title;
            } else {
              titleResourceId = string.com_accountkit_phone_loading_title;
            }
            break label34;
          case EMAIL:
            titleResourceId = string.com_accountkit_email_loading_title;
            break label34;
          default:
            throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.UNEXPECTED_STATE);
        }
      case SENT_CODE:
        titleResourceId = string.com_accountkit_sent_title;
        break;
      case VERIFIED:
        titleResourceId = string.com_accountkit_success_title;
        break;
      case VERIFYING_CODE:
        titleResourceId = string.com_accountkit_verify_title;
        break;
      case RESEND:
        titleResourceId = string.com_accountkit_resend_title;
        break;
      default:
        titleResourceId = -1;
    }

    TitleFragmentFactory.TitleFragment headerFragment;
    if (titleResourceId > -1) {
      headerFragment = TitleFragmentFactory.create(uiManager, titleResourceId);
    } else {
      headerFragment = TitleFragmentFactory.create(uiManager);
    }

    return headerFragment;
  }

  static Fragment getDefaultHeaderFragment(UIManager uiManager, UpdateFlowState state) {
    int titleResourceId;
    switch (state) {
      case CODE_INPUT:
        titleResourceId = string.com_accountkit_confirmation_code_title;
        break;
      case CODE_INPUT_ERROR:
        titleResourceId = string.com_accountkit_error_title;
        break;
      case PHONE_NUMBER_INPUT_ERROR:
        titleResourceId = string.com_accountkit_phone_error_title;
        break;
      case PHONE_NUMBER_INPUT:
        titleResourceId = string.com_accountkit_phone_update_title;
        break;
      case SENDING_CODE:
        titleResourceId = string.com_accountkit_phone_loading_title;
        break;
      case SENT_CODE:
        titleResourceId = string.com_accountkit_sent_title;
        break;
      case VERIFIED:
        titleResourceId = string.com_accountkit_success_title;
        break;
      case VERIFYING_CODE:
        titleResourceId = string.com_accountkit_verify_title;
        break;
      default:
        titleResourceId = -1;
    }

    TitleFragmentFactory.TitleFragment headerFragment;
    if (titleResourceId > -1) {
      headerFragment = TitleFragmentFactory.create(uiManager, titleResourceId);
    } else {
      headerFragment = TitleFragmentFactory.create(uiManager);
    }

    return headerFragment;
  }

  LoginFlowState getFlowState() {
    return this.flowState;
  }

  public int getThemeId() {
    return this.themeId;
  }

  public void setThemeId(@StyleRes int themeId) {
    this.themeId = themeId;
  }

  protected void updateFlowState(LoginFlowState state) {
    if (this.flowState != state) {
      this.flowState = state;
      this.headerFragment = null;
      this.bodyFragment = null;
      this.footerFragment = null;
    }

  }

  @Nullable
  public Fragment getBodyFragment(LoginFlowState state) {
    this.updateFlowState(state);
    if (this.bodyFragment != null) {
      return this.bodyFragment;
    } else {
      this.bodyFragment = getDefaultBodyFragment(this, (LoginFlowState) this.flowState);
      return this.bodyFragment;
    }
  }

  @Nullable
  public ButtonType getButtonType(LoginFlowState state) {
    this.updateFlowState(state);
    return null;
  }

  @Nullable
  public Fragment getFooterFragment(LoginFlowState state) {
    this.updateFlowState(state);
    if (this.footerFragment != null) {
      return this.footerFragment;
    } else {
      this.footerFragment = getDefaultFooterFragment(this);
      return this.footerFragment;
    }
  }

  @Nullable
  public Fragment getHeaderFragment(LoginFlowState state) {
    this.updateFlowState(state);
    return this.headerFragment;
  }

  @Nullable
  public TextPosition getTextPosition(LoginFlowState state) {
    this.updateFlowState(state);
    return TextPosition.BELOW_BODY;
  }

  public void setUIManagerListener(UIManager.UIManagerListener listener) {
    this.listener = listener;
  }

  public void onError(AccountKitError error) {
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.themeId);
    dest.writeInt(this.flowState.ordinal());
  }
}
