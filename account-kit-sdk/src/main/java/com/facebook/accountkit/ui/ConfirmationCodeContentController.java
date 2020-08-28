package com.facebook.accountkit.ui;

import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.Nullable;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.internal.Utility;

abstract class ConfirmationCodeContentController extends ContentControllerBase implements ButtonContentController {
  private static final LoginFlowState LOGIN_FLOW_STATE;
  private static final ButtonType DEFAULT_BUTTON_TYPE;
  private static final String NUMERIC_REGEX = "[0-9]+";
  private static final String ERROR_RESTART_KEY = "is_error_restart";

  static {
    LOGIN_FLOW_STATE = LoginFlowState.CODE_INPUT;
    DEFAULT_BUTTON_TYPE = ButtonType.CONTINUE;
  }

  TitleFragmentFactory.TitleFragment footerFragment;
  @Nullable
  ConfirmationCodeContentController.TitleFragment headerFragment;
  @Nullable
  ConfirmationCodeContentController.TopFragment topFragment;
  @Nullable
  PrivacyPolicyFragment bottomFragment;
  private ButtonType buttonType;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private StaticContentFragmentFactory.StaticContentFragment textFragment;

  ConfirmationCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
    this.buttonType = DEFAULT_BUTTON_TYPE;
  }

  private static char[] getConfirmationCodeToPaste(Context context) {
    String pasteText = getCurrentPasteText(context);
    return pasteText != null && pasteText.length() == 6 && pasteText.matches("[0-9]+") ? pasteText.toCharArray() : null;
  }

  private static String getCurrentPasteText(Context context) {
    if (context == null) {
      return null;
    } else {
      ClipboardManager clipboard = (ClipboardManager) context.getSystemService("clipboard");
      if (clipboard.hasPrimaryClip()) {
        Item item = clipboard.getPrimaryClip().getItemAt(0);
        if (item.getText() != null) {
          return item.getText().toString();
        }
      }

      return null;
    }
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(PrivacyPolicyFragment.create(this.configuration.getUIManager(), LOGIN_FLOW_STATE, this.getButtonType()));
    }

    return this.bottomFragment;
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
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState(), layout.com_accountkit_fragment_confirmation_code_center));
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
    return this.topFragment == null ? null : this.topFragment.getFocusView();
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

  public LoginFlowState getLoginFlowState() {
    return LoginFlowState.CODE_INPUT;
  }

  public ContentFragment getTextFragment() {
    if (this.textFragment == null) {
      this.setTextFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
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
      this.setTopFragment(new ConfirmationCodeContentController.TopFragment());
    }

    return this.topFragment;
  }

  public boolean isTransient() {
    return false;
  }

  void setPhoneNumber(@Nullable PhoneNumber phoneNumber) {
    if (this.headerFragment != null) {
      this.headerFragment.setPhoneNumber(phoneNumber);
    }

  }

  void setDetectedConfirmationCode(@Nullable String detectedConfirmationCode) {
    if (this.topFragment != null) {
      this.topFragment.setDetectedConfirmationCode(detectedConfirmationCode);
    }
  }

  void updateNextButton() {
    if (this.topFragment != null && this.bottomFragment != null) {
      this.bottomFragment.setNextButtonEnabled(this.topFragment.isConfirmationCodeValid());
      this.bottomFragment.setNextButtonType(this.getButtonType());
    }
  }

  void setRetry(boolean retry) {
    if (this.headerFragment != null) {
      this.headerFragment.setRetry(retry);
    }

    if (this.bottomFragment != null) {
      this.bottomFragment.setRetry(retry);
    }

    if (retry && this.topFragment != null) {
      this.topFragment.onRetry();
    }

  }

  protected void logImpression() {
    if (this.topFragment != null && this.bottomFragment != null) {
      AccountKitController.Logger.logUIConfirmationCodeShown(this.bottomFragment.getRetry());
    }
  }

  public static final class TopFragment extends ContentFragment {
    private static final String DETECTED_CONFIRMATION_CODE_KEY = "detectedConfirmationCode";
    private static final String TEXT_UPDATED_KEY = "textUpdated";
    @Nullable
    private EditText[] confirmationCodeViews;
    private ConfirmationCodeContentController.TopFragment.OnConfirmationCodeChangedListener onConfirmationCodeChangedListener;
    private PrivacyPolicyFragment.OnCompleteListener onCompleteListener;

    public TopFragment() {
    }

    LoginFlowState getLoginFlowState() {
      return ConfirmationCodeContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return true;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_confirmation_code_top, container, false);
    }

    @Nullable
    public View getFocusView() {
      if (this.confirmationCodeViews == null) {
        return null;
      } else {
        EditText[] var1 = this.confirmationCodeViews;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
          EditText confirmationCodeView = var1[var3];
          if (confirmationCodeView.getText().length() == 0) {
            return confirmationCodeView;
          }
        }

        return null;
      }
    }

    private void clearCodeWhenRestart() {
      if (this.confirmationCodeViews != null) {
        boolean isErrorRestart = this.getViewState().getBoolean("is_error_restart", false);
        if (isErrorRestart) {
          EditText[] var2 = this.confirmationCodeViews;
          int var3 = var2.length;

          for (int var4 = 0; var4 < var3; ++var4) {
            EditText confirmationCodeView = var2[var4];
            confirmationCodeView.setText("");
          }

          this.getViewState().putBoolean("is_error_restart", false);
        }

      }
    }

    public void onResume() {
      super.onResume();
      this.clearCodeWhenRestart();
      ViewUtility.showKeyboard(this.getFocusView());
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      UIManager uiManager = this.getUIManager();
      if (uiManager instanceof BaseUIManager) {
        LoginFlowState state = ((BaseUIManager) uiManager).getFlowState();
        if (state == LoginFlowState.ERROR) {
          this.confirmationCodeViews = null;
          this.getViewState().putBoolean("is_error_restart", true);
          return;
        }

        if (state == LoginFlowState.VERIFIED) {
          return;
        }
      }

      this.confirmationCodeViews = new EditText[]{(EditText) view.findViewById(id.com_accountkit_confirmation_code_1), (EditText) view.findViewById(id.com_accountkit_confirmation_code_2), (EditText) view.findViewById(id.com_accountkit_confirmation_code_3), (EditText) view.findViewById(id.com_accountkit_confirmation_code_4), (EditText) view.findViewById(id.com_accountkit_confirmation_code_5), (EditText) view.findViewById(id.com_accountkit_confirmation_code_6)};
      EditText[] var11 = this.confirmationCodeViews;
      int var5 = var11.length;

      for (int var6 = 0; var6 < var5; ++var6) {
        EditText confirmationCodeView = var11[var6];
        if (confirmationCodeView.getText().length() != 0) {
          confirmationCodeView.clearFocus();
        }
      }

      OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (actionId == 5 && TopFragment.this.isConfirmationCodeValid() && TopFragment.this.onCompleteListener != null) {
            TopFragment.this.onCompleteListener.onNext(v.getContext(), Buttons.ENTER_CONFIRMATION_CODE_KEYBOARD.name());
          }

          return true;
        }
      };
      OnKeyListener onKeyListener = new OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
          EditText confirmationCodeView = (EditText) v;
          if (keyCode >= 7 && keyCode <= 16 && event.getAction() == 0) {
            String text = Character.toString((char) event.getUnicodeChar());
            confirmationCodeView.setText(text);
            return true;
          } else if (keyCode == 67 && event.getAction() == 0) {
            if (confirmationCodeView.getText().length() == 0) {
              EditText previous = TopFragment.this.focusOnPrevious(confirmationCodeView);
              if (previous != null) {
                previous.setText("");
              }
            } else {
              confirmationCodeView.setText("");
            }

            return true;
          } else {
            return false;
          }
        }
      };
      EditText[] var14 = this.confirmationCodeViews;
      int var15 = var14.length;

      for (int var8 = 0; var8 < var15; ++var8) {
        final EditText confirmationCodeView = var14[var8];
        confirmationCodeView.setRawInputType(18);
        confirmationCodeView.setOnEditorActionListener(onEditorActionListener);
        confirmationCodeView.setOnKeyListener(onKeyListener);
        if (confirmationCodeView instanceof NotifyingEditText) {
          NotifyingEditText notifyingEditText = (NotifyingEditText) confirmationCodeView;
          notifyingEditText.setOnSoftKeyListener(onKeyListener);
          notifyingEditText.setPasteListener(new NotifyingEditText.PasteListener() {
            public void onTextPaste() {
              char[] code = ConfirmationCodeContentController.getConfirmationCodeToPaste(TopFragment.this.getActivity());
              if (code != null && TopFragment.this.confirmationCodeViews != null) {
                for (int i = 0; i < code.length; ++i) {
                  TopFragment.this.confirmationCodeViews[i].setText(String.valueOf(code[i]));
                }
              }

            }
          });
        }

        confirmationCodeView.addTextChangedListener(new TextWatcher() {
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          public void afterTextChanged(Editable s) {
            if (!TopFragment.this.getTextUpdated()) {
              TopFragment.this.setTextUpdated(true);
            }

            if (s.length() == 1) {
              TopFragment.this.focusOnNext(confirmationCodeView);
            }

            if (TopFragment.this.onConfirmationCodeChangedListener != null) {
              TopFragment.this.onConfirmationCodeChangedListener.onConfirmationCodeChanged();
            }

          }
        });
      }

      this.updateDetectedConfirmationCode();
      ViewUtility.showKeyboard(this.getFocusView());
    }

    @Nullable
    public String getConfirmationCode() {
      if (this.confirmationCodeViews == null) {
        return null;
      } else {
        StringBuilder confirmationCode = new StringBuilder();
        EditText[] var2 = this.confirmationCodeViews;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
          EditText confirmationCodeView = var2[var4];
          confirmationCode.append(confirmationCodeView.getText());
        }

        return confirmationCode.toString();
      }
    }

    @Nullable
    public String getDetectedConfirmationCode() {
      return this.getViewState().getString("detectedConfirmationCode");
    }

    public void setDetectedConfirmationCode(@Nullable String detectedConfirmationCode) {
      this.getViewState().putString("detectedConfirmationCode", detectedConfirmationCode);
      this.updateDetectedConfirmationCode();
    }

    public void setOnCompleteListener(@Nullable PrivacyPolicyFragment.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    public void setOnConfirmationCodeChangedListener(@Nullable ConfirmationCodeContentController.TopFragment.OnConfirmationCodeChangedListener onConfirmationCodeChangedListener) {
      this.onConfirmationCodeChangedListener = onConfirmationCodeChangedListener;
    }

    public void onRetry() {
      if (this.confirmationCodeViews != null) {
        EditText[] var1 = this.confirmationCodeViews;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
          EditText confirmationCodeView = var1[var3];
          confirmationCodeView.setText("");
        }

        if (this.confirmationCodeViews.length > 0) {
          this.confirmationCodeViews[0].requestFocus();
        }

      }
    }

    private boolean getTextUpdated() {
      return this.getViewState().getBoolean("textUpdated", false);
    }

    private void setTextUpdated(boolean textUpdated) {
      this.getViewState().putBoolean("textUpdated", textUpdated);
    }

    private int getConfirmationCodeViewIndex(View view) {
      if (this.confirmationCodeViews == null) {
        return -1;
      } else {
        if (view != null) {
          int length = this.confirmationCodeViews.length;

          for (int i = 0; i < length; ++i) {
            if (this.confirmationCodeViews[i] == view) {
              return i;
            }
          }
        }

        return -1;
      }
    }

    private EditText focusOnNext(View currentView) {
      if (this.confirmationCodeViews == null) {
        return null;
      } else {
        int confirmationCodeIndex = this.getConfirmationCodeViewIndex(currentView);
        if (confirmationCodeIndex < this.confirmationCodeViews.length - 1) {
          EditText nextView = this.confirmationCodeViews[confirmationCodeIndex + 1];
          nextView.requestFocus();
          return nextView;
        } else {
          this.confirmationCodeViews[this.confirmationCodeViews.length - 1].setSelection(1);
          return null;
        }
      }
    }

    private EditText focusOnPrevious(View currentView) {
      if (this.confirmationCodeViews == null) {
        return null;
      } else {
        int confirmationCodeIndex = this.getConfirmationCodeViewIndex(currentView);
        if (confirmationCodeIndex > 0) {
          EditText previousView = this.confirmationCodeViews[confirmationCodeIndex - 1];
          previousView.requestFocus();
          return previousView;
        } else {
          return null;
        }
      }
    }

    public boolean isConfirmationCodeValid() {
      if (this.confirmationCodeViews == null) {
        return false;
      } else {
        EditText[] var1 = this.confirmationCodeViews;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
          EditText confirmationCodeView = var1[var3];
          if (confirmationCodeView.getText().length() != 1) {
            return false;
          }
        }

        return true;
      }
    }

    private void updateDetectedConfirmationCode() {
      if (this.confirmationCodeViews != null) {
        String detectedConfirmationCode = this.getDetectedConfirmationCode();
        if (!Utility.isNullOrEmpty(detectedConfirmationCode)) {
          int length = detectedConfirmationCode.length();
          if (length == this.confirmationCodeViews.length) {
            EditText[] var3 = this.confirmationCodeViews;
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
              EditText confirmationCodeView = var3[var5];
              if (confirmationCodeView.getText().length() != 0) {
                return;
              }
            }

            for (int i = 0; i < length; ++i) {
              this.confirmationCodeViews[i].setText(Character.toString(detectedConfirmationCode.charAt(i)));
            }

            this.confirmationCodeViews[this.confirmationCodeViews.length - 1].setSelection(1);
          }
        }
      }
    }

    interface OnConfirmationCodeChangedListener {
      void onConfirmationCodeChanged();
    }
  }

  public abstract static class TitleFragment extends TitleFragmentFactory.TitleFragment {
    @Nullable
    ConfirmationCodeContentController.TitleFragment.OnCompleteListener onCompleteListener;
    @Nullable
    PhoneNumber phoneNumber;
    boolean retry = false;

    public TitleFragment() {
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_title, container, false);
    }

    void setPhoneNumber(PhoneNumber phoneNumber) {
      this.phoneNumber = phoneNumber;
      this.setPhoneNumberView();
    }

    void setRetry(boolean retry) {
      this.retry = retry;
      this.setPhoneNumberView();
    }

    void setRetryWithoutViewUpdate(boolean retry) {
      this.retry = retry;
    }

    void setOnCompleteListener(@Nullable ConfirmationCodeContentController.TitleFragment.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      this.setPhoneNumberView();
    }

    public void onResume() {
      super.onResume();
      this.setPhoneNumberView();
    }

    abstract void setPhoneNumberView();

    public interface OnCompleteListener {
      void onEdit(Context var1);
    }
  }
}
