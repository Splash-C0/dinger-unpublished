package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.custom.R.id;
import com.facebook.accountkit.custom.R.layout;
import com.facebook.accountkit.internal.InternalAccountKitError;

abstract class AccountKitActivityBase extends AppCompatActivity {
  public static final String ACCOUNT_KIT_ACTIVITY_CONFIGURATION;
  private static final String TAG;
  private static final String VIEW_STATE_KEY;

  static {
    ACCOUNT_KIT_ACTIVITY_CONFIGURATION = AccountKitConfiguration.TAG;
    TAG = AccountKitUpdateActivity.class.getSimpleName();
    VIEW_STATE_KEY = TAG + ".viewState";
  }

  private final Bundle viewState = new Bundle();
  @Nullable
  AccountKitConfiguration configuration;
  UIManager uiManager;
  AccountKitError error;
  private KeyboardObserver keyboardObserver;

  AccountKitActivityBase() {
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = this.getIntent();
    this.configuration = (AccountKitConfiguration) intent.getParcelableExtra(ACCOUNT_KIT_ACTIVITY_CONFIGURATION);
    if (this.configuration == null) {
      this.error = new AccountKitError(AccountKitError.Type.INITIALIZATION_ERROR, InternalAccountKitError.INVALID_INTENT_EXTRAS_CONFIGURATION);
      this.sendResult();
    } else {
      this.uiManager = this.configuration.getUIManager();
      if (!ViewUtility.doesTextColorContrast(this, this.configuration.getUIManager())) {
        this.error = new AccountKitError(AccountKitError.Type.INITIALIZATION_ERROR, InternalAccountKitError.INVALID_BACKGROUND_CONTRACT);
        this.sendResult();
      } else {
        int themeId;
        if ((themeId = this.configuration.getUIManager().getThemeId()) != -1) {
          this.setTheme(themeId);
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (!ViewUtility.isTablet(this)) {
          this.setRequestedOrientation(1);
        }

        this.setContentView(layout.com_accountkit_activity_layout);
        final ConstrainedLinearLayout contentView = (ConstrainedLinearLayout) this.findViewById(id.com_accountkit_content_view);
        View scrollView = this.findViewById(id.com_accountkit_scroll_view);
        if (contentView != null && scrollView != null && contentView.getMinHeight() < 0) {
          View rootView = contentView.getRootView();
          if (rootView != null) {
            this.keyboardObserver = new KeyboardObserver(scrollView);
            KeyboardObserver.OnVisibleFrameChangedListener onVisibleFrameChangedListener = new KeyboardObserver.OnVisibleFrameChangedListener() {
              public void onVisibleFrameChanged(Rect visibleFrame) {
                int minHeight = visibleFrame.height();
                if (minHeight >= 0) {
                  contentView.setMinHeight(minHeight);
                }

              }
            };
            this.keyboardObserver.setOnVisibleFrameChangedListener(onVisibleFrameChangedListener);
          }
        }

        if (savedInstanceState != null) {
          this.viewState.putAll(savedInstanceState.getBundle(VIEW_STATE_KEY));
        }

        ViewUtility.applyThemeBackground(this, this.configuration.getUIManager(), this.findViewById(id.com_accountkit_background));
      }
    }
  }

  protected void onSaveInstanceState(Bundle outState) {
    outState.putBundle(VIEW_STATE_KEY, this.viewState);
    super.onSaveInstanceState(outState);
  }

  protected void onDestroy() {
    super.onDestroy();
    if (this.keyboardObserver != null) {
      this.keyboardObserver.setOnVisibleFrameChangedListener((KeyboardObserver.OnVisibleFrameChangedListener) null);
      this.keyboardObserver = null;
    }

  }

  abstract void sendResult();

  void ensureNextButton(ContentController contentController) {
    if (ViewUtility.isSkin(this.uiManager, SkinManager.Skin.CONTEMPORARY)) {
      FragmentManager fm = this.getFragmentManager();
      if (contentController == null) {
        FragmentTransaction removeBtnTransaction = fm.beginTransaction();
        Fragment fragment = this.remove(removeBtnTransaction, id.com_accountkit_content_bottom_fragment);
        if (fragment == null) {
          this.remove(removeBtnTransaction, id.com_accountkit_content_bottom_keyboard_fragment);
        }

        removeBtnTransaction.commit();
      } else {
        ContentFragment contentBottomFragment = contentController.getBottomFragment();
        FragmentTransaction btnTransaction = fm.beginTransaction();
        if (contentBottomFragment.isKeyboardFragment()) {
          this.remove(btnTransaction, id.com_accountkit_content_bottom_fragment);
          this.replace(btnTransaction, id.com_accountkit_content_bottom_keyboard_fragment, contentBottomFragment);
        } else {
          this.remove(btnTransaction, id.com_accountkit_content_bottom_keyboard_fragment);
          this.replace(btnTransaction, id.com_accountkit_content_bottom_fragment, contentBottomFragment);
        }

        btnTransaction.commit();
      }
    }
  }

  void replace(FragmentTransaction transaction, int containerViewId, Fragment fragment) {
    FragmentManager fragmentManager = this.getFragmentManager();
    Fragment oldFragment = fragmentManager.findFragmentById(containerViewId);
    if (oldFragment != fragment) {
      transaction.replace(containerViewId, fragment);
    }

  }

  Fragment remove(FragmentTransaction transaction, int containerViewId) {
    FragmentManager fragmentManager = this.getFragmentManager();
    Fragment fragment = fragmentManager.findFragmentById(containerViewId);
    if (fragment != null) {
      transaction.remove(fragment);
    }

    return fragment;
  }
}
