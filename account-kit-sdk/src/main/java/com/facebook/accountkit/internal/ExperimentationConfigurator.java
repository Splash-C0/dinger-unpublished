package com.facebook.accountkit.internal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.accountkit.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

final class ExperimentationConfigurator {
  private static final String GRAPH_PATH_GET_CONFIGURATION = "experimentation_configuration";
  private static final String PARAMETER_UNIT_IDENTIFIER = "unit_id";
  private static final String RESPONSE_PARAMETER_UNIT_ID = "unit_id";
  private static final String RESPONSE_PARAMETER_DATA = "data";
  private static final String RESPONSE_PARAMETER_CREATE_TIME = "create_time";
  private static final String RESPONSE_PARAMETER_TTL = "ttl";
  private static final String RESPONSE_PARAMETER_FEATURE_SET = "feature_set";
  private static final String FEATURE_PARAMETER_KEY = "key";
  private static final String FEATURE_PARAMETER_VALUE = "value";
  private Context applicationContext;

  ExperimentationConfigurator() {
  }

  void initialize(@NonNull Context context) {
    Validate.checkInternetPermissionAndThrow(context);
    this.applicationContext = context.getApplicationContext();
    Utility.getThreadPoolExecutor().execute(new Runnable() {
      public void run() {
        ExperimentationConfiguration ec = ExperimentationConfigurator.this.getExperimentationConfiguration();
        if (!ec.exists() || ec.isStale()) {
          ExperimentationConfigurator.this.downloadExperimentationConfiguration(ec.getUnitID());
        }

      }
    });
  }

  ExperimentationConfiguration getExperimentationConfiguration() {
    return new ExperimentationConfiguration(this.applicationContext);
  }

  private void downloadExperimentationConfiguration(@Nullable final String unitID) {
    Utility.getThreadPoolExecutor().execute(new Runnable() {
      public void run() {
        AccountKitGraphRequest graphRequest = ExperimentationConfigurator.this.buildGraphRequest("experimentation_configuration", unitID);
        AccountKitGraphRequestAsyncTask.cancelCurrentAsyncTask();
        AccountKitGraphRequest.Callback callback = new AccountKitGraphRequest.Callback() {
          public void onCompleted(AccountKitGraphResponse response) {
            if (response != null && response.getResponseObject() != null && response.getError() == null) {
              try {
                JSONObject responseObject = response.getResponseObject().getJSONArray("data").getJSONObject(0);
                Long createTime = null;
                Long ttl = null;
                String unitIDx = null;
                if (responseObject.has("create_time")) {
                  createTime = responseObject.getLong("create_time");
                }

                if (responseObject.has("unit_id")) {
                  unitIDx = responseObject.getString("unit_id");
                }

                if (responseObject.has("ttl")) {
                  ttl = responseObject.getLong("ttl");
                }

                JSONArray payload = responseObject.getJSONArray("feature_set");
                Map<Integer, Integer> featureSet = new HashMap(payload.length());

                for (int i = 0; i < payload.length(); ++i) {
                  JSONObject elem = payload.getJSONObject(i);
                  featureSet.put(elem.getInt("key"), elem.getInt("value"));
                }

                ExperimentationConfiguration.load(ExperimentationConfigurator.this.applicationContext, unitIDx, createTime, ttl, featureSet);
              } catch (JSONException var10) {
              }

            }
          }
        };
        AccountKitGraphRequestAsyncTask task = AccountKitGraphRequest.executeAsync(graphRequest, callback);
        AccountKitGraphRequestAsyncTask.setCurrentAsyncTask(task);
      }
    });
  }

  private AccountKitGraphRequest buildGraphRequest(String graphPath, @Nullable String unitID) {
    Bundle parameters = new Bundle();
    Utility.putNonNullString(parameters, "unit_id", unitID);
    return new AccountKitGraphRequest((AccessToken) null, graphPath, parameters, false, HttpMethod.GET);
  }
}
