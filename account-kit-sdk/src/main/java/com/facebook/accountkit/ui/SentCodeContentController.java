package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.custom.R.string;

abstract class SentCodeContentController extends ContentControllerBase {
  static final int COMPLETION_UI_DURATION_MS = 2000;
  @Nullable
  Handler delayedTransitionHandler;
  @Nullable
  Runnable delayedTransitionRunnable;
  private StaticContentFragmentFactory.StaticContentFragment bottomFragment;
  private StaticContentFragmentFactory.StaticContentFragment centerFragment;
  private TitleFragmentFactory.TitleFragment footerFragment;
  private TitleFragmentFactory.TitleFragment headerFragment;
  private StaticContentFragmentFactory.StaticContentFragment textFragment;
  private StaticContentFragmentFactory.StaticContentFragment topFragment;

  SentCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public void onResume(final Activity activity) {
    super.onResume(activity);
    this.cancelTransition();
    this.delayedTransitionHandler = new Handler();
    this.delayedTransitionRunnable = new Runnable() {
      public void run() {
        Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.SENT_CODE_COMPLETE);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
        SentCodeContentController.this.delayedTransitionHandler = null;
        SentCodeContentController.this.delayedTransitionRunnable = null;
      }
    };
    this.delayedTransitionHandler.postDelayed(this.delayedTransitionRunnable, 2000L);
  }

  public void onPause(Activity activity) {
    this.cancelTransition();
    super.onPause(activity);
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
      this.setCenterFragment(StaticContentFragmentFactory.create(this.configuration.getUIManager(), this.getLoginFlowState(), layout.com_accountkit_fragment_sent_code_center));
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
      this.setHeaderFragment(TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_sent_title));
    }

    return this.headerFragment;
  }

  public void setHeaderFragment(@Nullable TitleFragmentFactory.TitleFragment fragment) {
    this.headerFragment = fragment;
  }

  public LoginFlowState getLoginFlowState() {
    return LoginFlowState.SENT_CODE;
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

  void cancelTransition() {
    if (this.delayedTransitionHandler != null && this.delayedTransitionRunnable != null) {
      this.delayedTransitionHandler.removeCallbacks(this.delayedTransitionRunnable);
      this.delayedTransitionRunnable = null;
      this.delayedTransitionHandler = null;
    }

  }
}
