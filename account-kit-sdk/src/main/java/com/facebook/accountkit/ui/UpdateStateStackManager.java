package com.facebook.accountkit.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKitUpdateResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.UpdateFlowBroadcastReceiver;
import com.facebook.accountkit.internal.AccountKitController;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

final class UpdateStateStackManager extends UpdateFlowBroadcastReceiver {
  private static final int COMPLETION_UI_DURATION_MS = 2000;
  private final WeakReference<AccountKitUpdateActivity> activityRef;
  private final AccountKitConfiguration configuration;
  private final Map<UpdateFlowState, ContentController> contentControllerMap = new HashMap();
  @Nullable
  private ContentController contentController;
  private UpdateFlowState updateFlowState;

  UpdateStateStackManager(AccountKitUpdateActivity activity, AccountKitConfiguration configuration) {
    this.activityRef = new WeakReference(activity);
    this.configuration = configuration;
    this.pushState(UpdateFlowState.PHONE_NUMBER_INPUT);
  }

  public UpdateFlowState getUpdateFlowState() {
    return this.updateFlowState;
  }

  @Nullable
  ContentController getContentController() {
    return this.contentController;
  }

  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (UpdateFlowBroadcastReceiver.ACTION_UPDATE.contentEquals(action)) {
      UpdateFlowBroadcastReceiver.Event event = (UpdateFlowBroadcastReceiver.Event) intent.getSerializableExtra(EXTRA_EVENT);
      String error = intent.getStringExtra(EXTRA_ERROR_MESSAGE);
      switch (event) {
        case UPDATE_START:
          PhoneNumber lastUsedPhoneNumber = (PhoneNumber) intent.getParcelableExtra(EXTRA_PHONE_NUMBER);
          this.pushState(UpdateFlowState.SENDING_CODE);
          AccountKitController.updatePhoneNumber(lastUsedPhoneNumber, this.configuration.getInitialAuthState());
          break;
        case SENT_CODE:
          this.pushState(UpdateFlowState.SENT_CODE);
          break;
        case SENT_CODE_COMPLETE:
          this.pushState(UpdateFlowState.CODE_INPUT);
          break;
        case CONFIRMATION_CODE_COMPLETE:
          this.pushState(UpdateFlowState.VERIFYING_CODE);
          String confirmationCode = intent.getStringExtra(EXTRA_CONFIRMATION_CODE);
          AccountKitController.continueUpdateWithCode(confirmationCode);
          break;
        case ACCOUNT_UPDATE_COMPLETE:
          this.pushState(UpdateFlowState.VERIFIED);
          final String finalUpdateState = intent.getStringExtra(EXTRA_UPDATE_STATE);
          (new Handler()).postDelayed(new Runnable() {
            public void run() {
              UpdateStateStackManager.this.finishActivity(finalUpdateState);
            }
          }, 2000L);
          break;
        case ERROR_UPDATE:
          this.pushState(UpdateFlowState.PHONE_NUMBER_INPUT_ERROR, error);
          break;
        case ERROR_CONFIRMATION_CODE:
          this.pushState(UpdateFlowState.CODE_INPUT_ERROR, error);
          break;
        case RETRY_CONFIRMATION_CODE:
          this.popState();
          ((UpdateConfirmationCodeContentController) this.contentController).setRetry(true);
          break;
        case RETRY:
          this.popState();
      }

    }
  }

  void popState() {
    AccountKitUpdateActivity activity = (AccountKitUpdateActivity) this.activityRef.get();
    if (activity != null) {
      UpdateFlowState oldState = this.updateFlowState;
      UpdateFlowState newState = UpdateFlowState.getBackState(oldState);
      this.updateFlowState = newState;
      this.contentController = this.ensureContentController(this.updateFlowState);
      switch (newState) {
        case NONE:
          if (oldState == UpdateFlowState.VERIFIED) {
            activity.sendResult();
          } else {
            activity.sendCancelResult();
          }
          break;
        case PHONE_NUMBER_INPUT:
          AccountKitController.cancelUpdate();
      }

      activity.getFragmentManager().popBackStack();
      activity.ensureNextButton(this.contentController);
    }
  }

  private void finishActivity(String finalUpdateState) {
    AccountKitUpdateActivity activity = (AccountKitUpdateActivity) this.activityRef.get();
    if (activity != null) {
      activity.setFinalUpdateState(finalUpdateState);
      activity.setUpdateResult(AccountKitUpdateResult.UpdateResult.SUCCESS);
      activity.sendResult();
    }
  }

  private void pushState(UpdateFlowState newState) {
    this.pushState(newState, (String) null);
  }

  private void pushState(UpdateFlowState newState, String error) {
    AccountKitUpdateActivity activity = (AccountKitUpdateActivity) this.activityRef.get();
    if (activity != null) {
      this.updateFlowState = newState;
      ContentController fromContentController = this.getContentController();
      this.contentController = this.ensureContentController(this.updateFlowState);
      if (this.contentController != null && fromContentController != this.contentController) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fromContentController != null) {
          fromContentController.onPause(activity);
          if (fromContentController.isTransient()) {
            fragmentManager.popBackStack();
          }
        }

        activity.updateUI(this.updateFlowState, this.contentController);
        if ((newState == UpdateFlowState.PHONE_NUMBER_INPUT_ERROR || newState == UpdateFlowState.CODE_INPUT_ERROR) && error != null) {
          ((UpdateErrorContentController) this.contentController).setErrorMessage(error);
        }

      }
    }
  }

  @Nullable
  private ContentController ensureContentController(UpdateFlowState updateFlowState) {
    ContentController contentController = (ContentController) this.contentControllerMap.get(updateFlowState);
    if (contentController != null) {
      return contentController;
    } else {
      switch (updateFlowState) {
        case NONE:
          return null;
        case PHONE_NUMBER_INPUT:
          contentController = new PhoneUpdateContentController(this.configuration);
          break;
        case SENDING_CODE:
          contentController = new SendingCodeContentController(this.configuration);
          break;
        case SENT_CODE:
          contentController = new PhoneUpdateSentCodeContentController(this.configuration);
          break;
        case CODE_INPUT:
          contentController = new UpdateConfirmationCodeContentController(this.configuration);
          break;
        case VERIFYING_CODE:
          contentController = new VerifyingCodeContentController(this.configuration);
          break;
        case VERIFIED:
          contentController = new VerifiedCodeContentController(this.configuration);
          break;
        case CODE_INPUT_ERROR:
        case PHONE_NUMBER_INPUT_ERROR:
          contentController = new UpdateErrorContentController(this.configuration);
          break;
        default:
          return null;
      }

      this.contentControllerMap.put(updateFlowState, contentController);
      return (ContentController) contentController;
    }
  }
}
