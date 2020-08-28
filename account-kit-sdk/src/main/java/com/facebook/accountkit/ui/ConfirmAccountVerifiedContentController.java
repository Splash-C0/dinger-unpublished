package com.facebook.accountkit.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.LoginModel;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.internal.Utility;

final class ConfirmAccountVerifiedContentController extends ContentControllerBase implements ButtonContentController {
  private static final ButtonType DEFAULT_BUTTON_TYPE;
  private static final LoginFlowState LOGIN_FLOW_STATE;

  static {
    DEFAULT_BUTTON_TYPE = ButtonType.CONTINUE;
    LOGIN_FLOW_STATE = LoginFlowState.CONFIRM_ACCOUNT_VERIFIED;
  }

  TitleFragmentFactory.TitleFragment footerFragment;
  TitleFragmentFactory.TitleFragment headerFragment;
  private PrivacyPolicyFragment bottomFragment;
  private ButtonType buttonType;
  private ContentFragment centerFragment;
  private ContentFragment textFragment;
  private ContentFragment topFragment;
  private PrivacyPolicyFragment.OnCompleteListener onCompleteListener;

  ConfirmAccountVerifiedContentController(AccountKitConfiguration configuration) {
    super(configuration);
    this.buttonType = DEFAULT_BUTTON_TYPE;
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(ConfirmAccountVerifiedContentController.BottomFragment.create(this.configuration.getUIManager(), LOGIN_FLOW_STATE, DEFAULT_BUTTON_TYPE));
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof ConfirmAccountVerifiedContentController.BottomFragment) {
      this.bottomFragment = (ConfirmAccountVerifiedContentController.BottomFragment) fragment;
      this.bottomFragment.setOnCompleteListener(this.getOnCompleteListener());
      this.bottomFragment.setRetryVisible(false);
      this.updateNextButton();
    }
  }

  public ButtonType getButtonType() {
    return this.buttonType;
  }

  public void setButtonType(ButtonType buttonType) {
    this.buttonType = buttonType;
    this.updateNextButton();
  }

  public ContentFragment getCenterFragment() {
    if (this.centerFragment == null) {
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
    }

    return this.centerFragment;
  }

  public void setCenterFragment(@Nullable ContentFragment fragment) {
    this.centerFragment = fragment;
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
      this.setHeaderFragment(TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_account_verified));
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
      this.setTextFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
    }

    return this.textFragment;
  }

  public void setTextFragment(@Nullable ContentFragment fragment) {
    this.textFragment = fragment;
  }

  public ContentFragment getTopFragment() {
    if (this.topFragment == null) {
      this.setTopFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
    }

    return this.topFragment;
  }

  public void setTopFragment(@Nullable ContentFragment fragment) {
    this.topFragment = fragment;
  }

  public boolean isTransient() {
    return false;
  }

  private void updateNextButton() {
    if (this.topFragment != null && this.bottomFragment != null) {
      this.bottomFragment.setNextButtonType(this.getButtonType());
    }
  }

  protected void logImpression() {
    if (this.bottomFragment != null) {
      AccountKitController.Logger.logUIConfirmAccountVerified(true, this.configuration.getLoginType());
    }
  }

  private PrivacyPolicyFragment.OnCompleteListener getOnCompleteListener() {
    if (this.onCompleteListener == null) {
      this.onCompleteListener = new PrivacyPolicyFragment.OnCompleteListener() {
        public void onNext(Context context, String buttonName) {
          if (ConfirmAccountVerifiedContentController.this.topFragment != null && ConfirmAccountVerifiedContentController.this.bottomFragment != null) {
            Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.CONFIRM_SEAMLESS_LOGIN);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
          }
        }

        public void onRetry(Context context) {
        }
      };
    }

    return this.onCompleteListener;
  }

  public static class BottomFragment extends PrivacyPolicyFragment {
    private static final String ACCOUNT_KIT_URL = "https://www.accountkit.com/faq";

    public BottomFragment() {
    }

    public static ConfirmAccountVerifiedContentController.BottomFragment create(@NonNull UIManager uiManager, @NonNull LoginFlowState loginFlowState, @NonNull ButtonType nextButtonType) {
      ConfirmAccountVerifiedContentController.BottomFragment fragment = new ConfirmAccountVerifiedContentController.BottomFragment();
      fragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
      fragment.setLoginFlowState(loginFlowState);
      fragment.setNextButtonType(nextButtonType);
      return fragment;
    }

    protected void updateTermsText(TextView termsTextView, CharSequence nextButtonText) {
      if (termsTextView != null) {
        if (this.getActivity() != null) {
          LoginModel loginModel = AccountKit.getCurrentLogInModel();
          if (loginModel != null && !Utility.isNullOrEmpty(loginModel.getPrivacyPolicy())) {
            if (!Utility.isNullOrEmpty(loginModel.getTermsOfService())) {
              termsTextView.setText(Html.fromHtml(this.getString(string.com_accountkit_confirmation_code_agreement_app_privacy_policy_and_terms_instant_verification, new Object[]{nextButtonText, "https://m.facebook.com/terms", "https://m.facebook.com/about/privacy/", "https://m.facebook.com/policies/cookies/", loginModel.getPrivacyPolicy(), loginModel.getTermsOfService(), AccountKit.getApplicationName(), "https://www.accountkit.com/faq"})));
            } else {
              termsTextView.setText(Html.fromHtml(this.getString(string.com_accountkit_confirmation_code_agreement_app_privacy_policy_instant_verification, new Object[]{nextButtonText, "https://m.facebook.com/terms", "https://m.facebook.com/about/privacy/", "https://m.facebook.com/policies/cookies/", loginModel.getPrivacyPolicy(), AccountKit.getApplicationName(), "https://www.accountkit.com/faq"})));
            }
          } else {
            termsTextView.setText(Html.fromHtml(this.getString(string.com_accountkit_confirmation_code_agreement_instant_verification, new Object[]{nextButtonText, "https://m.facebook.com/terms", "https://m.facebook.com/about/privacy/", "https://m.facebook.com/policies/cookies/", "https://www.accountkit.com/faq"})));
          }

        }
      }
    }
  }
}
