package iaps

import com.android.billingclient.api.BillingClient

private sealed class BillingException(
    description: String,
    private val fieldName: String) : Exception("Code $fieldName: $description") {
  class BillingUnavailable : BillingException(
      description = "Billing API version is not supported for the type requested",
      fieldName = "BILLING_UNAVAILABLE")

  class DeveloperError : BillingException(
      description = "Invalid arguments provided to the API.",
      fieldName = "DEVELOPER_ERROR")

  class Error : BillingException(
      description = "Fatal error during the API action",
      fieldName = "ERROR")

  class FeatureNotSupported : BillingException(
      description = "Requested feature is not supported by Play Store on the current device.",
      fieldName = "FEATURE_NOT_SUPPORTED")

  class ItemAlreadyOwned : BillingException(
      description = "Failure to purchase since item is already owned",
      fieldName = "ITEM_ALREADY_OWNED")

  class ItemNotOwned : BillingException(
      description = "Failure to consume since item is not owned",
      fieldName = "ITEM_NOT_OWNED")

  class ItemUnavailable : BillingException(
      description = "Requested product is not available for purchase",
      fieldName = "ITEM_UNAVAILABLE")

  /**
   * This one will likely never be used or show up anywhere, but leave it here to clarify potential
   * bugs in the future.
   */
  class Ok : BillingException(description = "Success", fieldName = "OK")

  class ServiceDisconnected : BillingException(
      description = "Play Store service is not connected now - potentially transient state.",
      fieldName = "SERVICE_DISCONNECTED")

  class ServiceTimeout : BillingException(
      description = "The request has reached the maximum timeout before Google Play responds.",
      fieldName = "SERVICE_TIMEOUT")

  class ServiceUnavailable : BillingException(
      description = "Network connection is down",
      fieldName = "SERVICE_UNAVAILABLE")

  class UserCanceled : BillingException(
      description = "User pressed back or canceled a dialog",
      fieldName = "USER_CANCELED"
  )
}

internal fun billingExceptionFromResponseCode(
    @BillingClient.BillingResponseCode billingResponseCode: Int?) =
    when (billingResponseCode) {
      BillingClient.BillingResponseCode.BILLING_UNAVAILABLE ->
        BillingException.BillingUnavailable()
      BillingClient.BillingResponseCode.DEVELOPER_ERROR -> BillingException.DeveloperError()
      BillingClient.BillingResponseCode.ERROR -> BillingException.Error()
      BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED ->
        BillingException.FeatureNotSupported()
      BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> BillingException.ItemAlreadyOwned()
      BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> BillingException.ItemNotOwned()
      BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> BillingException.ItemUnavailable()
      BillingClient.BillingResponseCode.OK -> BillingException.Ok()
      BillingClient.BillingResponseCode.SERVICE_DISCONNECTED ->
        BillingException.ServiceDisconnected()
      BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> BillingException.ServiceTimeout()
      BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> BillingException.ServiceUnavailable()
      BillingClient.BillingResponseCode.USER_CANCELED -> BillingException.UserCanceled()
      else -> IllegalArgumentException(
          "Unsupported billing response code produced: $billingResponseCode")
    }
