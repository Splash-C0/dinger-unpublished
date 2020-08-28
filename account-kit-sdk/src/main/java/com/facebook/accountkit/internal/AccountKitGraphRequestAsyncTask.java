package com.facebook.accountkit.internal;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.facebook.accountkit.AccountKitError;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

final class AccountKitGraphRequestAsyncTask extends AsyncTask<Void, Void, AccountKitGraphResponse> {
  private static final String TAG = AccountKitGraphRequestAsyncTask.class.getCanonicalName();
  private static final int BACKOFF_INTERVAL_SEC = 5;
  private static final int MAX_NUM_RETRIES = 4;
  private static volatile AccountKitGraphRequestAsyncTask currentAsyncTask;
  private final AccountKitGraphRequest.Callback callback;
  private final HttpURLConnection connection;
  private final int numRetries;
  private final AccountKitGraphRequest request;
  private Exception exception;

  AccountKitGraphRequestAsyncTask(AccountKitGraphRequest request, AccountKitGraphRequest.Callback callback) {
    this((HttpURLConnection) null, request, callback, 0);
  }

  private AccountKitGraphRequestAsyncTask(HttpURLConnection connection, AccountKitGraphRequest request, AccountKitGraphRequest.Callback callback, int numRetries) {
    this.connection = connection;
    this.request = request;
    this.callback = callback;
    this.numRetries = numRetries;
  }

  static AccountKitGraphRequestAsyncTask getCurrentAsyncTask() {
    return currentAsyncTask;
  }

  static void setCurrentAsyncTask(AccountKitGraphRequestAsyncTask task) {
    currentAsyncTask = task;
  }

  static AccountKitGraphRequestAsyncTask cancelCurrentAsyncTask() {
    AccountKitGraphRequestAsyncTask task = currentAsyncTask;
    if (task != null) {
      task.cancel(true);
    }

    return task;
  }

  public String toString() {
    return "{AccountKitGraphRequestAsyncTask:  connection: " + this.connection + ", request: " + this.request + "}";
  }

  protected void onPreExecute() {
    super.onPreExecute();
    if (this.request.getCallbackHandler() == null) {
      Handler handler;
      if (Thread.currentThread() instanceof HandlerThread) {
        handler = new Handler();
      } else {
        handler = new Handler(Looper.getMainLooper());
      }

      this.request.setCallbackHandler(handler);
    }

  }

  protected void onPostExecute(AccountKitGraphResponse result) {
    super.onPostExecute(result);
    if (result != null && result.getError() != null && result.getError().getException().getError().getErrorType() == AccountKitError.Type.NETWORK_CONNECTION_ERROR && result.getError().getException().getError().getDetailErrorCode() != 101 && this.numRetries < 4) {
      Handler mainHandler = new Handler(AccountKitController.getApplicationContext().getMainLooper());
      mainHandler.post(new Runnable() {
        public void run() {
          int newNumRetries = AccountKitGraphRequestAsyncTask.this.numRetries + 1;
          final AccountKitGraphRequestAsyncTask asyncTask = new AccountKitGraphRequestAsyncTask((HttpURLConnection) null, AccountKitGraphRequestAsyncTask.this.request, AccountKitGraphRequestAsyncTask.this.callback, newNumRetries);
          Utility.getBackgroundExecutor().schedule(new Runnable() {
            public void run() {
              if (!AccountKitGraphRequestAsyncTask.this.isCancelled() && !asyncTask.isCancelled()) {
                asyncTask.executeOnExecutor(Utility.getThreadPoolExecutor(), new Void[0]);
              }

            }
          }, (long) (5 * newNumRetries), TimeUnit.SECONDS);
          if (AccountKitGraphRequestAsyncTask.this.request.isLoginRequest()) {
            AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(asyncTask);
          }

        }
      });
    } else {
      if (this.callback != null) {
        this.callback.onCompleted(result);
      }

      if (this.exception != null) {
        Log.d(TAG, String.format("onPostExecute: exception encountered during request: %s", this.exception.getMessage()));
      }

    }
  }

  protected AccountKitGraphResponse doInBackground(Void... params) {
    try {
      return this.connection == null ? this.request.executeAndWait() : AccountKitGraphRequest.executeConnectionAndWait(this.connection, this.request);
    } catch (Exception var3) {
      this.exception = var3;
      return null;
    }
  }
}
