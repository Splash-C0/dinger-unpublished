package com.facebook.accountkit.internal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.UpdateFlowBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

final class PhoneUpdateController {
  private static final String GRAPH_PATH_START_UPDATE = "start_update";
  private static final String GRAPH_PATH_CONFIRM_UPDATE = "confirm_update";
  private static final String PARAMETER_STATE = "state";
  private static final String PARAMETER_PHONE = "phone_number";
  private static final String PARAMETER_EXTRAS_TYPE = "extras";
  private static final String PARAMETER_CREDENTIALS_TYPE = "credentials_type";
  private static final String PARAMETER_CONFIRMATION_CODE = "confirmation_code";
  private static final String PARAMETER_UPDATE_REQUEST_CODE = "update_request_code";
  private final WeakReference<UpdateManager> updateManagerRef;
  private final PhoneUpdateModelImpl updateModel;

  PhoneUpdateController(UpdateManager updateManager, PhoneUpdateModelImpl updateModel) {
    this.updateManagerRef = new WeakReference(updateManager);
    this.updateModel = updateModel;
  }

  void update(String initialUpdateState) {
    AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
      public void onCompleted(AccountKitGraphResponse response) {
        UpdateManager updateManager = PhoneUpdateController.this.getUpdateManager();
        if (updateManager != null && response != null) {
          Pair error = null;
          boolean var18 = false;

          label176:
          {
            Intent intentxx;
            label177:
            {
              try {
                var18 = true;
                if (response.getError() == null) {
                  JSONObject result = response.getResponseObject();
                  if (result == null) {
                    PhoneUpdateController.this.setError(AccountKitError.Type.UPDATE_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND);
                    var18 = false;
                    break label176;
                  }

                  String privacyPolicy = result.optString("privacy_policy");
                  if (!Utility.isNullOrEmpty(privacyPolicy)) {
                    PhoneUpdateController.this.updateModel.putField("privacy_policy", privacyPolicy);
                  }

                  String termsOfService = result.optString("terms_of_service");
                  if (!Utility.isNullOrEmpty(termsOfService)) {
                    PhoneUpdateController.this.updateModel.putField("terms_of_service", termsOfService);
                  }

                  try {
                    String updateRequestCode = result.getString("update_request_code");
                    String expiresInString = result.getString("expires_in_sec");
                    long expiresIn = Long.parseLong(expiresInString);
                    PhoneUpdateController.this.updateModel.setExpiresInSeconds(expiresIn);
                    String minResendIntervalSecString = result.optString("min_resend_interval_sec");
                    long minResendIntervalSec = Long.parseLong(minResendIntervalSecString);
                    PhoneUpdateController.this.updateModel.setResendTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(minResendIntervalSec));
                    PhoneUpdateController.this.updateModel.setStatus(UpdateStatus.PENDING);
                    PhoneUpdateController.this.updateModel.setUpdateRequestCode(updateRequestCode);
                    var18 = false;
                  } catch (NumberFormatException | JSONException var19) {
                    PhoneUpdateController.this.setError(AccountKitError.Type.UPDATE_INVALIDATED, InternalAccountKitError.INVALID_GRAPH_RESULTS_FORMAT);
                    var18 = false;
                  }
                  break label177;
                }

                error = Utility.createErrorFromServerError(response.getError());
                PhoneUpdateController.this.setError((AccountKitError) error.first);
                var18 = false;
              } finally {
                if (var18) {
                  Intent intentx = new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE);
                  if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.PENDING) {
                    intentx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.SENT_CODE);
                  } else if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.ERROR) {
                    intentx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_UPDATE);
                    if (error != null) {
                      intentx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_ERROR_MESSAGE, ((AccountKitError) error.first).getUserFacingMessage());
                    }
                  }

                  updateManager.getLocalBroadcastManager().sendBroadcast(intentx);
                }
              }

              intentxx = new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE);
              if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.PENDING) {
                intentxx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.SENT_CODE);
              } else if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.ERROR) {
                intentxx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_UPDATE);
                if (error != null) {
                  intentxx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_ERROR_MESSAGE, ((AccountKitError) error.first).getUserFacingMessage());
                }
              }

              updateManager.getLocalBroadcastManager().sendBroadcast(intentxx);
              return;
            }

            intentxx = new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE);
            if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.PENDING) {
              intentxx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.SENT_CODE);
            } else if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.ERROR) {
              intentxx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_UPDATE);
              if (error != null) {
                intentxx.putExtra(UpdateFlowBroadcastReceiver.EXTRA_ERROR_MESSAGE, ((AccountKitError) error.first).getUserFacingMessage());
              }
            }

            updateManager.getLocalBroadcastManager().sendBroadcast(intentxx);
            return;
          }

          Intent intent = new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE);
          if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.PENDING) {
            intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.SENT_CODE);
          } else if (PhoneUpdateController.this.updateModel.getStatus() == UpdateStatus.ERROR) {
            intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_UPDATE);
            if (error != null) {
              intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_ERROR_MESSAGE, ((AccountKitError) error.first).getUserFacingMessage());
            }
          }

          updateManager.getLocalBroadcastManager().sendBroadcast(intent);
        }
      }
    };
    String phoneNumberString = this.updateModel.getPhoneNumber().toString();
    Bundle parameters = new Bundle();
    Utility.putNonNullString(parameters, "phone_number", phoneNumberString);
    Utility.putNonNullString(parameters, "state", initialUpdateState);
    Utility.putNonNullString(parameters, "extras", "terms_of_service,privacy_policy");
    this.updateModel.setInitialUpdateState(initialUpdateState);
    AccountKitGraphRequest request = this.buildGraphRequest("start_update", parameters);
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
    AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(request, requestCallback);
    AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
  }

  void onPending() {
    if (!Utility.isNullOrEmpty(this.updateModel.getConfirmationCode())) {
      AccountKitGraphRequest.Callback requestCallback = new AccountKitGraphRequest.Callback() {
        public void onCompleted(AccountKitGraphResponse response) {
          UpdateManager updateManager = PhoneUpdateController.this.getUpdateManager();
          if (updateManager != null && response != null) {
            Intent intent = new Intent(UpdateFlowBroadcastReceiver.ACTION_UPDATE);
            if (response.getError() != null) {
              Pair<AccountKitError, InternalAccountKitError> error = Utility.createErrorFromServerError(response.getError());
              if (Utility.isConfirmationCodeRetryable((InternalAccountKitError) error.second)) {
                PhoneUpdateController.this.updateModel.setStatus(UpdateStatus.PENDING);
                PhoneUpdateController.this.updateModel.setError((AccountKitError) null);
                intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.RETRY_CONFIRMATION_CODE);
              } else {
                PhoneUpdateController.this.setError((AccountKitError) error.first);
                updateManager.clearUpdate();
                intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_CONFIRMATION_CODE);
                intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_ERROR_MESSAGE, ((AccountKitError) error.first).getUserFacingMessage());
              }
            } else {
              JSONObject result = response.getResponseObject();
              if (result == null) {
                PhoneUpdateController.this.setError(AccountKitError.Type.UPDATE_INVALIDATED, InternalAccountKitError.NO_RESULT_FOUND);
                intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ERROR_CONFIRMATION_CODE);
              } else {
                String finalAuthState = result.optString("state");
                PhoneUpdateController.this.updateModel.setFinalUpdateState(finalAuthState);
                PhoneUpdateController.this.updateModel.setStatus(UpdateStatus.SUCCESS);
                intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_EVENT, UpdateFlowBroadcastReceiver.Event.ACCOUNT_UPDATE_COMPLETE);
                intent.putExtra(UpdateFlowBroadcastReceiver.EXTRA_UPDATE_STATE, PhoneUpdateController.this.updateModel.getFinalUpdateState());
              }

              updateManager.clearUpdate();
            }

            updateManager.getLocalBroadcastManager().sendBroadcast(intent);
          }
        }
      };
      Bundle parameters = new Bundle();
      Utility.putNonNullString(parameters, "confirmation_code", this.updateModel.getConfirmationCode());
      Utility.putNonNullString(parameters, "phone_number", this.updateModel.getPhoneNumber().toString());
      AccountKitGraphRequest request = this.buildGraphRequest("confirm_update", parameters);
      AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
      AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(request, requestCallback);
      AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
    }
  }

  public void onCancel() {
    this.updateModel.setStatus(UpdateStatus.CANCELLED);
    AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
    AccountKitGraphRequestAsyncTask.setCurrentAsyncTask((AccountKitGraphRequestAsyncTask) null);
    UpdateManager updateManager = this.getUpdateManager();
    if (updateManager != null) {
      updateManager.clearUpdate();
    }

  }

  public void onError(AccountKitError error) {
    this.updateModel.setError(error);
    this.updateModel.setStatus(UpdateStatus.ERROR);
    UpdateManager updateManager = this.getUpdateManager();
    if (updateManager != null) {
      updateManager.clearUpdate();
    }

  }

  PhoneUpdateModelImpl getUpdateModel() {
    return this.updateModel;
  }

  private void setError(AccountKitError error) {
    this.updateModel.setError(error);
    this.updateModel.setStatus(UpdateStatus.ERROR);
  }

  private void setError(AccountKitError.Type errorType, InternalAccountKitError internalError) {
    this.setError(new AccountKitError(errorType, internalError));
  }

  private String getCredentialsType() {
    return "phone_number";
  }

  private AccountKitGraphRequest buildGraphRequest(String graphPath, Bundle extraParameters) {
    Bundle parameters = new Bundle();
    Utility.putNonNullString(parameters, "credentials_type", this.getCredentialsType());
    Utility.putNonNullString(parameters, "update_request_code", this.updateModel.getUpdateRequestCode());
    parameters.putAll(extraParameters);
    return new AccountKitGraphRequest(AccountKit.getCurrentAccessToken(), graphPath, parameters, false, HttpMethod.POST);
  }

  @Nullable
  private UpdateManager getUpdateManager() {
    UpdateManager updateManager = (UpdateManager) this.updateManagerRef.get();
    if (updateManager == null) {
      return null;
    } else {
      return !updateManager.isActivityAvailable() ? null : updateManager;
    }
  }
}
