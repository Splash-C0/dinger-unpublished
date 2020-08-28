package com.facebook.accountkit.internal;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.PhoneNumber;

final class UpdateManager {
  private static final String SAVED_UPDATE_MODEL = "accountkitUpdateModel";
  private final InternalLogger logger;
  private final LocalBroadcastManager localBroadcastManager;
  @Nullable
  private volatile PhoneUpdateController currentPhoneUpdateController;
  @Nullable
  private volatile Activity currentActivity;
  private volatile boolean isActivityAvailable = false;

  UpdateManager(InternalLogger internalLogger, LocalBroadcastManager localBroadcastManager) {
    this.logger = internalLogger;
    this.localBroadcastManager = localBroadcastManager;
  }

  @Nullable
  PhoneUpdateModelImpl updatePhoneNumber(PhoneNumber phoneNumber, @Nullable String initialAuthState) {
    Utility.assertUIThread();
    AccessToken accessToken = AccountKit.getCurrentAccessToken();
    if (accessToken == null) {
      return null;
    } else {
      this.cancelExisting();
      PhoneUpdateModelImpl updateModel = new PhoneUpdateModelImpl(phoneNumber);
      PhoneUpdateController updateHandler = new PhoneUpdateController(this, updateModel);
      updateHandler.update(initialAuthState);
      this.currentPhoneUpdateController = updateHandler;
      return updateModel;
    }
  }

  void continueWithCode(String code) {
    Utility.assertUIThread();
    AccessToken accessToken = AccountKit.getCurrentAccessToken();
    if (accessToken != null) {
      PhoneUpdateModelImpl updateModel = this.getCurrentUpdateModel();
      if (updateModel != null) {
        try {
          updateModel.setConfirmationCode(code);
          this.handle(updateModel);
        } catch (AccountKitException var5) {
          if (Utility.isDebuggable(AccountKitController.getApplicationContext())) {
            throw var5;
          }
        }

      }
    }
  }

  void cancelExisting() {
    if (this.currentPhoneUpdateController != null) {
      this.currentPhoneUpdateController.onCancel();
    }

  }

  InternalLogger getLogger() {
    return this.logger;
  }

  void onActivityCreate(Activity activity, Bundle savedInstanceState) {
    this.isActivityAvailable = true;
    this.currentActivity = activity;
    this.logger.onActivityCreate(savedInstanceState);
    if (savedInstanceState != null) {
      PhoneUpdateModelImpl updateModel = (PhoneUpdateModelImpl) savedInstanceState.getParcelable("accountkitUpdateModel");
      if (updateModel != null) {
        this.continueWith(updateModel);
      }
    }

  }

  void onActivityDestroy(Activity activity) {
    if (this.currentActivity == activity) {
      this.isActivityAvailable = false;
      this.currentActivity = null;
      this.currentPhoneUpdateController = null;
      AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask((AccountKitGraphRequestAsyncTask) null);
    }
  }

  void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    if (this.currentActivity == activity) {
      this.logger.saveInstanceState(outState);
      if (this.currentPhoneUpdateController != null) {
        outState.putParcelable("accountkitUpdateModel", this.currentPhoneUpdateController.getUpdateModel());
      }

    }
  }

  boolean isActivityAvailable() {
    return this.isActivityAvailable;
  }

  void clearUpdate() {
    this.currentPhoneUpdateController = null;
  }

  LocalBroadcastManager getLocalBroadcastManager() {
    return this.localBroadcastManager;
  }

  private void handle(PhoneUpdateModelImpl updateModel) {
    Utility.assertUIThread();
    if (this.currentPhoneUpdateController != null) {
      switch (updateModel.getStatus()) {
        case EMPTY:
        case SUCCESS:
        default:
          break;
        case PENDING:
          this.currentPhoneUpdateController.onPending();
          break;
        case CANCELLED:
          this.currentPhoneUpdateController.onCancel();
          break;
        case ERROR:
          this.currentPhoneUpdateController.onError(updateModel.getError());
      }

    }
  }

  @Nullable
  private PhoneUpdateModelImpl getCurrentUpdateModel() {
    return this.currentPhoneUpdateController == null ? null : this.currentPhoneUpdateController.getUpdateModel();
  }

  private void continueWith(PhoneUpdateModelImpl updateModel) {
    Utility.assertUIThread();
    this.currentPhoneUpdateController = new PhoneUpdateController(this, updateModel);
    this.handle(updateModel);
  }
}
