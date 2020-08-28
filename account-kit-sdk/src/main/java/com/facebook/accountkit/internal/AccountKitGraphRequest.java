package com.facebook.accountkit.internal;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitException;
import com.facebook.accountkit.LoggingBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

final class AccountKitGraphRequest {
  public static final String TAG = AccountKitGraphRequest.class.getSimpleName();
  private static final String ACCESS_TOKEN_PREFIX = "AA";
  private static final int DEFAULT_TIMEOUT_MILLISECONDS = 10000;
  private static final String GRAPH_API_VERSION = "v1.3";
  private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private static final String ISO_8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
  private static final String MIME_BOUNDARY;
  private static final String PARAMETER_ACCESS_TOKEN = "access_token";
  private static final String USER_AGENT_BASE = "AccountKitAndroidSDK";
  private static final String USER_AGENT_HEADER = "User-Agent";
  private static final Pattern versionPattern = Pattern.compile("^/?v\\d+\\.\\d+/(.*)");
  private static final int SOCKET_TAG = 61453;

  static {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    MIME_BOUNDARY = (new BigInteger(1, bytes)).toString(16);
  }

  private final String graphPath;
  private final boolean isLoginRequest;
  private AccessToken accessToken;
  private Handler callbackHandler;
  private HttpMethod httpMethod;
  private Bundle parameters;
  private JSONObject requestObject;
  private Object tag;
  private String version;

  public AccountKitGraphRequest(AccessToken accessToken, String graphPath, Bundle parameters, boolean isLoginRequest, HttpMethod httpMethod) {
    this(accessToken, graphPath, parameters, isLoginRequest, httpMethod, (String) null);
  }

  public AccountKitGraphRequest(AccessToken accessToken, String graphPath, Bundle parameters, boolean isLoginRequest, HttpMethod httpMethod, String version) {
    this.accessToken = accessToken;
    this.graphPath = graphPath;
    this.isLoginRequest = isLoginRequest;
    this.setHttpMethod(httpMethod);
    if (parameters != null) {
      this.parameters = new Bundle(parameters);
    } else {
      this.parameters = new Bundle();
    }

    this.version = version == null ? "v1.3" : version;
  }

  static AccountKitGraphResponse executeConnectionAndWait(HttpURLConnection connection, AccountKitGraphRequest request) {
    AccountKitGraphResponse response = AccountKitGraphResponse.fromHttpConnection(connection, request);
    Utility.disconnectQuietly(connection);
    return response;
  }

  private static HttpURLConnection createConnection(URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("User-Agent", AccountKitGraphRequest.LazyUserAgentHolder.userAgent);
    connection.setChunkedStreamingMode(0);
    return connection;
  }

  private static void setConnectionContentType(HttpURLConnection connection, boolean isMultipart) {
    if (isMultipart) {
      connection.setRequestProperty("Content-Type", getMimeContentType());
    } else {
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Content-Encoding", "gzip");
    }

  }

  private static void serializeParameters(Bundle bundle, AccountKitGraphRequest.Serializer serializer) throws IOException {
    Set<String> keys = bundle.keySet();
    Iterator var3 = keys.iterator();

    while (var3.hasNext()) {
      String key = (String) var3.next();
      Object value = bundle.get(key);
      serializer.writeObject(key, value);
    }

  }

  private static String getMimeContentType() {
    return String.format("multipart/form-data; boundary=%s", MIME_BOUNDARY);
  }

  private static boolean isSupportedParameterType(Object value) {
    return value instanceof String || value instanceof Boolean || value instanceof Number || value instanceof Date;
  }

  private static String parameterToString(Object value) {
    if (value instanceof String) {
      return (String) value;
    } else if (!(value instanceof Boolean) && !(value instanceof Number)) {
      if (value instanceof Date) {
        SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        return iso8601DateFormat.format(value);
      } else {
        throw new IllegalArgumentException("Unsupported parameter type.");
      }
    } else {
      return value.toString();
    }
  }

  static HttpURLConnection toHttpConnection(AccountKitGraphRequest request) {
    URL url;
    try {
      String s = request.getUrlForSingleRequest();
      url = new URL(s);
    } catch (MalformedURLException var6) {
      throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.CANNOT_CONSTRUCT_URL, var6);
    }

    try {
      HttpURLConnection connection = createConnection(url);
      serializeToUrlConnection(request, connection);
      return connection;
    } catch (UnknownHostException var4) {
      throw new AccountKitException(AccountKitError.Type.NETWORK_CONNECTION_ERROR, InternalAccountKitError.NO_NETWORK_CONNECTION);
    } catch (JSONException | IOException var5) {
      throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.CANNOT_CONSTRUCT_MESSAGE_BODY, var5);
    }
  }

  static AccountKitGraphRequestAsyncTask executeAsync(@NonNull AccountKitGraphRequest request, AccountKitGraphRequest.Callback callback) {
    AccountKitGraphRequestAsyncTask asyncTask = new AccountKitGraphRequestAsyncTask(request, callback);
    asyncTask.executeOnExecutor(Utility.getThreadPoolExecutor(), new Void[0]);
    return asyncTask;
  }

  private static void serializeToUrlConnection(AccountKitGraphRequest request, HttpURLConnection connection) throws IOException, JSONException {
    ConsoleLogger consoleLogger = new ConsoleLogger(LoggingBehavior.REQUESTS, "Request");
    HttpMethod connectionHttpMethod = request.httpMethod;
    connection.setRequestMethod(connectionHttpMethod.name());
    boolean isMultipart = isMultiPart(request.parameters);
    setConnectionContentType(connection, isMultipart);
    URL url = connection.getURL();
    consoleLogger.appendLine("Request:");
    consoleLogger.appendKeyValue("AccessToken", request.getAccessToken());
    consoleLogger.appendKeyValue("URL", url);
    consoleLogger.appendKeyValue("Method", connection.getRequestMethod());
    consoleLogger.appendKeyValue("User-Agent", connection.getRequestProperty("User-Agent"));
    consoleLogger.appendKeyValue("Content-Type", connection.getRequestProperty("Content-Type"));
    consoleLogger.log();
    connection.setConnectTimeout(10000);
    connection.setReadTimeout(10000);
    if (connectionHttpMethod == HttpMethod.POST) {
      connection.setDoOutput(true);

      OutputStream outputStream = null;
      try {
        outputStream = connection.getOutputStream();
        outputStream = new BufferedOutputStream(outputStream);
        if (!isMultipart) {
          outputStream = new GZIPOutputStream((OutputStream) outputStream);
        }

        processRequest(request, (OutputStream) outputStream, isMultipart);
      } finally {
        if (outputStream != null) {
          ((OutputStream) outputStream).close();
        }

      }

    }
  }

  private static boolean isMultiPart(Bundle parameters) {
    Iterator var1 = parameters.keySet().iterator();

    Object value;
    do {
      if (!var1.hasNext()) {
        return false;
      }

      String key = (String) var1.next();
      value = parameters.get(key);
    } while (!isMultipartType(value));

    return true;
  }

  private static boolean isMultipartType(Object value) {
    return value instanceof Bitmap || value instanceof byte[] || value instanceof Uri || value instanceof ParcelFileDescriptor || value instanceof AccountKitGraphRequest.ParcelableResourceWithMimeType;
  }

  private static void processRequest(AccountKitGraphRequest request, OutputStream outputStream, boolean isMultipart) throws IOException {
    AccountKitGraphRequest.Serializer serializer = new AccountKitGraphRequest.Serializer(outputStream, !isMultipart);
    serializeParameters(request.parameters, serializer);
    if (request.requestObject != null) {
      processRequestObject(request.requestObject, serializer);
    }

  }

  private static void processRequestObject(JSONObject requestObject, AccountKitGraphRequest.KeyValueSerializer serializer) throws IOException {
    Iterator keyIterator = requestObject.keys();

    while (keyIterator.hasNext()) {
      String key = (String) keyIterator.next();
      Object value = requestObject.opt(key);
      processRequestObjectProperty(key, value, serializer);
    }

  }

  private static void processRequestObjectProperty(String key, Object value, AccountKitGraphRequest.KeyValueSerializer serializer) throws IOException {
    Class<?> valueClass = value.getClass();
    if (!String.class.isAssignableFrom(valueClass) && !Number.class.isAssignableFrom(valueClass) && !Boolean.class.isAssignableFrom(valueClass)) {
      if (Date.class.isAssignableFrom(valueClass)) {
        Date date = (Date) value;
        SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        serializer.writeString(key, iso8601DateFormat.format(date));
      }
    } else {
      serializer.writeString(key, value.toString());
    }

  }

  boolean isLoginRequest() {
    return this.isLoginRequest;
  }

  JSONObject getRequestObject() {
    return this.requestObject;
  }

  void setRequestObject(JSONObject requestObject) {
    this.requestObject = requestObject;
  }

  String getGraphPath() {
    return this.graphPath;
  }

  HttpMethod getHttpMethod() {
    return this.httpMethod;
  }

  void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod != null ? httpMethod : HttpMethod.GET;
  }

  public Bundle getParameters() {
    return this.parameters;
  }

  public void setParameters(Bundle parameters) {
    this.parameters = parameters;
  }

  public AccessToken getAccessToken() {
    return this.accessToken;
  }

  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Object getTag() {
    return this.tag;
  }

  public void setTag(Object tag) {
    this.tag = tag;
  }

  AccountKitGraphResponse executeAndWait() {
    HttpURLConnection connection;
    try {
      TrafficStats.setThreadStatsTag(61453);
      connection = toHttpConnection(this);
    } catch (AccountKitException var3) {
      return new AccountKitGraphResponse(this, (HttpURLConnection) null, new AccountKitRequestError(var3));
    } catch (Exception var4) {
      return new AccountKitGraphResponse(this, (HttpURLConnection) null, new AccountKitRequestError(new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, var4)));
    }

    AccountKitGraphResponse response = executeConnectionAndWait(connection, this);
    if (response == null) {
      throw new AccountKitException(AccountKitError.Type.INTERNAL_ERROR, InternalAccountKitError.INVALID_GRAPH_RESPONSE);
    } else {
      return response;
    }
  }

  public String toString() {
    return "{Request:  accessToken: " + (this.accessToken == null ? "null" : this.accessToken) + ", graphPath: " + this.graphPath + ", requestObject: " + this.requestObject + ", httpMethod: " + this.httpMethod + ", parameters: " + this.parameters + "}";
  }

  private void addCommonParameters() {
    Utility.putNonNullString(this.parameters, "locale", LocaleMapper.getSystemLocale());
    Utility.putNonNullString(this.parameters, "sdk", "android");
    this.parameters.putBoolean("fb_app_events_enabled", AccountKit.getAccountKitFacebookAppEventsEnabled());
    String appID;
    if (this.accessToken != null) {
      if (!this.parameters.containsKey("access_token")) {
        appID = this.accessToken.getToken();
        this.parameters.putString("access_token", appID);
      }
    } else if (!this.parameters.containsKey("access_token")) {
      appID = AccountKit.getApplicationId();
      String clientToken = AccountKit.getClientToken();
      if (!Utility.isNullOrEmpty(appID) && !Utility.isNullOrEmpty(clientToken)) {
        String appAccessToken = "AA|" + appID + "|" + clientToken;
        this.parameters.putString("access_token", appAccessToken);
      } else {
        Log.d(TAG, "Warning: Request without access token missing application ID or client token.");
      }
    }

  }

  private String getUrlForSingleRequest() {
    Builder builder = (new Builder()).scheme("https").authority(AccountKitController.getBaseGraphHost());
    Matcher matcher = versionPattern.matcher(this.graphPath);
    if (!matcher.matches()) {
      builder.appendPath(this.version);
    }

    builder.appendPath(this.graphPath);
    this.addCommonParameters();
    if (this.httpMethod != HttpMethod.POST) {
      this.appendQueryParametersToUri(builder);
    }

    return builder.toString();
  }

  private void appendQueryParametersToUri(Builder uriBuilder) {
    List<String> keys = new ArrayList(this.parameters.keySet());
    Collections.sort(keys);

    String key;
    Object value;
    for (Iterator var3 = keys.iterator(); var3.hasNext(); uriBuilder.appendQueryParameter(key, parameterToString(value))) {
      key = (String) var3.next();
      value = this.parameters.get(key);
      if (value == null) {
        value = "";
      }
    }

  }

  Handler getCallbackHandler() {
    return this.callbackHandler;
  }

  void setCallbackHandler(Handler callbackHandler) {
    this.callbackHandler = callbackHandler;
  }

  public interface Callback {
    void onCompleted(AccountKitGraphResponse var1);
  }

  private interface KeyValueSerializer {
    void writeString(String var1, String var2) throws IOException;
  }

  private static class ParcelableResourceWithMimeType<RESOURCE extends Parcelable> implements Parcelable {
    public static final Creator<AccountKitGraphRequest.ParcelableResourceWithMimeType> CREATOR = new Creator<AccountKitGraphRequest.ParcelableResourceWithMimeType>() {
      public AccountKitGraphRequest.ParcelableResourceWithMimeType createFromParcel(Parcel in) {
        return new AccountKitGraphRequest.ParcelableResourceWithMimeType(in);
      }

      public AccountKitGraphRequest.ParcelableResourceWithMimeType[] newArray(int size) {
        return new AccountKitGraphRequest.ParcelableResourceWithMimeType[size];
      }
    };
    private final String mimeType;
    private final RESOURCE resource;

    private ParcelableResourceWithMimeType(Parcel in) {
      this.mimeType = in.readString();
      this.resource = in.readParcelable(AccountKitController.getApplicationContext().getClassLoader());
    }

    String getMimeType() {
      return this.mimeType;
    }

    public RESOURCE getResource() {
      return this.resource;
    }

    public int describeContents() {
      return 1;
    }

    public void writeToParcel(Parcel out, int flags) {
      out.writeString(this.mimeType);
      out.writeParcelable(this.resource, flags);
    }
  }

  private static class Serializer implements AccountKitGraphRequest.KeyValueSerializer {
    private final OutputStream outputStream;
    private boolean firstWrite = true;
    private boolean useUrlEncode = false;

    Serializer(OutputStream outputStream, boolean useUrlEncode) {
      this.outputStream = outputStream;
      this.useUrlEncode = useUrlEncode;
    }

    void writeObject(String key, Object value) throws IOException {
      if (AccountKitGraphRequest.isSupportedParameterType(value)) {
        this.writeString(key, AccountKitGraphRequest.parameterToString(value));
      } else if (value instanceof Bitmap) {
        this.writeBitmap(key, (Bitmap) value);
      } else if (value instanceof byte[]) {
        this.writeBytes(key, (byte[]) ((byte[]) value));
      } else if (value instanceof Uri) {
        this.writeContentUri(key, (Uri) value, (String) null);
      } else if (value instanceof ParcelFileDescriptor) {
        this.writeFile(key, (ParcelFileDescriptor) value, (String) null);
      } else {
        if (!(value instanceof AccountKitGraphRequest.ParcelableResourceWithMimeType)) {
          throw this.getInvalidTypeError();
        }

        AccountKitGraphRequest.ParcelableResourceWithMimeType resourceWithMimeType = (AccountKitGraphRequest.ParcelableResourceWithMimeType) value;
        Parcelable resource = resourceWithMimeType.getResource();
        String mimeType = resourceWithMimeType.getMimeType();
        if (resource instanceof ParcelFileDescriptor) {
          this.writeFile(key, (ParcelFileDescriptor) resource, mimeType);
        } else {
          if (!(resource instanceof Uri)) {
            throw this.getInvalidTypeError();
          }

          this.writeContentUri(key, (Uri) resource, mimeType);
        }
      }

    }

    private RuntimeException getInvalidTypeError() {
      return new IllegalArgumentException("value is not a supported type.");
    }

    public void writeString(String key, String value) throws IOException {
      this.writeContentDisposition(key, (String) null, (String) null);
      this.writeLine("%s", value);
      this.writeRecordBoundary();
    }

    void writeBitmap(String key, Bitmap bitmap) throws IOException {
      this.writeContentDisposition(key, key, "image/png");
      bitmap.compress(CompressFormat.PNG, 100, this.outputStream);
      this.writeLine("");
      this.writeRecordBoundary();
    }

    void writeBytes(String key, byte[] bytes) throws IOException {
      this.writeContentDisposition(key, key, "content/unknown");
      this.outputStream.write(bytes);
      this.writeLine("");
      this.writeRecordBoundary();
    }

    void writeContentUri(String key, Uri contentUri, String mimeType) throws IOException {
      if (mimeType == null) {
        mimeType = "content/unknown";
      }

      this.writeContentDisposition(key, key, mimeType);
      InputStream inputStream = AccountKitController.getApplicationContext().getContentResolver().openInputStream(contentUri);
      Utility.copyAndCloseInputStream(inputStream, this.outputStream);
      this.writeLine("");
      this.writeRecordBoundary();
    }

    void writeFile(String key, ParcelFileDescriptor descriptor, String mimeType) throws IOException {
      if (mimeType == null) {
        mimeType = "content/unknown";
      }

      this.writeContentDisposition(key, key, mimeType);
      AutoCloseInputStream inputStream = new AutoCloseInputStream(descriptor);
      Utility.copyAndCloseInputStream(inputStream, this.outputStream);
      this.writeLine("");
      this.writeRecordBoundary();
    }

    void writeRecordBoundary() throws IOException {
      if (!this.useUrlEncode) {
        this.writeLine("--%s", AccountKitGraphRequest.MIME_BOUNDARY);
      } else {
        this.outputStream.write("&".getBytes());
      }

    }

    void writeContentDisposition(String name, String filename, String contentType) throws IOException {
      if (!this.useUrlEncode) {
        this.write("Content-Disposition: form-data; name=\"%s\"", name);
        if (filename != null) {
          this.write("; filename=\"%s\"", filename);
        }

        this.writeLine("");
        if (contentType != null) {
          this.writeLine("%s: %s", "Content-Type", contentType);
        }

        this.writeLine("");
      } else {
        this.outputStream.write(String.format("%s=", name).getBytes());
      }

    }

    void write(String format, Object... args) throws IOException {
      if (!this.useUrlEncode) {
        if (this.firstWrite) {
          this.outputStream.write("--".getBytes());
          this.outputStream.write(AccountKitGraphRequest.MIME_BOUNDARY.getBytes());
          this.outputStream.write("\r\n".getBytes());
          this.firstWrite = false;
        }

        this.outputStream.write(String.format(format, args).getBytes());
      } else {
        this.outputStream.write(URLEncoder.encode(String.format(Locale.US, format, args), "UTF-8").getBytes());
      }

    }

    void writeLine(String format, Object... args) throws IOException {
      this.write(format, args);
      if (!this.useUrlEncode) {
        this.write("\r\n");
      }

    }
  }

  private static class LazyUserAgentHolder {
    static final String userAgent = buildUserAgent();

    private LazyUserAgentHolder() {
    }

    private static String buildUserAgent() {
      String systemUserAgent = System.getProperty("http.agent");
      if (systemUserAgent == null) {
        systemUserAgent = "";
      }

      return systemUserAgent + " " + "AccountKitAndroidSDK" + "/" + "5.4.0";
    }
  }
}
