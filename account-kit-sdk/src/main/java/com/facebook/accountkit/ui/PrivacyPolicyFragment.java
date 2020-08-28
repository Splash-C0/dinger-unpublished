package com.facebook.accountkit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.PhoneLoginModel;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.Utility;

public class PrivacyPolicyFragment extends ContentFragment {
  protected static final String COOKIE_URL = "https://m.facebook.com/policies/cookies/";
  protected static final String DATA_URL = "https://m.facebook.com/about/privacy/";
  protected static final String TERMS_URL = "https://m.facebook.com/terms";
  private static final String RETRY_KEY = "retry";
  private static final String NEXT_BUTTON_TYPE = "next_button_type";
  private static final String LOGIN_FLOW_STATE = "login_flow_state";
  private static final String RETRY_BUTTON_VISIBLE = "retry button visible";
  private boolean nextButtonEnabled = true;
  private PrivacyPolicyFragment.OnCompleteListener onCompleteListener;
  private ButtonType nextButtonType;
  private LoginFlowState loginFlowState;
  private TextView retryButton;
  private Button nextButton;
  private boolean retryButtonVisible = true;
  private TextView termsText;
  @Nullable
  private String mPrivacyPolicy = null;
  @Nullable
  private String mTermsOfService = null;

  public PrivacyPolicyFragment() {
  }

  public static PrivacyPolicyFragment create(@NonNull UIManager uiManager, @NonNull LoginFlowState loginFlowState, @NonNull ButtonType nextButtonType) {
    PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
    fragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
    fragment.setLoginFlowState(loginFlowState);
    fragment.setNextButtonType(nextButtonType);
    return fragment;
  }

  public LoginFlowState getLoginFlowState() {
    return this.loginFlowState;
  }

  protected void setLoginFlowState(@NonNull LoginFlowState loginFlowState) {
    this.loginFlowState = loginFlowState;
    this.getViewState().putInt("login_flow_state", loginFlowState.ordinal());
  }

  boolean isKeyboardFragment() {
    return true;
  }

  protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup view = (ViewGroup) inflater.inflate(layout.com_accountkit_fragment_confirmation_code_bottom, container, false);
    if (ViewUtility.isSkin(this.getUIManager(), SkinManager.Skin.CONTEMPORARY)) {
      View btn = view.findViewById(id.com_accountkit_next_button);
      ((ViewGroup) btn.getParent()).removeView(btn);
      View space = view.findViewById(id.com_accountkit_space);
      ((ViewGroup) space.getParent()).removeView(space);
      view.addView(space);
      view.addView(btn);
    }

    return view;
  }

  protected void onViewReadyWithState(View view, Bundle viewState) {
    super.onViewReadyWithState(view, viewState);
    this.nextButtonType = ButtonType.values()[viewState.getInt("next_button_type")];
    this.loginFlowState = LoginFlowState.values()[viewState.getInt("login_flow_state")];
    this.retryButtonVisible = viewState.getBoolean("retry button visible", true);
    this.nextButton = (Button) view.findViewById(id.com_accountkit_next_button);
    this.retryButton = (TextView) view.findViewById(id.com_accountkit_retry_button);
    if (this.nextButton != null) {
      this.nextButton.setEnabled(this.nextButtonEnabled);
      this.nextButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          if (PrivacyPolicyFragment.this.onCompleteListener != null) {
            PrivacyPolicyFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.ENTER_CONFIRMATION_CODE.name());
          }

        }
      });
      this.nextButton.setText(this.nextButtonType.getValue());
    }

    if (this.retryButton != null) {
      this.retryButton.setVisibility(this.retryButtonVisible ? 0 : 8);
      this.retryButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          if (PrivacyPolicyFragment.this.onCompleteListener != null) {
            PrivacyPolicyFragment.this.onCompleteListener.onRetry(v.getContext());
          }

        }
      });
      this.retryButton.setTextColor(ViewUtility.getButtonColor(this.getActivity(), this.getUIManager()));
    }

    this.termsText = (TextView) view.findViewById(id.com_accountkit_confirmation_code_agreement);
    if (this.termsText != null) {
      this.termsText.setMovementMethod(new CustomLinkMovement(new CustomLinkMovement.OnURLClickedListener() {
        public void onURLClicked(String url) {
        }
      }));
    }

    this.updateTermsText(this.termsText, this.nextButton.getText());
  }

  public void onStart() {
    super.onStart();
    this.updateTermsText(this.termsText, this.nextButton.getText());
  }

  public void setNextButtonType(ButtonType buttonType) {
    this.nextButtonType = buttonType;
    this.getViewState().putInt("next_button_type", this.nextButtonType.ordinal());
    if (this.nextButton != null) {
      this.nextButton.setText(buttonType.getValue());
    }

  }

  public void setNextButtonEnabled(boolean enabled) {
    this.nextButtonEnabled = enabled;
    if (this.nextButton != null) {
      this.nextButton.setEnabled(enabled);
    }

  }

  public void setRetryVisible(boolean visible) {
    this.retryButtonVisible = visible;
    this.getViewState().putBoolean("retry button visible", this.retryButtonVisible);
    if (this.retryButton != null) {
      this.retryButton.setVisibility(visible ? 0 : 8);
    }

  }

  public void setOnCompleteListener(@Nullable PrivacyPolicyFragment.OnCompleteListener onCompleteListener) {
    this.onCompleteListener = onCompleteListener;
  }

  public boolean getRetry() {
    return this.getViewState().getBoolean("retry", false);
  }

  public void setRetry(boolean retry) {
    this.getViewState().putBoolean("retry", retry);
  }

  protected void updateTermsText(TextView termsTextView, CharSequence nextButtonText) {
    if (termsTextView != null) {
      if (this.getActivity() != null) {
        PhoneLoginModel loginModel = AccountKit.getCurrentPhoneNumberLogInModel();
        this.mPrivacyPolicy = loginModel != null && !Utility.isNullOrEmpty(loginModel.getPrivacyPolicy()) ? loginModel.getPrivacyPolicy() : this.mPrivacyPolicy;
        this.mTermsOfService = loginModel != null && !Utility.isNullOrEmpty(loginModel.getTermsOfService()) ? loginModel.getTermsOfService() : this.mTermsOfService;
        if (!Utility.isNullOrEmpty(this.mPrivacyPolicy)) {
          if (!Utility.isNullOrEmpty(this.mTermsOfService)) {
            termsTextView.setText(Html.fromHtml(this.getString(string.com_accountkit_confirmation_code_agreement_app_privacy_policy_and_terms, new Object[]{nextButtonText, "https://m.facebook.com/terms", "https://m.facebook.com/about/privacy/", "https://m.facebook.com/policies/cookies/", this.mPrivacyPolicy, this.mTermsOfService, AccountKit.getApplicationName()})));
          } else {
            termsTextView.setText(Html.fromHtml(this.getString(string.com_accountkit_confirmation_code_agreement_app_privacy_policy, new Object[]{nextButtonText, "https://m.facebook.com/terms", "https://m.facebook.com/about/privacy/", "https://m.facebook.com/policies/cookies/", this.mPrivacyPolicy, AccountKit.getApplicationName()})));
          }
        } else {
          termsTextView.setText(this.getConfirmationCodeAgreementText(nextButtonText));
        }

      }
    }
  }

  @SuppressLint({"StringFormatMatches"})
  private Spanned getConfirmationCodeAgreementText(CharSequence nextButtonText) {
    return Html.fromHtml(this.getString(string.com_accountkit_confirmation_code_agreement, new Object[]{nextButtonText, "https://m.facebook.com/terms", "https://m.facebook.com/about/privacy/", "https://m.facebook.com/policies/cookies/"}));
  }

  public interface OnCompleteListener {
    void onNext(Context var1, String var2);

    void onRetry(Context var1);
  }
}
