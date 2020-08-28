package com.facebook.accountkit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.UpdateFlowBroadcastReceiver;
import com.facebook.accountkit.internal.AccountKitController;

final class PhoneUpdateSentCodeContentController extends SentCodeContentController {
  PhoneUpdateSentCodeContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public void onResume(final Activity activity) {
    super.onResume(activity);
    this.cancelTransition();
    this.delayedTransitionHandler = new Handler();
    this.delayedTransitionRunnable = new Runnable() {
      public void run() {
        Intent intent = new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE);
        intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.SENT_CODE_COMPLETE);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
        PhoneUpdateSentCodeContentController.this.delayedTransitionHandler = null;
        PhoneUpdateSentCodeContentController.this.delayedTransitionRunnable = null;
      }
    };
    this.delayedTransitionHandler.postDelayed(this.delayedTransitionRunnable, 2000L);
  }

  protected void logImpression() {
    AccountKitController.Logger.logUISentCode(true, LoginType.PHONE);
  }
}
