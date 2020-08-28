package com.facebook.accountkit.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

final class SeamlessLoginClient implements ServiceConnection {
  private static final int MIN_PROTOCOL_VERSION = 20161017;
  private static final int REQUEST_MESSAGE = 65544;
  private static final int REPLY_MESSAGE = 65545;
  private final Context context;
  private final Handler handler;
  private final String applicationId;
  private final InternalLogger logger;
  private SeamlessLoginClient.CompletedListener listener;
  private boolean running;
  private Messenger sender;

  public SeamlessLoginClient(Context context, String applicationId, InternalLogger internalLogger) {
    this.context = context;
    this.applicationId = applicationId;
    this.logger = internalLogger;
    this.handler = new Handler() {
      public void handleMessage(Message message) {
        SeamlessLoginClient.this.handleMessage(message);
      }
    };
  }

  public void setCompletedListener(SeamlessLoginClient.CompletedListener listener) {
    this.listener = listener;
  }

  public boolean start() {
    if (this.running) {
      return false;
    } else if (!NativeProtocol.validateApplicationForService()) {
      return false;
    } else if (!NativeProtocol.validateProtocolVersionForService(20161017)) {
      return false;
    } else {
      Intent intent = NativeProtocol.createPlatformServiceIntent(this.context);
      if (intent == null) {
        return false;
      } else {
        this.running = true;
        this.context.bindService(intent, this, 1);
        return true;
      }
    }
  }

  public boolean isRunning() {
    return this.running;
  }

  public void onServiceConnected(ComponentName name, IBinder service) {
    this.sender = new Messenger(service);
    this.sendMessage();
  }

  public void onServiceDisconnected(ComponentName name) {
    this.sender = null;

    try {
      this.context.unbindService(this);
    } catch (IllegalArgumentException var3) {
    }

    this.callback((Bundle) null);
  }

  private void sendMessage() {
    Bundle data = new Bundle();
    data.putString("com.facebook.platform.extra.APPLICATION_ID", this.applicationId);
    Message request = Message.obtain((Handler) null, 65544);
    request.arg1 = 20161017;
    request.setData(data);
    request.replyTo = new Messenger(this.handler);

    try {
      this.sender.send(request);
    } catch (RemoteException var4) {
      this.callback((Bundle) null);
    }

  }

  private void handleMessage(Message message) {
    if (message.what == 65545) {
      Bundle extras = message.getData();
      String errorType = extras.getString("com.facebook.platform.status.ERROR_TYPE");
      if (errorType != null) {
        this.callback((Bundle) null);
      } else {
        this.callback(extras);
      }

      try {
        this.context.unbindService(this);
      } catch (IllegalArgumentException var5) {
      }
    }

  }

  private void callback(Bundle result) {
    if (this.running) {
      this.running = false;
      if (this.listener != null) {
        this.listener.completed(result);
      }

    }
  }

  public interface CompletedListener {
    void completed(Bundle var1);
  }
}
