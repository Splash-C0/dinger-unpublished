package com.facebook.accountkit.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.AccountKitController;

final class EmailVerifyContentController extends ContentControllerBase {
  private static final LoginFlowState LOGIN_FLOW_STATE;

  static {
    LOGIN_FLOW_STATE = LoginFlowState.EMAIL_VERIFY;
  }

  private EmailVerifyContentController.BottomFragment bottomFragment;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private TitleFragmentFactory.TitleFragment footerFragment;
  private TitleFragmentFactory.TitleFragment headerFragment;
  private StaticContentFragmentFactory.StaticContentFragment textFragment;
  private StaticContentFragmentFactory.StaticContentFragment topFragment;

  EmailVerifyContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(new EmailVerifyContentController.BottomFragment());
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof EmailVerifyContentController.BottomFragment) {
      this.bottomFragment = (EmailVerifyContentController.BottomFragment) fragment;
      this.bottomFragment.getViewState().putParcelable(ViewStateFragment.UI_MANAGER_KEY, this.configuration.getUIManager());
      this.bottomFragment.setOnCompleteListener(new EmailVerifyContentController.BottomFragment.OnCompleteListener() {
        public void onRetry(Context context) {
          Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.EMAIL_VERIFY_RETRY);
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
      });
    }
  }

  public ContentFragment getCenterFragment() {
    if (this.centerFragment == null) {
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState(), layout.com_accountkit_fragment_email_verify_center));
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
      this.footerFragment = new TitleFragmentFactory.TitleFragment();
    }

    return this.footerFragment;
  }

  public void setFooterFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    this.footerFragment = fragment;
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.headerFragment = TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_email_verify_title);
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
    AccountKitController.Logger.logUIEmailVerify(true);
  }

  public static final class BottomFragment extends ContentFragment {
    private EmailVerifyContentController.BottomFragment.OnCompleteListener onCompleteListener;

    public BottomFragment() {
    }

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(layout.com_accountkit_fragment_email_verify_bottom, container, false);
    }

    LoginFlowState getLoginFlowState() {
      return EmailVerifyContentController.LOGIN_FLOW_STATE;
    }

    boolean isKeyboardFragment() {
      return false;
    }

    protected void onViewReadyWithState(View view, Bundle viewState) {
      super.onViewReadyWithState(view, viewState);
      View retryButton = view.findViewById(id.com_accountkit_retry_email_button);
      if (retryButton != null) {
        retryButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            if (BottomFragment.this.onCompleteListener != null) {
              BottomFragment.this.onCompleteListener.onRetry(v.getContext());
            }

          }
        });
      }

      Button checkEmail = (Button) view.findViewById(id.com_accountkit_check_email_button);
      if (checkEmail != null) {
        checkEmail.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.APP_EMAIL");
            intent.addFlags(1073741824);

            try {
              BottomFragment.this.startActivity(intent);
            } catch (ActivityNotFoundException var4) {
            }

          }
        });
      }

    }

    public void setOnCompleteListener(@Nullable EmailVerifyContentController.BottomFragment.OnCompleteListener onCompleteListener) {
      this.onCompleteListener = onCompleteListener;
    }

    interface OnCompleteListener {
      void onRetry(Context var1);
    }
  }
}
