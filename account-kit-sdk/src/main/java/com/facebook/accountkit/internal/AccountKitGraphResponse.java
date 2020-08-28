package com.facebook.accountkit.internal;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.LoggingBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Locale;

final class AccountKitGraphResponse {
  private static final String TAG = "AccountKitGraphResponse";
  private static final AccountKitGraphResponse.IntegerRange HTTP_RANGE_SUCCESS = new AccountKitGraphResponse.IntegerRange(200, 299);
  private final HttpURLConnection connection;
  private final AccountKitRequestError error;
  private final String rawResponse;
  private final AccountKitGraphRequest request;
  private final JSONArray responseArray;
  private final JSONObject responseObject;

  public AccountKitGraphResponse(AccountKitGraphRequest request, HttpURLConnection connection, AccountKitRequestError error) {
    this(request, connection, (String) null, (JSONObject) null, (JSONArray) null, error);
  }

  private AccountKitGraphResponse(AccountKitGraphRequest request, HttpURLConnection connection, String rawResponse, JSONObject responseObject, JSONArray responseArray, AccountKitRequestError error) {
    this.request = request;
    this.connection = connection;
    this.rawResponse = rawResponse;
    this.responseObject = responseObject;
    this.responseArray = responseArray;
    this.error = error;
  }

  static AccountKitGraphResponse fromHttpConnection(HttpURLConnection connection, AccountKitGraphRequest request) {
    InputStream stream = null;

    AccountKitGraphResponse var4;
    try {
      if (connection.getResponseCode() >= 400) {
        stream = connection.getErrorStream();
      } else {
        stream = connection.getInputStream();
      }

      AccountKitGraphResponse var3 = createResponseFromStream(stream, connection, request);
      return var3;
    } catch (AccountKitException var9) {
      ConsoleLogger.log(LoggingBehavior.REQUESTS, "AccountKitGraphResponse", "Response <ERROR>: %s", var9);
      var4 = new AccountKitGraphResponse(request, connection, new AccountKitRequestError(var9));
      return var4;
    } catch (IOException | SecurityException | JSONException var10) {
      ConsoleLogger.log(LoggingBehavior.REQUESTS, "AccountKitGraphResponse", "Response <ERROR>: %s", var10);
      var4 = new AccountKitGraphResponse(request, connection, new AccountKitRequestError(new AccountKitException(AccountKitError.Type.SERVER_ERROR, var10)));
    } finally {
      Utility.closeQuietly(stream);
    }

    return var4;
  }

  private static AccountKitGraphResponse createResponseFromStream(InputStream stream, HttpURLConnection connection, AccountKitGraphRequest request) throws AccountKitException, JSONException, IOException {
    String responseString = Utility.readStreamToString(stream);
    ConsoleLogger.log(LoggingBehavior.REQUESTS, "AccountKitGraphResponse", "Response:\n%s\n", responseString);
    Object resultObject = (new JSONTokener(responseString)).nextValue();

    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("body", resultObject);
      int responseCode = connection != null ? connection.getResponseCode() : 200;
      jsonObject.put("code", responseCode);
      resultObject = jsonObject;
    } catch (IOException | JSONException var7) {
      return new AccountKitGraphResponse(request, connection, new AccountKitRequestError(new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.INVALID_GRAPH_RESPONSE, var7)));
    }

    return createResponseFromObject(request, connection, resultObject);
  }

  private static AccountKitRequestError checkResponseAndCreateError(JSONObject errorResult) {
    try {
      if (errorResult.has("code")) {
        int responseCode = errorResult.getInt("code");
        Object body = Utility.getStringPropertyAsJSON(errorResult, "body");
        if (body != null && body instanceof JSONObject) {
          JSONObject jsonBody = (JSONObject) body;
          String errorType = null;
          String errorMessage = null;
          String userErrorMessage = null;
          int errorCode = -1;
          int subErrorCode = -1;
          boolean hasError = false;
          if (jsonBody.has("error")) {
            JSONObject error = (JSONObject) Utility.getStringPropertyAsJSON(jsonBody, "error");
            errorType = error.optString("type", (String) null);
            errorMessage = error.optString("message", (String) null);
            userErrorMessage = error.optString("error_user_msg");
            errorCode = error.optInt("code", -1);
            subErrorCode = error.optInt("error_subcode", -1);
            hasError = true;
          } else if (jsonBody.has("error_code") || jsonBody.has("error_msg") || jsonBody.has("error_reason")) {
            errorType = jsonBody.optString("error_reason", (String) null);
            errorMessage = jsonBody.optString("error_msg", (String) null);
            errorCode = jsonBody.optInt("error_code", -1);
            hasError = true;
          }

          if (hasError) {
            return new AccountKitRequestError(responseCode, errorCode, subErrorCode, errorType, errorMessage, userErrorMessage, (AccountKitException) null);
          }
        }

        if (!HTTP_RANGE_SUCCESS.contains(responseCode)) {
          return new AccountKitRequestError(responseCode, -1, -1, (String) null, (String) null, (String) null, (AccountKitException) null);
        }
      }
    } catch (JSONException var11) {
    }

    return null;
  }

  private static AccountKitGraphResponse createResponseFromObject(AccountKitGraphRequest request, HttpURLConnection connection, Object object) {
    try {
      if (object instanceof JSONObject) {
        JSONObject jsonObject = (JSONObject) object;
        AccountKitRequestError requestError = checkResponseAndCreateError(jsonObject);
        if (requestError != null) {
          return new AccountKitGraphResponse(request, connection, requestError);
        }

        Object body = Utility.getStringPropertyAsJSON(jsonObject, "body");
        if (body instanceof JSONObject) {
          return new AccountKitGraphResponse(request, connection, body.toString(), (JSONObject) body, (JSONArray) null, (AccountKitRequestError) null);
        }

        if (body instanceof JSONArray) {
          return new AccountKitGraphResponse(request, connection, body.toString(), (JSONObject) null, (JSONArray) body, (AccountKitRequestError) null);
        }

        object = JSONObject.NULL;
      }

      if (object == JSONObject.NULL) {
        return new AccountKitGraphResponse(request, connection, object.toString(), (JSONObject) null, (JSONArray) null, (AccountKitRequestError) null);
      } else {
        throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.UNEXPECTED_OBJECT_TYPE_RESPONSE, object.getClass().getSimpleName());
      }
    } catch (JSONException var6) {
      return new AccountKitGraphResponse(request, connection, new AccountKitRequestError(new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.INVALID_GRAPH_RESPONSE, var6)));
    }
  }

  public AccountKitRequestError getError() {
    return this.error;
  }

  public JSONObject getResponseObject() {
    return this.responseObject;
  }

  public JSONArray getResponseArray() {
    return this.responseArray;
  }

  public HttpURLConnection getConnection() {
    return this.connection;
  }

  public AccountKitGraphRequest getRequest() {
    return this.request;
  }

  public String getRawResponse() {
    return this.rawResponse;
  }

  public String toString() {
    String responseCode;
    try {
      responseCode = String.format(Locale.US, "%d", this.connection != null ? this.connection.getResponseCode() : 200);
    } catch (IOException var3) {
      responseCode = "unknown";
    }

    return "{Response:  responseCode: " + responseCode + ", responseObject: " + this.responseObject + ", error: " + this.error + "}";
  }

  private static final class IntegerRange {
    private final int end;
    private final int start;

    private IntegerRange(int start, int end) {
      this.start = start;
      this.end = end;
    }

    public boolean contains(int value) {
      return this.start <= value && value <= this.end;
    }
  }
}
