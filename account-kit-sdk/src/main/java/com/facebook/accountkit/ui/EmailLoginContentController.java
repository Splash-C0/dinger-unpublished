package com.facebook.accountkit.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.internal.Utility;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.HintRequest.Builder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

final class EmailLoginContentController extends ContentControllerBase implements ButtonContentController {
  private static final ButtonType DEFAULT_BUTTON_TYPE;
  private static final LoginFlowState LOGIN_FLOW_STATE;
  private static final int RESOLVE_HINT_REQUEST_CODE = 152;

  static {
    DEFAULT_BUTTON_TYPE = ButtonType.NEXT;
    LOGIN_FLOW_STATE = LoginFlowState.EMAIL_INPUT;
  }

  private EmailLoginContentController.BottomFragment bottomFragment;
  private ButtonType buttonType;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private TitleFragmentFactory.TitleFragment footerFragment;
  private TitleFragmentFactory.TitleFragment headerFragment;
  private EmailLoginContentController.TextFragment textFragment;
  @Nullable
  private EmailLoginContentController.TopFragment topFragment;
  private EmailLoginContentController.OnCompleteListener onCompleteListener;

  EmailLoginContentController(AccountKitConfiguration configuration) {
    super(configuration);
    this.buttonType = DEFAULT_BUTTON_TYPE;
    AccountKitController.initializeLogin();
  }

  static EmailLoginContentController.EmailSourceAppSupplied getEmailAppSuppliedSource(String appSuppliedEmail, String submittedEmail) {
    if (!Utility.isNullOrEmpty(appSuppliedEmail)) {
      return appSuppliedEmail.equals(submittedEmail) ? EmailLoginContentController.EmailSourceAppSupplied.APP_SUPPLIED_EMAIL_USED : EmailLoginContentController.EmailSourceAppSupplied.APP_SUPPLIED_EMAIL_CHANGED;
    } else {
      return EmailLoginContentController.EmailSourceAppSupplied.NO_APP_SUPPLIED_EMAIL;
    }
  }

  static EmailLoginContentController.EmailSourceSelected getEmailSourceSelected(String selectedEmail, String submittedEmail, List<String> availableEmails) {
    if (!Utility.isNullOrEmpty(selectedEmail)) {
      return selectedEmail.equals(submittedEmail) ? EmailLoginContentController.EmailSourceSelected.SELECTED_USED : EmailLoginContentController.EmailSourceSelected.SELECTED_CHANGED;
    } else {
      return availableEmails != null && !availableEmails.isEmpty() ? EmailLoginContentController.EmailSourceSelected.SELECTED_NOT_USED : EmailLoginContentController.EmailSourceSelected.NO_SELECTABLE_EMAILS;
    }
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(new EmailLoginContentController.BottomFragment());
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof EmailLoginContentController.BottomFragment) {
      this.bottomFragment = (EmailLoginContentController.BottomFragment) fragment;
      this.bottomFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.bottomFragment.setOnCompleteListener(this.getOnCompleteListener());
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
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState(), layout.com_accountkit_fragment_email_login_center));
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
    return this.topFragment == null ? null : this.topFragment.emailView;
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
      this.headerFragment = TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_email_login_title);
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
      this.setTextFragment(new EmailLoginContentController.TextFragment());
    }

    return this.textFragment;
  }

  public void setTextFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof EmailLoginContentController.TextFragment) {
      this.textFragment = (EmailLoginContentController.TextFragment) fragment;
      this.textFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.textFragment.setNextButtonTextProvider(new TextContentFragment.NextButtonTextProvider() {
        public String getNextButtonText() {
          return EmailLoginContentController.this.bottomFragment == null ? null : EmailLoginContentController.this.textFragment.getResources().getText(EmailLoginContentController.this.bottomFragment.getNextButtonTextId()).toString();
        }
      });
    }
  }

  @Nullable
  public ContentFragment getTopFragment() {
    if (this.topFragment == null) {
      this.setTopFragment(new EmailLoginContentController.TopFragment());
    }

    return this.topFragment;
  }

  public void setTopFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof EmailLoginContentController.TopFragment) {
      this.topFragment = (EmailLoginContentController.TopFragment) fragment;
      this.topFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.topFragment.setOnEmailChangedListener(new EmailLoginContentController.TopFragment.OnEmailChangedListener() {
        public void onEmailChanged() {
          EmailLoginContentController.this.updateNextButton();
        }
      });
      this.topFragment.setOnCompleteListener(this.getOnCompleteListener());
      if (this.configuration != null && this.configuration.getInitialEmail() != null) {
        this.topFragment.setAppSuppliedEmail(this.configuration.getInitialEmail());
      }

      this.updateNextButton();
    }
  }

  public boolean isTransient() {
    return false;
  }

  void setRetry() {
    if (this.headerFragment != null) {
      this.headerFragment.setTitleResourceId(string.com_accountkit_email_login_retry_title);
    }

    if (this.bottomFragment != null) {
      this.bottomFragment.setRetry(true);
    }

    if (this.textFragment != null) {
      this.textFragment.updateText();
    }

  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 152 && resultCode == -1) {
      Credential credential = (Credential) data.getParcelableExtra("com.google.android.gms.credentials.Credential");
      if (credential != null && this.topFragment != null) {
        this.topFragment.setRequestedHintEmail(credential.getId());
      }
    }

  }

  public void onResume(Activity activity) {
    super.onResume(activity);
    ViewUtility.showKeyboard(this.getFocusView());
  }

  private void updateNextButton() {
    if (this.topFragment != null && this.bottomFragment != null) {
      this.bottomFragment.setNextButtonEnabled(!Utility.isNullOrEmpty(this.topFragment.getEmail()));
      this.bottomFragment.setNextButtonType(this.getButtonType());
    }
  }

  protected void logImpression() {
    if (this.bottomFragment != null) {
      AccountKitController.Logger.logUIEmailLoginShown(this.bottomFragment.getRetry());
    }
  }

  private EmailLoginContentController.OnCompleteListener getOnCompleteListener() {
    if (this.onCompleteListener == null) {
      this.onCompleteListener = new EmailLoginContentController.OnCompleteListener() {
        public void onNext(Context context, String buttonName) {
          if (EmailLoginContentController.this.topFragment != null) {
            String email = EmailLoginContentController.this.topFragment.getEmail();
            if (email != null) {
              email = email.trim();
              if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (EmailLoginContentController.this.headerFragment != null) {
                  EmailLoginContentController.this.headerFragment.setTitleResourceId(string.com_accountkit_email_invalid);
                }

                if (EmailLoginContentController.this.topFragment.emailViewLayout != null) {
                  EmailLoginContentController.this.topFragment.emailViewLayout.setError(context.getText(string.com_accountkit_email_invalid));
                }

              } else {
                if (EmailLoginContentController.this.topFragment.emailViewLayout != null) {
                  EmailLoginContentController.this.topFragment.emailViewLayout.setError((CharSequence) null);
                }

                Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.EMAIL_LOGIN_COMPLETE).putExtra(LoginFlowBroadcastReceiver.EXTRA_EMAIL, email);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
              }
            }
          }
        }
      };
    }

    return this.onCompleteListener;
  }

  static enum EmailSourceSelected {
    NO_SELECTABLE_EMAILS,
    SELECTED_CHANGED,
    SELECTED_NOT_USED,
    SELECTED_USED;

    private EmailSourceSelected() {
    }
  }

  static enum EmailSourceAppSupplied {
    NO_APP_SUPPLIED_EMAIL,
    APP_SUPPLIED_EMAIL_CHANGED,
    APP_SUPPLIED_EMAIL_USED;

    private EmailSourceAppSupplied() {
    }
  }

  public interface OnCompleteListener {
    void onNext(Context var1, String var2);
  }

  public static final class TopFragment extends ContentFragment {
    private static final String APP_SUPPLIED_EMAIL_KEY = "appSuppliedEmail";
    private static final String SELECTED_EMAIL_KEY = "selectedEmail";
    private AutoCompleteTextView emailView;
    private TextInputLayout emailViewLayout;
    private EmailLoginContentController.TopFragment.OnEmailChangedListener onEmailChangedListener;
    private EmailLoginContentController.OnCompleteListener onCompleteListener;

    public TopFragment() {
    }

    LoginFlowState getLoginFlowState() {
      return EmailLoginContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_email_login_top, container, false);
    }

    public void onStart() {
      super.onStart();
      this.fillEmail();
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.emailView = (AutoCompleteTextView) view.findViewById(id.com_accountkit_email);
      this.emailViewLayout = (TextInputLayout) view.findViewById(id.com_accountkit_email_layout);
      if (this.emailView != null) {
        this.emailView.addTextChangedListener(new TextWatcher() {
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          public void afterTextChanged(Editable s) {
            if (TopFragment.this.onEmailChangedListener != null) {
              TopFragment.this.onEmailChangedListener.onEmailChanged();
            }

          }
        });
        this.emailView.setOnEditorActionListener(new OnEditorActionListener() {
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == 5 && !Utility.isNullOrEmpty(TopFragment.this.getEmail())) {
              if (TopFragment.this.onCompleteListener != null) {
                TopFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.EMAIL_LOGIN_NEXT_KEYBOARD.name());
              }

              return true;
            } else {
              return false;
            }
          }
        });
        this.emailView.setInputType(33);
      }

    }

    private void fillEmail() {
      Activity activity = this.getActivity();
      List<String> deviceEmails = Utility.getDeviceEmailsIfAvailable(activity.getApplicationContext());
      if (deviceEmails != null) {
        this.emailView.setAdapter(new ArrayAdapter(activity, 17367050, deviceEmails));
        this.emailView.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
            TopFragment.this.setSelectedEmail(TopFragment.this.emailView.getText().toString());
          }
        });
      }

      String appSuppliedEmail = this.getAppSuppliedEmail();
      if (!Utility.isNullOrEmpty(appSuppliedEmail)) {
        this.emailView.setText(appSuppliedEmail);
        this.emailView.setSelection(appSuppliedEmail.length());
      } else if (Utility.hasGooglePlayServices(this.getActivity())) {
        GoogleApiClient googleApiClient = this.getGoogleApiClient();
        if (googleApiClient != null && this.getCurrentState() == EmailLoginContentController.LOGIN_FLOW_STATE && Utility.isNullOrEmpty(this.getEmail())) {
          HintRequest hintRequest = (new Builder()).setEmailAddressIdentifierSupported(true).build();
          PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);

          try {
            this.getActivity().startIntentSenderForResult(intent.getIntentSender(), 152, (Intent) null, 0, 0, 0);
          } catch (SendIntentException var8) {
            Log.w(TAG, "Failed to send intent", var8);
          }
        }
      }

    }

    public void setRequestedHintEmail(String email) {
      this.emailView.setText(email);
      this.emailView.setSelection(email.length());
    }

    public String getAppSuppliedEmail() {
      return this.getViewState().getString("appSuppliedEmail");
    }

    public void setAppSuppliedEmail(String appSuppliedEmail) {
      this.getViewState().putString("appSuppliedEmail", appSuppliedEmail);
    }

    @Nullable
    public String getEmail() {
      return this.emailView == null ? null : this.emailView.getText().toString();
    }

    public void setOnCompleteListener(@Nullable EmailLoginContentController.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    public void setOnEmailChangedListener(@Nullable EmailLoginContentController.TopFragment.OnEmailChangedListener onEmailChangedListener) {
      this.onEmailChangedListener = onEmailChangedListener;
    }

    public String getSelectedEmail() {
      return this.getViewState().getString("selectedEmail");
    }

    public void setSelectedEmail(String selectedEmail) {
      this.getViewState().putString("selectedEmail", selectedEmail);
    }

    public interface OnEmailChangedListener {
      void onEmailChanged();
    }
  }

  public static final class TextFragment extends TextContentFragment {
    private static final String ACCOUNT_KIT_URL = "https://www.accountkit.com/faq";

    public TextFragment() {
    }

    LoginFlowState getLoginFlowState() {
      return EmailLoginContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_email_login_text, container, false);
    }

    protected Spanned getText(String nextButtonText) {
      return Html.fromHtml(this.getString(string.com_accountkit_email_login_text, new Object[]{nextButtonText, AccountKit.getApplicationName(), "https://www.accountkit.com/faq"}));
    }
  }

  public static final class BottomFragment extends ContentFragment {
    private static final String RETRY_KEY = "retry";
    private Button nextButton;
    private boolean nextButtonEnabled;
    private ButtonType nextButtonType;
    private EmailLoginContentController.OnCompleteListener onCompleteListener;

    public BottomFragment() {
      this.nextButtonType = EmailLoginContentController.DEFAULT_BUTTON_TYPE;
    }

    LoginFlowState getLoginFlowState() {
      return EmailLoginContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return true;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(layout.com_accountkit_fragment_email_login_bottom, container, false);
      UIManager uiManager = this.getUIManager();
      if (uiManager instanceof SkinManager && ((SkinManager) uiManager).getSkin() == SkinManager.Skin.CONTEMPORARY) {
        View btn = view.findViewById(id.com_accountkit_next_button);
        ((ViewGroup) view).removeView(btn);
        view = btn;
        btn.setLayoutParams(new LayoutParams(-1, -2));
      }

      return view;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.nextButton = (Button) view.findViewById(id.com_accountkit_next_button);
      if (this.nextButton != null) {
        this.nextButton.setEnabled(this.nextButtonEnabled);
        this.nextButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            if (BottomFragment.this.onCompleteListener != null) {
              BottomFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.EMAIL_LOGIN_NEXT.name());
            }

          }
        });
      }

      this.updateButtonText();
    }

    public void setNextButtonEnabled(boolean enabled) {
      this.nextButtonEnabled = enabled;
      if (this.nextButton != null) {
        this.nextButton.setEnabled(enabled);
      }

    }

    public void setNextButtonType(ButtonType buttonType) {
      this.nextButtonType = buttonType;
      this.updateButtonText();
    }

    @StringRes
    public int getNextButtonTextId() {
      return this.getRetry() ? string.com_accountkit_resend_email_text : this.nextButtonType.getValue();
    }

    public void setOnCompleteListener(@Nullable EmailLoginContentController.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    public boolean getRetry() {
      return this.getViewState().getBoolean("retry", false);
    }

    public void setRetry(boolean retry) {
      this.getViewState().putBoolean("retry", retry);
      this.updateButtonText();
    }

    private void updateButtonText() {
      if (this.nextButton != null) {
        this.nextButton.setText(this.getNextButtonTextId());
      }

    }
  }
}
