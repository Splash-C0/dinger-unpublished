package com.facebook.accountkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.accountkit.internal.AccountKitController;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Tracker {
  public static final String EXTRA_LOGIN_ERROR = "com.facebook.accountkit.sdk.EXTRA_LOGIN_ERROR";
  public static final String EXTRA_LOGIN_MODEL = "com.facebook.accountkit.sdk.EXTRA_LOGIN_MODEL";
  public static final String EXTRA_LOGIN_STATUS = "com.facebook.accountkit.sdk.EXTRA_LOGIN_STATUS";
  private final List<Intent> pendingBroadcasts = new ArrayList();
  private final BroadcastReceiver receiver = new Tracker.TrackerBroadcastReceiver(this);
  private boolean isPaused = false;
  private boolean isTracking = false;

  public Tracker() {
  }

  public void startTracking() {
    if (!this.isTracking) {
      this.isTracking = true;
      this.addBroadcastReceiver();
    }

    if (this.isPaused) {
      this.isPaused = false;
      ArrayList<Intent> intents = new ArrayList(this.pendingBroadcasts);
      this.pendingBroadcasts.clear();
      Iterator var2 = intents.iterator();

      while (var2.hasNext()) {
        Intent intent = (Intent) var2.next();
        if (this.isTracking()) {
          this.onReceive(intent);
        }
      }
    }

  }

  public void stopTracking() {
    if (this.isTracking) {
      this.isTracking = false;
      this.unregisterReceiver(this.receiver);
      this.pendingBroadcasts.clear();
    }
  }

  public void pauseTracking() {
    this.isPaused = true;
  }

  public boolean isPaused() {
    return this.isPaused;
  }

  public boolean isTracking() {
    return this.isTracking;
  }

  protected abstract List<String> getActionsStateChanged();

  protected boolean isLocal() {
    return true;
  }

  protected abstract void onReceive(Intent var1);

  private void addBroadcastReceiver() {
    IntentFilter filter = new IntentFilter();
    List<String> actionsStateChanged = this.getActionsStateChanged();
    Iterator var3 = actionsStateChanged.iterator();

    while (var3.hasNext()) {
      String action = (String) var3.next();
      filter.addAction(action);
    }

    this.registerReceiver(this.receiver, filter);
  }

  private void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    Context context = AccountKitController.getApplicationContext();
    if (this.isLocal()) {
      LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    } else {
      context.registerReceiver(receiver, filter);
    }

  }

  private void unregisterReceiver(BroadcastReceiver receiver) {
    Context context = AccountKitController.getApplicationContext();
    if (this.isLocal()) {
      LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    } else {
      context.unregisterReceiver(receiver);
    }

  }

  private static class TrackerBroadcastReceiver extends BroadcastReceiver {
    final WeakReference<Tracker> trackerRef;

    TrackerBroadcastReceiver(Tracker tracker) {
      this.trackerRef = new WeakReference(tracker);
    }

    public void onReceive(Context context, Intent intent) {
      Tracker tracker = (Tracker) this.trackerRef.get();
      if (tracker != null) {
        List<String> actionsStateChanged = tracker.getActionsStateChanged();
        String intentAction = intent.getAction();
        if (actionsStateChanged.contains(intentAction)) {
          if (tracker.isPaused()) {
            tracker.pendingBroadcasts.add(intent);
          } else if (tracker.isTracking()) {
            tracker.onReceive(intent);
          }

        }
      }
    }
  }
}
