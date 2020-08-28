package com.facebook.accountkit.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.custom.R.dimen;
import com.facebook.accountkit.custom.R.id;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class StateStackManager implements UIManager.UIManagerListener, AdvancedUIManager.AdvancedUIManagerListener, OnBackStackChangedListener {
  private final WeakReference<AccountKitActivity> activityRef;
  private final UIManager uiManager;
  private final AccountKitConfiguration configuration;
  private final Map<LoginFlowState, ContentController> contentControllerMap = new HashMap();
  private final List<StateStackManager.OnPopListener> onPopListeners = new ArrayList();
  private final List<StateStackManager.OnPushListener> onPushListeners = new ArrayList();
  private ContentController contentController;

  StateStackManager(AccountKitActivity activity, AccountKitConfiguration configuration) {
    this.activityRef = new WeakReference(activity);
    activity.getFragmentManager().addOnBackStackChangedListener(this);
    this.configuration = configuration;
    this.uiManager = configuration == null ? null : configuration.getUIManager();
    if (this.uiManager instanceof AdvancedUIManagerWrapper) {
      ((AdvancedUIManagerWrapper) this.uiManager).getAdvancedUIManager().setAdvancedUIManagerListener(this);
    } else if (this.uiManager != null) {
      this.uiManager.setUIManagerListener(this);
    }

  }

  public void onBack() {
    AccountKitActivity activity = (AccountKitActivity) this.activityRef.get();
    if (activity != null) {
      activity.onBackPressed();
    }
  }

  public void onBackStackChanged() {
    AccountKitActivity activity = (AccountKitActivity) this.activityRef.get();
    if (activity != null) {
      this.updateContentController(activity);
    }
  }

  public void onCancel() {
    AccountKitActivity activity = (AccountKitActivity) this.activityRef.get();
    if (activity != null) {
      activity.sendCancelResult();
    }
  }

  @Nullable
  ContentController getContentController() {
    return this.contentController;
  }

  void popBackStack(LoginFlowState toState, @Nullable StateStackManager.OnPopListener onPopListener) {
    AccountKitActivity activity = (AccountKitActivity) this.activityRef.get();
    if (activity != null) {
      if (onPopListener != null) {
        this.onPopListeners.add(onPopListener);
      }

      ContentController toContentController = this.ensureContentController(activity, toState, LoginFlowState.NONE, false);
      if (toState != LoginFlowState.PHONE_NUMBER_INPUT && toState != LoginFlowState.EMAIL_INPUT) {
        activity.getFragmentManager().popBackStack();
      } else {
        activity.getFragmentManager().popBackStack(0, 0);
      }

      activity.ensureNextButton(toContentController);
    }
  }

  void multiPopBackStack(StateStackManager.OnPopListener onPopListener) {
    AccountKitActivity activity = (AccountKitActivity) this.activityRef.get();
    if (activity != null) {
      if (onPopListener != null) {
        this.onPopListeners.add(onPopListener);
      }

      activity.getFragmentManager().popBackStack();
      activity.ensureNextButton((ContentController) null);
    }
  }

  void pushError(AccountKitActivity activity, LoginFlowManager loginFlowManager, LoginFlowState returnState, AccountKitError error, @Nullable StateStackManager.OnPushListener onPushListener) {
    this.uiManager.onError(error);
    this.pushState(activity, loginFlowManager, returnState, onPushListener);
  }

  StateStackManager.OnPushListener getErrorOnPushListener(@Nullable final String errorMessage) {
    return new StateStackManager.OnPushListener() {
      public void onContentControllerReady(ContentController contentController) {
        if (contentController instanceof LoginErrorContentController) {
          LoginErrorContentController loginErrorContentController = (LoginErrorContentController) contentController;
          loginErrorContentController.setErrorMessage(errorMessage);
        }

      }

      public void onContentPushed() {
      }
    };
  }

  void pushState(AccountKitActivity activity, LoginFlowManager loginFlowManager, @Nullable StateStackManager.OnPushListener onPushListener) {
    this.pushState(activity, loginFlowManager, LoginFlowState.NONE, onPushListener);
  }

  private void pushState(AccountKitActivity activity, LoginFlowManager loginFlowManager, LoginFlowState returnState, @Nullable StateStackManager.OnPushListener onPushListener) {
    LoginFlowState loginFlowState = loginFlowManager.getFlowState();
    ContentController fromContentController = this.getContentController();
    ContentController toContentController = this.ensureContentController(activity, loginFlowState, returnState, false);
    if (toContentController != null && fromContentController != toContentController) {
      NotificationChannel notificationChannel = null;
      if (loginFlowManager instanceof PhoneLoginFlowManager) {
        notificationChannel = ((PhoneLoginFlowManager) loginFlowManager).getNotificationChannel();
      }

      Object headerFragment;
      if ((loginFlowState != LoginFlowState.RESEND || !(toContentController instanceof ResendContentController)) && (loginFlowState != LoginFlowState.CODE_INPUT || !(toContentController instanceof LoginConfirmationCodeContentController)) && !(toContentController instanceof LoginErrorContentController)) {
        headerFragment = this.uiManager.getHeaderFragment(loginFlowState);
      } else {
        headerFragment = toContentController.getHeaderFragment();
      }

      Fragment contentCenterFragment = this.uiManager.getBodyFragment(loginFlowState);
      Fragment footerFragment = this.uiManager.getFooterFragment(loginFlowState);
      if (headerFragment == null) {
        headerFragment = BaseUIManager.getDefaultHeaderFragment(this.uiManager, loginFlowState, loginFlowManager.getLoginType(), notificationChannel);
      }

      if (contentCenterFragment == null) {
        contentCenterFragment = BaseUIManager.getDefaultBodyFragment(this.uiManager, loginFlowState);
      }

      if (footerFragment == null) {
        footerFragment = BaseUIManager.getDefaultFooterFragment(this.uiManager);
      }

      TextPosition textPosition = this.uiManager.getTextPosition(loginFlowState);
      if (toContentController instanceof ButtonContentController) {
        ButtonType buttonType = this.uiManager.getButtonType(loginFlowState);
        if (buttonType != null) {
          ((ButtonContentController) toContentController).setButtonType(buttonType);
        }
      }

      Fragment contentTopFragment = toContentController.getTopFragment();
      ContentFragment contentTextFragment = toContentController.getTextFragment();
      ContentFragment contentBottomFragment = toContentController.getBottomFragment();
      if (onPushListener != null) {
        this.onPushListeners.add(onPushListener);
        onPushListener.onContentControllerReady(toContentController);
      }

      if (textPosition == null) {
        textPosition = TextPosition.BELOW_BODY;
      }

      if (contentTextFragment != null) {
        int contentPaddingTopResourceId;
        int contentPaddingBottomResourceId;
        switch (textPosition) {
          case ABOVE_BODY:
            contentPaddingBottomResourceId = 0;
            contentPaddingTopResourceId = dimen.com_accountkit_vertical_spacer_small_height;
            break;
          case BELOW_BODY:
            contentPaddingBottomResourceId = dimen.com_accountkit_vertical_spacer_small_height;
            contentPaddingTopResourceId = 0;
            break;
          default:
            contentPaddingBottomResourceId = 0;
            contentPaddingTopResourceId = 0;
        }

        int contentPaddingTop = contentPaddingTopResourceId == 0 ? 0 : activity.getResources().getDimensionPixelSize(contentPaddingTopResourceId);
        int contentPaddingBottom = contentPaddingBottomResourceId == 0 ? 0 : activity.getResources().getDimensionPixelSize(contentPaddingBottomResourceId);
        if (contentTextFragment instanceof TextContentFragment) {
          TextContentFragment textContentFragment = (TextContentFragment) contentTextFragment;
          textContentFragment.setContentPaddingTop(contentPaddingTop);
          textContentFragment.setContentPaddingBottom(contentPaddingBottom);
        }
      }

      FragmentManager fm = activity.getFragmentManager();
      if (fromContentController != null) {
        activity.onContentControllerDismissed(fromContentController);
        if (fromContentController.isTransient()) {
          fm.popBackStack();
        }
      }

      if (ViewUtility.isSkin(this.uiManager, SkinManager.Skin.CONTEMPORARY)) {
        activity.ensureNextButton(toContentController);
      }

      FragmentTransaction transaction = fm.beginTransaction();
      activity.replace(transaction, id.com_accountkit_header_fragment, (Fragment) headerFragment);
      activity.replace(transaction, id.com_accountkit_content_top_fragment, contentTopFragment);
      activity.replace(transaction, id.com_accountkit_content_top_text_fragment, textPosition == TextPosition.ABOVE_BODY ? contentTextFragment : null);
      activity.replace(transaction, id.com_accountkit_content_center_fragment, contentCenterFragment);
      activity.replace(transaction, id.com_accountkit_content_bottom_text_fragment, textPosition == TextPosition.BELOW_BODY ? contentTextFragment : null);
      if (!ViewUtility.isSkin(this.uiManager, SkinManager.Skin.CONTEMPORARY)) {
        activity.replace(transaction, id.com_accountkit_content_bottom_fragment, contentBottomFragment);
        activity.replace(transaction, id.com_accountkit_footer_fragment, footerFragment);
      }

      transaction.addToBackStack((String) null);
      ViewUtility.hideKeyboard(activity);
      transaction.commit();
      toContentController.onResume(activity);
    }
  }

  void updateContentController(AccountKitActivity activity) {
    ContentFragment topFragment = this.getContentFragment(activity, id.com_accountkit_content_top_fragment);
    if (topFragment != null) {
      LoginFlowState loginFlowState = topFragment.getLoginFlowState();
      ContentController contentController = this.ensureContentController(activity, loginFlowState, LoginFlowState.NONE, true);
      if (contentController != null) {
        this.contentController = contentController;
        List<StateStackManager.OnPopListener> onPopListeners = new ArrayList(this.onPopListeners);
        this.onPopListeners.clear();
        Iterator var6 = onPopListeners.iterator();

        while (var6.hasNext()) {
          StateStackManager.OnPopListener onPopListener = (StateStackManager.OnPopListener) var6.next();
          onPopListener.onContentPopped();
        }

        List<StateStackManager.OnPushListener> onPushListeners = new ArrayList(this.onPushListeners);
        this.onPushListeners.clear();
        Iterator var10 = onPushListeners.iterator();

        while (var10.hasNext()) {
          StateStackManager.OnPushListener onPushListener = (StateStackManager.OnPushListener) var10.next();
          onPushListener.onContentPushed();
        }

      }
    }
  }

  @Nullable
  private ContentFragment getContentFragment(AccountKitActivity activity, int id) {
    Fragment fragment = activity.getFragmentManager().findFragmentById(id);
    return !(fragment instanceof ContentFragment) ? null : (ContentFragment) fragment;
  }

  @Nullable
  private ContentController ensureContentController(AccountKitActivity activity, LoginFlowState loginFlowState, LoginFlowState returnState, boolean updateFragments) {
    ContentController contentController = (ContentController) this.contentControllerMap.get(loginFlowState);
    if (contentController != null) {
      return contentController;
    } else {
      label39:
      switch (loginFlowState) {
        case NONE:
          return null;
        case PHONE_NUMBER_INPUT:
          contentController = new PhoneLoginContentController(this.configuration);
          break;
        case SENDING_CODE:
          contentController = new SendingCodeContentController(this.configuration);
          break;
        case SENT_CODE:
          switch (this.configuration.getLoginType()) {
            case PHONE:
              contentController = new PhoneSentCodeContentController(this.configuration);
              break label39;
            case EMAIL:
              contentController = new EmailSentCodeContentController(this.configuration);
              break label39;
            default:
              throw new RuntimeException("Unexpected login type: " + this.configuration.getLoginType().toString());
          }
        case ACCOUNT_VERIFIED:
          contentController = new AccountVerifiedContentController(this.configuration);
          break;
        case CONFIRM_ACCOUNT_VERIFIED:
          contentController = new ConfirmAccountVerifiedContentController(this.configuration);
          break;
        case CONFIRM_INSTANT_VERIFICATION_LOGIN:
          contentController = new VerifyingCodeContentController(this.configuration);
          break;
        case CODE_INPUT:
          contentController = new LoginConfirmationCodeContentController(this.configuration);
          break;
        case VERIFYING_CODE:
          contentController = new VerifyingCodeContentController(this.configuration);
          break;
        case VERIFIED:
          contentController = new VerifiedCodeContentController(this.configuration);
          break;
        case ERROR:
          contentController = new LoginErrorContentController(returnState, this.configuration);
          break;
        case EMAIL_INPUT:
          contentController = new EmailLoginContentController(this.configuration);
          break;
        case EMAIL_VERIFY:
          contentController = new EmailVerifyContentController(this.configuration);
          break;
        case RESEND:
          contentController = new ResendContentController(this.configuration);
          break;
        default:
          return null;
      }

      if (updateFragments) {
        Fragment headerFragment = activity.getFragmentManager().findFragmentById(id.com_accountkit_header_fragment);
        if (headerFragment instanceof TitleFragmentFactory.TitleFragment) {
          ((ContentController) contentController).setHeaderFragment((TitleFragmentFactory.TitleFragment) headerFragment);
        }

        ((ContentController) contentController).setTopFragment(this.getContentFragment(activity, id.com_accountkit_content_top_fragment));
        ((ContentController) contentController).setCenterFragment(this.getContentFragment(activity, id.com_accountkit_content_center_fragment));
        ((ContentController) contentController).setBottomFragment(this.getContentFragment(activity, id.com_accountkit_content_bottom_fragment));
        Fragment footerFragment = activity.getFragmentManager().findFragmentById(id.com_accountkit_footer_fragment);
        if (footerFragment instanceof TitleFragmentFactory.TitleFragment) {
          ((ContentController) contentController).setFooterFragment((TitleFragmentFactory.TitleFragment) footerFragment);
        }

        ((ContentController) contentController).onResume(activity);
      }

      this.contentControllerMap.put(loginFlowState, contentController);
      return (ContentController) contentController;
    }
  }

  private static enum FragmentType {
    BODY,
    FOOTER,
    HEADER;

    private FragmentType() {
    }
  }

  interface OnPushListener {
    void onContentControllerReady(ContentController var1);

    void onContentPushed();
  }

  interface OnPopListener {
    void onContentPopped();
  }
}
