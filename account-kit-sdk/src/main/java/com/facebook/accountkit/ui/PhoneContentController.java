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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.PhoneNumber;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.PhoneNumberUtil.ValidationResult;

abstract class PhoneContentController extends ContentControllerBase implements ButtonContentController {
  private static final int RESOLVE_HINT_REQUEST_CODE = 152;
  private static final String COUNTRY_PHONE_PREFIX = "+";
  private static final LoginFlowState LOGIN_FLOW_STATE;
  private static final ButtonType DEFAULT_BUTTON_TYPE;

  static {
    LOGIN_FLOW_STATE = LoginFlowState.PHONE_NUMBER_INPUT;
    DEFAULT_BUTTON_TYPE = ButtonType.NEXT;
  }

  @Nullable
  PhoneContentController.TopFragment topFragment;
  @Nullable
  PhoneContentController.BottomFragment bottomFragment;
  @Nullable
  PhoneContentController.TextFragment textFragment;
  @Nullable
  TitleFragmentFactory.TitleFragment headerFragment;
  PhoneContentController.OnCompleteListener onCompleteListener;
  private ButtonType buttonType;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private TitleFragmentFactory.TitleFragment footerFragment;

  PhoneContentController(AccountKitConfiguration configuration) {
    super(configuration);
    this.buttonType = DEFAULT_BUTTON_TYPE;
  }

  static PhoneContentController.PhoneNumberSource getPhoneNumberSource(@Nullable PhoneNumber submittedPhoneNumber, @Nullable PhoneNumber appSuppliedPhoneNumber, @Nullable String devicePhoneNumber) {
    if (submittedPhoneNumber == null) {
      return PhoneContentController.PhoneNumberSource.UNKNOWN;
    } else {
      if (!Utility.isNullOrEmpty(devicePhoneNumber)) {
        if (appSuppliedPhoneNumber != null && devicePhoneNumber.equals(appSuppliedPhoneNumber.getRawPhoneNumber()) && devicePhoneNumber.equals(submittedPhoneNumber.getRawPhoneNumber())) {
          return PhoneContentController.PhoneNumberSource.APP_SUPPLIED_AND_DEVICE_PHONE_NUMBER;
        }

        if (devicePhoneNumber.equals(submittedPhoneNumber.getRawPhoneNumber())) {
          return PhoneContentController.PhoneNumberSource.DEVICE_PHONE_NUMBER;
        }
      }

      if (appSuppliedPhoneNumber != null && appSuppliedPhoneNumber.equals(submittedPhoneNumber)) {
        return PhoneContentController.PhoneNumberSource.APP_SUPPLIED_PHONE_NUMBER;
      } else {
        return devicePhoneNumber == null && appSuppliedPhoneNumber == null ? PhoneContentController.PhoneNumberSource.DEVICE_PHONE_NUMBER_AND_APP_NUMBER_NOT_SUPPLIED : PhoneContentController.PhoneNumberSource.DEVICE_PHONE_NUMBER_NOT_SUPPLIED;
      }
    }
  }

  abstract PhoneContentController.OnCompleteListener getOnCompleteListener();

  public PhoneContentController.BottomFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(new PhoneContentController.BottomFragment());
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof PhoneContentController.BottomFragment) {
      this.bottomFragment = (PhoneContentController.BottomFragment) fragment;
      this.bottomFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.bottomFragment.setOnCompleteListener(this.getOnCompleteListener());
      this.bottomFragment.setEnableSms(this.configuration.getEnableSms());
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
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState(), layout.com_accountkit_fragment_phone_login_center));
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
    return this.topFragment == null ? null : this.topFragment.phoneNumberView;
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

  public void setHeaderFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    this.headerFragment = fragment;
  }

  public LoginFlowState getLoginFlowState() {
    return LOGIN_FLOW_STATE;
  }

  @Nullable
  public ContentFragment getTextFragment() {
    if (this.configuration.getUIManager() != null && ViewUtility.isSkin(this.configuration.getUIManager(), SkinManager.Skin.CONTEMPORARY) && !this.configuration.getEnableSms()) {
      if (this.textFragment == null) {
        this.setTextFragment(new PhoneContentController.TextFragment());
      }

      return this.textFragment;
    } else {
      return null;
    }
  }

  public void setTextFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof PhoneContentController.TextFragment) {
      this.textFragment = (PhoneContentController.TextFragment) fragment;
      this.textFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.textFragment.setNextButtonTextProvider(new TextContentFragment.NextButtonTextProvider() {
        @Nullable
        public String getNextButtonText() {
          return PhoneContentController.this.bottomFragment == null ? null : PhoneContentController.this.textFragment.getResources().getText(PhoneContentController.this.bottomFragment.getNextButtonTextId()).toString();
        }
      });
    }
  }

  @Nullable
  public PhoneContentController.TopFragment getTopFragment() {
    if (this.topFragment == null) {
      this.setTopFragment(new PhoneContentController.TopFragment());
    }

    return this.topFragment;
  }

  public void setTopFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof PhoneContentController.TopFragment) {
      this.topFragment = (PhoneContentController.TopFragment) fragment;
      this.topFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.topFragment.setOnPhoneNumberChangedListener(new PhoneContentController.TopFragment.OnPhoneNumberChangedListener() {
        public void onPhoneNumberChanged() {
          PhoneContentController.this.updateNextButton();
        }
      });
      this.topFragment.setOnCompleteListener(this.getOnCompleteListener());
      if (this.configuration.getInitialPhoneNumber() != null) {
        this.topFragment.setAppSuppliedPhoneNumber(this.configuration.getInitialPhoneNumber());
      }

      if (this.configuration.getDefaultCountryCode() != null) {
        this.topFragment.setDefaultCountryCodeValue(this.configuration.getDefaultCountryCode());
      }

      if (this.configuration.getSmsBlacklist() != null) {
        this.topFragment.setSmsBlacklist(this.configuration.getSmsBlacklist());
      }

      if (this.configuration.getSmsWhitelist() != null) {
        this.topFragment.setSmsWhitelist(this.configuration.getSmsWhitelist());
      }

      this.topFragment.setReadPhoneStateEnabled(this.configuration.isReadPhoneStateEnabled());
      this.updateNextButton();
    }
  }

  public boolean isTransient() {
    return false;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 152 && resultCode == -1 && this.topFragment != null) {
      this.topFragment.setRequestedPhoneNumber(((Credential) data.getParcelableExtra("com.google.android.gms.credentials.Credential")).getId());
    }

  }

  public void onResume(Activity activity) {
    super.onResume(activity);
    ViewUtility.showKeyboard(this.getFocusView());
  }

  protected void logImpression() {
    if (this.topFragment != null && this.bottomFragment != null) {
      PhoneCountryCodeAdapter.ValueData initialCountryCodeValue = this.topFragment.getInitialCountryCodeValue();
      AccountKitController.Logger.logUIPhoneLoginShown(initialCountryCodeValue == null ? null : initialCountryCodeValue.countryCode, initialCountryCodeValue == null ? null : initialCountryCodeValue.countryCodeSource, this.bottomFragment.getRetry());
    }
  }

  private void updateNextButton() {
    if (this.topFragment != null && this.bottomFragment != null) {
      this.bottomFragment.setNextButtonEnabled(this.topFragment.isPhoneNumberValid());
      this.bottomFragment.setNextButtonType(this.getButtonType());
    }
  }

  static enum PhoneNumberSource {
    UNKNOWN,
    APP_SUPPLIED_PHONE_NUMBER,
    APP_SUPPLIED_AND_DEVICE_PHONE_NUMBER,
    DEVICE_PHONE_NUMBER,
    DEVICE_PHONE_NUMBER_AND_APP_NUMBER_NOT_SUPPLIED,
    DEVICE_PHONE_NUMBER_NOT_SUPPLIED;

    private PhoneNumberSource() {
    }
  }

  interface OnCompleteListener {
    void onNext(Context var1, Buttons var2);
  }

  public static final class TopFragment extends ContentFragment {
    private static final String APP_SUPPLIED_PHONE_NUMBER_KEY = "appSuppliedPhoneNumber";
    private static final String DEFAULT_COUNTRY_CODE_NUMBER = "defaultCountryCodeNumber";
    private static final String DEVICE_PHONE_NUMBER_KEY = "devicePhoneNumber";
    private static final String INITIAL_COUNTRY_CODE_VALUE_KEY = "initialCountryCodeValue";
    private static final String LAST_PHONE_NUMBER = "lastPhoneNumber";
    private static final String READ_PHONE_STATE_ENABLED = "readPhoneStateEnabled";
    private static final String SMS_BLACKLIST_KEY = "smsBlacklist";
    private static final String SMS_WHITELIST_KEY = "smsWhitelist";
    private boolean isPhoneNumberValid;
    @Nullable
    private EditText phoneNumberView;
    @Nullable
    private AccountKitSpinner countryCodeView;
    private PhoneCountryCodeAdapter countryCodeAdapter;
    @Nullable
    private PhoneContentController.OnCompleteListener onCompleteListener;
    @Nullable
    private PhoneContentController.TopFragment.OnPhoneNumberChangedListener onPhoneNumberChangedListener;

    public TopFragment() {
    }

    private static boolean isValidPhoneNumber(@Nullable com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phoneNumber) {
      if (phoneNumber == null) {
        return false;
      } else {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        return phoneNumberUtil.isValidNumber(phoneNumber) || phoneNumberUtil.isPossibleNumberForTypeWithReason(phoneNumber, PhoneNumberType.MOBILE) == ValidationResult.IS_POSSIBLE;
      }
    }

    private static String getPhoneNumberWithPrefix(String countryCode) {
      return "+" + countryCode;
    }

    LoginFlowState getLoginFlowState() {
      return PhoneContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_phone_login_top, container, false);
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.countryCodeView = (AccountKitSpinner) view.findViewById(id.com_accountkit_country_code);
      this.phoneNumberView = (EditText) view.findViewById(id.com_accountkit_phone_number);
      final Activity activity = this.getActivity();
      final EditText phoneNumberView = this.phoneNumberView;
      final AccountKitSpinner countryCodeView = this.countryCodeView;
      if (activity != null && phoneNumberView != null && countryCodeView != null) {
        this.countryCodeAdapter = new PhoneCountryCodeAdapter(activity, this.getUIManager(), this.getSmsBlacklist(), this.getSmsWhitelist());
        countryCodeView.setAdapter(this.countryCodeAdapter);
        PhoneNumber phoneNumberForDisplay = this.determinePhoneNumberForDisplay(activity);
        PhoneCountryCodeAdapter.ValueData initialCountryCodeValue = this.countryCodeAdapter.getInitialValue(phoneNumberForDisplay, this.getDefaultCountryCodeValue());
        this.setInitialCountryCodeValue(initialCountryCodeValue);
        countryCodeView.setSelection(initialCountryCodeValue.position);
        countryCodeView.setOnSpinnerEventsListener(new AccountKitSpinner.OnSpinnerEventsListener() {
          public void onSpinnerOpened() {
            AccountKitController.Logger.logUICountryCode(true, ((PhoneCountryCodeAdapter.ValueData) countryCodeView.getSelectedItem()).countryCode);
            ViewUtility.hideKeyboard(activity);
          }

          public void onSpinnerClosed() {
            AccountKitController.Logger.logUICountryCode(false, ((PhoneCountryCodeAdapter.ValueData) countryCodeView.getSelectedItem()).countryCode);
            TopFragment.this.setLastPhoneNumber(TopFragment.this.getPhoneNumber());
            String countryCode = ((PhoneCountryCodeAdapter.ValueData) countryCodeView.getSelectedItem()).countryCode;
            phoneNumberView.setText(PhoneContentController.TopFragment.getPhoneNumberWithPrefix(countryCode));
            phoneNumberView.setSelection(phoneNumberView.getText().length());
            ViewUtility.showKeyboard(phoneNumberView);
          }
        });
        phoneNumberView.addTextChangedListener(new PhoneNumberTextWatcher(initialCountryCodeValue.countryCode) {
          public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            String raw = s.toString();
            if (!TextUtils.isEmpty(raw) && raw.startsWith("+")) {
              if (TopFragment.this.onPhoneNumberChangedListener != null) {
                TopFragment.this.onPhoneNumberChangedListener.onPhoneNumberChanged();
              }

              TopFragment.this.setLastPhoneNumber(TopFragment.this.getPhoneNumber());
              TopFragment.this.updateFlag(raw);
            } else {
              TopFragment.this.isPhoneNumberValid = false;
              countryCodeView.performClick();
            }
          }
        });
        phoneNumberView.setOnEditorActionListener(new OnEditorActionListener() {
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == 5 && TopFragment.this.isPhoneNumberValid()) {
              if (TopFragment.this.onCompleteListener != null) {
                TopFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.PHONE_LOGIN_NEXT_KEYBOARD);
              }

              return true;
            } else {
              return false;
            }
          }
        });
        phoneNumberView.setRawInputType(18);
        if (LoginFlowState.PHONE_NUMBER_INPUT.equals(this.getCurrentState())) {
          ViewUtility.showKeyboard(phoneNumberView);
        }

        this.setPhoneNumberText(phoneNumberForDisplay);
      }
    }

    @Nullable
    private PhoneNumber determinePhoneNumberForDisplay(Activity context) {
      PhoneNumber phoneNumber = null;
      if (this.getLastPhoneNumber() != null) {
        phoneNumber = this.getLastPhoneNumber();
      } else if (this.getAppSuppliedPhoneNumber() != null) {
        phoneNumber = this.getAppSuppliedPhoneNumber();
      } else {
        if (this.getDevicePhoneNumber() != null) {
          phoneNumber = Utility.createPhoneNumber(this.getDevicePhoneNumber());
        }

        if (phoneNumber == null) {
          phoneNumber = Utility.createPhoneNumber(this.attemptToDiscoverPhoneNumber(context));
        }
      }

      return phoneNumber;
    }

    private void updateFlag(String phoneNumber) {
      if (this.phoneNumberView != null && this.countryCodeView != null) {
        PhoneCountryCodeAdapter.ValueData selectedFlag = (PhoneCountryCodeAdapter.ValueData) this.countryCodeView.getSelectedItem();
        int newFlagIndex = this.countryCodeAdapter.getIndexOfCountryCode(Utility.getCountryCode(phoneNumber));
        String newCountryCode = Integer.toString(PhoneNumberUtil.getInstance().getCountryCodeForRegion(Utility.getCountryCode(phoneNumber)));
        if (newFlagIndex > 0 && !selectedFlag.countryCode.equals(newCountryCode)) {
          this.countryCodeView.setSelection(newFlagIndex, true);
        }

      }
    }

    private void setPhoneNumberText(@Nullable PhoneNumber phoneNumber) {
      if (this.phoneNumberView != null && this.countryCodeView != null) {
        if (phoneNumber != null) {
          this.phoneNumberView.setText(phoneNumber.toString());
          this.updateFlag(phoneNumber.getCountryCode());
        } else if (this.getInitialCountryCodeValue() != null) {
          this.phoneNumberView.setText(getPhoneNumberWithPrefix(this.countryCodeAdapter.getItem(this.getInitialCountryCodeValue().position).countryCode));
        } else {
          this.phoneNumberView.setText("");
        }

        this.phoneNumberView.setSelection(this.phoneNumberView.getText().length());
      }
    }

    @Nullable
    private String attemptToDiscoverPhoneNumber(Activity context) {
      if (this.countryCodeView != null && this.isReadPhoneStateEnabled()) {
        String phoneNumber = Utility.readPhoneNumberIfAvailable(context.getApplicationContext());
        if (phoneNumber == null) {
          this.requestPhoneNumberFromGooglePlay(context);
        }

        return phoneNumber;
      } else {
        return null;
      }
    }

    private void requestPhoneNumberFromGooglePlay(Activity context) {
      if (this.getLastPhoneNumber() == null && Utility.hasGooglePlayServices(context)) {
        GoogleApiClient googleApiClient = this.getGoogleApiClient();
        if (googleApiClient != null && this.countryCodeAdapter.getIndexOfCountryCode(Utility.getCurrentCountry(context)) != -1) {
          HintRequest hintRequest = (new Builder()).setPhoneNumberIdentifierSupported(true).build();
          PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);

          try {
            context.startIntentSenderForResult(intent.getIntentSender(), 152, (Intent) null, 0, 0, 0);
          } catch (SendIntentException var6) {
          }
        }

      }
    }

    public void setOnCompleteListener(@Nullable PhoneContentController.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    private PhoneNumber getLastPhoneNumber() {
      return (PhoneNumber) this.getViewState().getParcelable("lastPhoneNumber");
    }

    public void setLastPhoneNumber(PhoneNumber lastPhoneNumber) {
      this.getViewState().putParcelable("lastPhoneNumber", lastPhoneNumber);
    }

    public boolean isReadPhoneStateEnabled() {
      return this.getViewState().getBoolean("readPhoneStateEnabled");
    }

    public void setReadPhoneStateEnabled(boolean isReadPhoneStateEnabled) {
      this.getViewState().putBoolean("readPhoneStateEnabled", isReadPhoneStateEnabled);
    }

    @Nullable
    public PhoneNumber getAppSuppliedPhoneNumber() {
      return (PhoneNumber) this.getViewState().getParcelable("appSuppliedPhoneNumber");
    }

    private void setAppSuppliedPhoneNumber(@Nullable PhoneNumber appSuppliedPhoneNumber) {
      this.getViewState().putParcelable("appSuppliedPhoneNumber", appSuppliedPhoneNumber);
    }

    @Nullable
    public String getDefaultCountryCodeValue() {
      return this.getViewState().getString("defaultCountryCodeNumber");
    }

    private void setDefaultCountryCodeValue(@Nullable String defaultCountryCodeValue) {
      this.getViewState().putString("defaultCountryCodeNumber", defaultCountryCodeValue);
    }

    @Nullable
    public String[] getSmsBlacklist() {
      return this.getViewState().getStringArray("smsBlacklist");
    }

    private void setSmsBlacklist(@Nullable String[] smsBlacklist) {
      this.getViewState().putStringArray("smsBlacklist", smsBlacklist);
    }

    @Nullable
    public String[] getSmsWhitelist() {
      return this.getViewState().getStringArray("smsWhitelist");
    }

    private void setSmsWhitelist(@Nullable String[] smsWhitelist) {
      this.getViewState().putStringArray("smsWhitelist", smsWhitelist);
    }

    @Nullable
    public String getDevicePhoneNumber() {
      return this.getViewState().getString("devicePhoneNumber");
    }

    private void setDevicePhoneNumber(@Nullable String devicePhoneNumber) {
      this.getViewState().putString("devicePhoneNumber", devicePhoneNumber);
    }

    @Nullable
    public PhoneCountryCodeAdapter.ValueData getInitialCountryCodeValue() {
      return (PhoneCountryCodeAdapter.ValueData) this.getViewState().getParcelable("initialCountryCodeValue");
    }

    private void setInitialCountryCodeValue(@Nullable PhoneCountryCodeAdapter.ValueData initialCountryCodeValue) {
      this.getViewState().putParcelable("initialCountryCodeValue", initialCountryCodeValue);
    }

    @Nullable
    public PhoneNumber getPhoneNumber() {
      PhoneNumber phoneNumber = null;
      if (this.phoneNumberView != null) {
        try {
          com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phonenumber = PhoneNumberUtil.getInstance().parse(this.phoneNumberView.getText().toString(), (String) null);
          String nationalNumber = (phonenumber.hasItalianLeadingZero() ? "0" : "") + String.valueOf(phonenumber.getNationalNumber());
          phoneNumber = new PhoneNumber(String.valueOf(phonenumber.getCountryCode()), nationalNumber, phonenumber.getCountryCodeSource().name());
        } catch (NumberParseException | IllegalArgumentException var4) {
        }
      }

      return phoneNumber;
    }

    public boolean isPhoneNumberValid() {
      if (this.phoneNumberView != null && this.countryCodeView != null) {
        String countryCode = "+" + ((PhoneCountryCodeAdapter.ValueData) this.countryCodeView.getSelectedItem()).countryCode;
        String raw = this.phoneNumberView.getText().toString();
        return raw.startsWith(countryCode) && raw.length() != countryCode.length() && this.getPhoneNumber() != null;
      } else {
        return false;
      }
    }

    public void setOnPhoneNumberChangedListener(@Nullable PhoneContentController.TopFragment.OnPhoneNumberChangedListener onPhoneNumberChangedListener) {
      this.onPhoneNumberChangedListener = onPhoneNumberChangedListener;
    }

    void setRequestedPhoneNumber(String phoneNumber) {
      com.google.i18n.phonenumbers.Phonenumber.PhoneNumber number = Utility.createI8nPhoneNumber(phoneNumber);
      this.setDevicePhoneNumber(phoneNumber);
      this.setPhoneNumberText(Utility.createPhoneNumber(phoneNumber));
    }

    interface OnPhoneNumberChangedListener {
      void onPhoneNumberChanged();
    }
  }

  public static final class TextFragment extends TextContentFragment {
    public static final String ACCOUNT_KIT_URL = "https://www.accountkit.com/faq";

    public TextFragment() {
    }

    LoginFlowState getLoginFlowState() {
      return PhoneContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_phone_login_text, container, false);
    }

    protected Spanned getText(String nextButtonText) {
      return Html.fromHtml(this.getString(string.com_accountkit_phone_whatsapp_login_text, new Object[]{AccountKit.getApplicationName(), "https://www.accountkit.com/faq"}));
    }
  }

  public static final class BottomFragment extends ContentFragment {
    private static final String RETRY_KEY = "retry";
    private boolean enableSms = true;
    @Nullable
    private Button nextButton;
    private boolean nextButtonEnabled;
    private ButtonType nextButtonType;
    @Nullable
    private PhoneContentController.OnCompleteListener onCompleteListener;
    @Nullable
    private WhatsAppButton whatsAppButton;

    public BottomFragment() {
      this.nextButtonType = PhoneContentController.DEFAULT_BUTTON_TYPE;
    }

    LoginFlowState getLoginFlowState() {
      return PhoneContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return true;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(layout.com_accountkit_fragment_phone_login_bottom, container, false);
      if (ViewUtility.isSkin(this.getUIManager(), SkinManager.Skin.CONTEMPORARY) && !this.enableSms) {
        View btn = view.findViewById(id.com_accountkit_use_whatsapp_button);
        ((ViewGroup) view).removeView(btn);
        view = btn;
        btn.setLayoutParams(new LayoutParams(-1, -2));
      }

      this.enableWhatsappEnabledUI(view);
      return view;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.nextButton = (Button) view.findViewById(id.com_accountkit_next_button);
      if (!this.enableSms) {
        if (this.nextButton != null) {
          this.nextButton.setVisibility(4);
        }

      } else {
        if (this.nextButton != null) {
          this.nextButton.setEnabled(this.nextButtonEnabled);
          this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              if (BottomFragment.this.onCompleteListener != null) {
                BottomFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.PHONE_LOGIN_NEXT);
              }

            }
          });
        }

        this.updateButtonText();
      }
    }

    public void setNextButtonEnabled(boolean enabled) {
      this.nextButtonEnabled = enabled;
      if (this.nextButton != null) {
        this.nextButton.setEnabled(enabled);
      }

      if (this.whatsAppButton != null && this.whatsAppButton.getVisibility() == 0) {
        this.whatsAppButton.setEnabled(enabled);
      }

    }

    public void setNextButtonType(ButtonType buttonType) {
      this.nextButtonType = buttonType;
      this.updateButtonText();
    }

    @StringRes
    public int getNextButtonTextId() {
      if (this.whatsAppButton != null && this.whatsAppButton.getVisibility() == 0) {
        return string.com_accountkit_button_use_sms;
      } else {
        return this.getRetry() ? string.com_accountkit_button_resend_sms : this.nextButtonType.getValue();
      }
    }

    public void setOnCompleteListener(@Nullable PhoneContentController.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    public boolean getRetry() {
      return this.getViewState().getBoolean("retry", false);
    }

    public void setRetry(boolean retry) {
      this.getViewState().putBoolean("retry", retry);
      this.updateButtonText();
    }

    public void setEnableSms(boolean enableSms) {
      this.enableSms = enableSms;
    }

    private void updateButtonText() {
      if (this.nextButton != null) {
        this.nextButton.setText(this.getNextButtonTextId());
      }

    }

    private void enableWhatsappEnabledUI(View view) {
      TextView legalText = (TextView) view.findViewById(id.com_accountkit_text);
      if (legalText != null) {
        legalText.setText(Html.fromHtml(this.getString(string.com_accountkit_phone_whatsapp_login_text, new Object[]{AccountKit.getApplicationName(), "https://www.accountkit.com/faq"})));
        legalText.setVisibility(0);
        legalText.setMovementMethod(new CustomLinkMovement(new CustomLinkMovement.OnURLClickedListener() {
          public void onURLClicked(String url) {
          }
        }));
      }

//      this.whatsAppButton = (WhatsAppButton) view.findViewById(id.com_accountkit_use_whatsapp_button);
      if (this.whatsAppButton != null) {
        this.whatsAppButton.setEnabled(this.nextButtonEnabled);
        this.whatsAppButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            if (BottomFragment.this.onCompleteListener != null) {
              BottomFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.PHONE_LOGIN_USE_WHATSAPP);
            }

          }
        });
        this.whatsAppButton.setVisibility(0);
      }
      this.setNextButtonType(ButtonType.USE_SMS);
    }
  }
}
