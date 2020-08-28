package com.facebook.accountkit.ui;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.AccountKitController;

final class VerifiedCodeContentController extends ContentControllerBase {
  private static final LoginFlowState LOGIN_FLOW_STATE;

  static {
    LOGIN_FLOW_STATE = LoginFlowState.VERIFIED;
  }

  private StaticContentFragmentFactory.StaticContentFragment bottomFragment;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private TitleFragmentFactory.TitleFragment footerFragment;
  private TitleFragmentFactory.TitleFragment headerFragment;
  private StaticContentFragmentFactory.StaticContentFragment textFragment;
  private StaticContentFragmentFactory.StaticContentFragment topFragment;

  VerifiedCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public ContentFragment getBottomFragment() {
    if (this.bottomFragment == null) {
      this.setBottomFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState()));
    }

    return this.bottomFragment;
  }

  public void setBottomFragment(@Nullable ContentFragment fragment) {
    if (fragment instanceof StaticContentFragmentFactory.StaticContentFragment) {
      this.bottomFragment = (StaticContentFragmentFactory.StaticContentFragment) fragment;
    }
  }

  public ContentFragment getCenterFragment() {
    if (this.centerFragment == null) {
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState(), layout.com_accountkit_fragment_verified_code_center));
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
      this.setFooterFragment(TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_return_title, AccountKit.getApplicationName()));
    }

    return this.footerFragment;
  }

  public void setFooterFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    this.footerFragment = fragment;
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.setHeaderFragment(TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_success_title));
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
    AccountKitController.Logger.logUIVerifiedCode(true, this.configuration.getLoginType());
  }
}
