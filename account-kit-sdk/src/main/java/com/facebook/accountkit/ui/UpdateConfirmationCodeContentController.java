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

import com.facebook.accountkit.UpdateFlowBroadcastReceiver;
import com.facebook.accountkit.custom.R.string;

final class UpdateConfirmationCodeContentController extends ConfirmationCodeContentController {
  private UpdateConfirmationCodeContentController.OnCompleteListener onCompleteListener;

  UpdateConfirmationCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.setHeaderFragment(UpdateConfirmationCodeContentController.TitleFragment.create(this.configuration.getUIManager(), string.com_accountkit_confirmation_code_title));
    }

    return this.headerFragment;
  }

  public void setHeaderFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    if (fragment instanceof UpdateConfirmationCodeContentController.TitleFragment) {
      this.headerFragment = (UpdateConfirmationCodeContentController.TitleFragment) fragment;
      this.headerFragment.setOnCompleteListener(this.getOnCompleteListener());
    }
  }

  public void setTopFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof ConfirmationCodeContentController.TopFragment) {
      this.topFragment = (ConfirmationCodeContentController.TopFragment) fragment;
      this.topFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      ConfirmationCodeContentController.TopFragment.OnConfirmationCodeChangedListener onConfirmationCodeChangedListener = new ConfirmationCodeContentController.TopFragment.OnConfirmationCodeChangedListener() {
        public void onConfirmationCodeChanged() {
          UpdateConfirmationCodeContentController.this.updateNextButton();
        }
      };
      this.topFragment.setOnConfirmationCodeChangedListener(onConfirmationCodeChangedListener);
      this.topFragment.setOnCompleteListener(this.getOnCompleteListener());
    }
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof PrivacyPolicyFragment) {
      this.bottomFragment = (PrivacyPolicyFragment) fragment;
      this.bottomFragment.setOnCompleteListener(this.getOnCompleteListener());
      this.bottomFragment.setRetryVisible(false);
      this.updateNextButton();
    }
  }

  private UpdateConfirmationCodeContentController.OnCompleteListener getOnCompleteListener() {
    if (this.onCompleteListener == null) {
      this.onCompleteListener = new UpdateConfirmationCodeContentController.OnCompleteListener();
    }

    return this.onCompleteListener;
  }

  public static final class TitleFragment extends ConfirmationCodeContentController.TitleFragment {
    public TitleFragment() {
    }

    public static UpdateConfirmationCodeContentController.TitleFragment create(UIManager uiManager, int titleResourceId, @Nullable String... args) {
      UpdateConfirmationCodeContentController.TitleFragment titleFragment = new UpdateConfirmationCodeContentController.TitleFragment();
      titleFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, uiManager);
      titleFragment.setTitleResourceId(titleResourceId, args);
      return titleFragment;
    }

    void setPhoneNumberView() {
      if (this.isAdded()) {
        if (this.phoneNumber != null) {
          SpannableString span = new SpannableString(this.getString(string.com_accountkit_enter_code_sent_to, new Object[]{this.phoneNumber.toString()}));
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
          int start = span.toString().indexOf(this.phoneNumber.toString());
          int end = start + this.phoneNumber.toString().length();
          span.setSpan(clickSpan, start, end, 33);
          this.titleView.setText(span);
          this.titleView.setMovementMethod(LinkMovementMethod.getInstance());
        }

      }
    }
  }

  private class OnCompleteListener implements PrivacyPolicyFragment.OnCompleteListener, ConfirmationCodeContentController.TitleFragment.OnCompleteListener {
    private OnCompleteListener() {
    }

    public void onNext(Context context, String buttonName) {
      if (UpdateConfirmationCodeContentController.this.topFragment != null && UpdateConfirmationCodeContentController.this.bottomFragment != null) {
        String confirmationCode = UpdateConfirmationCodeContentController.this.topFragment.getConfirmationCode();
        Intent intent = (new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.CONFIRMATION_CODE_COMPLETE).putExtra(UpdateFlowBroadcastReceiver.EXTRA_CONFIRMATION_CODE, confirmationCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
      }
    }

    public void onRetry(Context context) {
    }

    public void onEdit(Context context) {
      Intent intent = (new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_UPDATE);
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
  }
}
