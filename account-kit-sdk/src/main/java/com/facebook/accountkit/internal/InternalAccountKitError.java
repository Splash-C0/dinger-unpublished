package com.facebook.accountkit.internal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class InternalAccountKitError implements Parcelable {
  public static final int UNKNOWN_ERROR_CODE = -1;
  public static final int NO_NETWORK_CONNECTION_CODE = 101;
  public static final InternalAccountKitError NO_NETWORK_CONNECTION = new InternalAccountKitError(101, "No network connection detected");
  public static final InternalAccountKitError NO_RESULT_FOUND = new InternalAccountKitError(201, "No response found");
  public static final InternalAccountKitError INVALID_GRAPH_RESULTS_FORMAT = new InternalAccountKitError(202, "Invalid format of graph response to call");
  public static final InternalAccountKitError NO_ACCOUNT_FOUND = new InternalAccountKitError(301, "No account found");
  public static final InternalAccountKitError EXPIRED_EMAIL_REQUEST = new InternalAccountKitError(302, "Email login request expired");
  public static final InternalAccountKitError CANNOT_CONSTRUCT_URL = new InternalAccountKitError(401, "Could not construct URL for request");
  public static final InternalAccountKitError CANNOT_CONSTRUCT_MESSAGE_BODY = new InternalAccountKitError(404, "Could not construct request body");
  public static final InternalAccountKitError CALLBACK_WITHOUT_ACTIVITY = new InternalAccountKitError(405, "Callback issues while activity not available");
  public static final InternalAccountKitError CANNOT_RETRIEVE_ACCESS_TOKEN_NO_ACCOUNT = new InternalAccountKitError(406, "No access token: cannot retrieve account");
  public static final InternalAccountKitError INVALID_ACCESS_TOKEN_FORMAT = new InternalAccountKitError(407, "Unknown AccessToken serialization format");
  public static final InternalAccountKitError INVALID_GRAPH_RESPONSE = new InternalAccountKitError(408, "Expected a single response");
  public static final InternalAccountKitError UNEXPECTED_OBJECT_TYPE_RESPONSE = new InternalAccountKitError(409, "Unexpected object type in response, class: ");
  public static final InternalAccountKitError UNEXPECTED_FRAGMENT = new InternalAccountKitError(410, "Unexpected fragment type: ");
  public static final InternalAccountKitError UNEXPECTED_STATE = new InternalAccountKitError(411, "Unexpected login status");
  public static final InternalAccountKitError OPERATION_NOT_SUCCESSFUL = new InternalAccountKitError(412, "Operation not successful");
  public static final InternalAccountKitError SDK_NOT_INITIALIZED = new InternalAccountKitError(501, "The SDK has not been initialized, make sure to call AccountKit.initialize() first");
  public static final InternalAccountKitError INVALID_APP_ID = new InternalAccountKitError(502, "The App Id must be specified in the string resource file as com.facebook.sdk.ApplicationId");
  public static final InternalAccountKitError INVALID_CLIENT_TOKEN = new InternalAccountKitError(503, "The Client Token must be specified in the string resource file as com.facebook.accountkit.ClientToken");
  public static final InternalAccountKitError INVALID_APP_NAME = new InternalAccountKitError(504, "The App Name must be specified in the string resource file as com.facebook.accountkit.ApplicationName");
  public static final InternalAccountKitError INVALID_INTENT_EXTRAS_CONFIGURATION = new InternalAccountKitError(505, "Configuration must be supplied as part of the intent");
  public static final InternalAccountKitError INVALID_INTENT_EXTRAS_LOGIN_TYPE = new InternalAccountKitError(506, "Login Type must be supplied as part of the configuration");
  public static final InternalAccountKitError INVALID_INTENT_EXTRAS_RESPONSE_TYPE = new InternalAccountKitError(507, "Response Type must be supplied as part of the configuration");
  public static final InternalAccountKitError INVALID_LOGIN_TYPE = new InternalAccountKitError(508, "Login type must be either PHONE_NUMBER or EMAIL");
  public static final InternalAccountKitError INVALID_BACKGROUND_CONTRACT = new InternalAccountKitError(509, "The provided com_accountkit_text_color and it's background do not contrast enough to be easily visible.");
  public static final InternalAccountKitError NO_LOGIN_ATTEMPT_IN_PROGRESS = new InternalAccountKitError(601, "No login request currently in progress");
  public static final InternalAccountKitError DIFFERENT_LOGIN_ATTEMPT_IN_PROGRESS = new InternalAccountKitError(602, "Cannot perform operation while different login request in progress");
  public static final InternalAccountKitError NOT_EQUAL_OBJECTS = new InternalAccountKitError(603, "The following types not equal: ");
  public static final InternalAccountKitError INVALID_PARAMETER_TYPE = new InternalAccountKitError(604, "Invalid parameter type");
  public static final int INVALID_PHONE_NUMBER = 605;
  public static final InternalAccountKitError NO_NATIVE_APP_INSTALLED = new InternalAccountKitError(701, "No native app installed");
  public static final InternalAccountKitError UNSUPPORTED_NATIVE_APP_VERSION = new InternalAccountKitError(702, "Unsupported native app version");
  public static final int INVALID_API_PARAMETERS = 100;
  public static final int INVALID_CONFIRMATION_CODE = 15003;
  public static final int LOGIN_REQUEST_EXPIRED = 1948001;
  public static final int INVALID_CREDENTIALS_OR_LOGIN_REQUEST = 1948002;
  public static final int TOO_MANY_ATTEMPTS = 1948003;
  public static final int INVALID_PHONE_NUMBER_SERVER_CODE = 1550001;
  public static final Creator<InternalAccountKitError> CREATOR = new Creator<InternalAccountKitError>() {
    public InternalAccountKitError createFromParcel(Parcel source) {
      return new InternalAccountKitError(source);
    }

    public InternalAccountKitError[] newArray(int size) {
      return new InternalAccountKitError[size];
    }
  };
  private final int subCode;
  private final String subMessage;
  private String userFacingMessage;

  public InternalAccountKitError(int subCode, String subMessage) {
    this(subCode, subMessage, (String) null);
  }

  public InternalAccountKitError(int subCode, String subMessage, @Nullable String userFacingMessage) {
    this.subCode = subCode;
    this.subMessage = Utility.isNullOrEmpty(subMessage) ? null : subMessage;
    this.userFacingMessage = Utility.isNullOrEmpty(userFacingMessage) ? null : userFacingMessage;
  }

  InternalAccountKitError(InternalAccountKitError error, Object... args) {
    this.subCode = error.subCode;
    this.subMessage = String.format(error.subMessage, args);
  }

  private InternalAccountKitError(Parcel parcel) {
    this.subCode = parcel.readInt();
    this.subMessage = parcel.readString();
    this.userFacingMessage = parcel.readString();
  }

  String getMessage() {
    return this.subMessage;
  }

  public String getUserFacingMessage() {
    return this.userFacingMessage;
  }

  public int getCode() {
    return this.subCode;
  }

  public String toString() {
    return this.subCode + (this.subMessage != null ? ": " + this.subMessage : "") + (this.userFacingMessage != null ? ": " + this.userFacingMessage : "");
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.subCode);
    dest.writeString(this.subMessage);
    dest.writeString(this.userFacingMessage);
  }

  public int describeContents() {
    return 0;
  }
}
