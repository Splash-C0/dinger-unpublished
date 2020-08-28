package com.facebook.accountkit.ui;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.UpdateFlowBroadcastReceiver;
import com.facebook.accountkit.custom.R.string;

final class PhoneUpdateContentController extends PhoneContentController {
  PhoneUpdateContentController(AccountKitConfiguration configuration) {
    super(configuration);
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.setHeaderFragment(TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_phone_update_title));
    }

    return this.headerFragment;
  }

  PhoneContentController.OnCompleteListener getOnCompleteListener() {
    if (this.onCompleteListener == null) {
      this.onCompleteListener = new PhoneContentController.OnCompleteListener() {
        public void onNext(Context context, Buttons button) {
          if (PhoneUpdateContentController.this.topFragment != null && PhoneUpdateContentController.this.bottomFragment != null) {
            PhoneNumber phoneNumber = PhoneUpdateContentController.this.topFragment.getPhoneNumber();
            if (phoneNumber != null) {
              Intent intent = (new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.UPDATE_START).putExtra(UpdateFlowBroadcastReceiver.EXTRA_PHONE_NUMBER, phoneNumber);
              LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
          }
        }
      };
    }

    return this.onCompleteListener;
  }
}
