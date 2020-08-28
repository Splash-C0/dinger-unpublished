package com.facebook.accountkit.ui;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.custom.R.string;
import com.facebook.accountkit.internal.AccountKitController;

final class PhoneLoginContentController extends PhoneContentController {
  PhoneLoginContentController(AccountKitConfiguration configuration) {
    super(configuration);
    AccountKitController.initializeLogin();
  }

  public TitleFragmentFactory.TitleFragment getHeaderFragment() {
    if (this.headerFragment == null) {
      this.setHeaderFragment(TitleFragmentFactory.create(this.configuration.getUIManager(), string.com_accountkit_phone_login_title));
    }

    return this.headerFragment;
  }

  void setRetry() {
    if (this.headerFragment != null) {
      this.headerFragment.setTitleResourceId(string.com_accountkit_phone_login_retry_title);
    }

    if (this.bottomFragment != null) {
      this.bottomFragment.setRetry(true);
    }

    if (this.textFragment != null) {
      this.textFragment.updateText();
    }

  }

  PhoneContentController.OnCompleteListener getOnCompleteListener() {
    if (this.onCompleteListener == null) {
      this.onCompleteListener = new PhoneContentController.OnCompleteListener() {
        public void onNext(Context context, Buttons button) {
          if (PhoneLoginContentController.this.topFragment != null && PhoneLoginContentController.this.bottomFragment != null) {
            PhoneNumber phoneNumber = PhoneLoginContentController.this.topFragment.getPhoneNumber();
            if (phoneNumber != null) {
              NotificationChannel notificationChannel = Buttons.PHONE_LOGIN_USE_WHATSAPP.equals(button) ? NotificationChannel.WHATSAPP : NotificationChannel.SMS;
              Intent intent = (new Intent(LoginFlowBroadcastReceiver.ACTION_UPDATE)).putExtra(LoginFlowBroadcastReceiver.EXTRA_EVENT, LoginFlowBroadcastReceiver.Event.PHONE_LOGIN_COMPLETE).putExtra(LoginFlowBroadcastReceiver.EXTRA_PHONE_NUMBER, phoneNumber).putExtra(LoginFlowBroadcastReceiver.EXTRA_NOTIFICATION_CHANNEL, notificationChannel);
              LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
          }
        }
      };
    }

    return this.onCompleteListener;
  }
}
