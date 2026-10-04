package com.example.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.data.preferences.SettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "BillingManager"

class BillingManager(
    private val context: Context,
    private val preferences: SettingsPreferences,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PurchasesUpdatedListener {

    companion object {
        const val PRODUCT_MONTHLY = "snake_premium_monthly"
        const val PRODUCT_YEARLY = "snake_premium_yearly"
        const val PRODUCT_LIFETIME = "snake_premium_lifetime"
    }

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _productDetails = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productDetails: StateFlow<Map<String, ProductDetails>> = _productDetails.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        // Load initial cached state from DataStore
        externalScope.launch {
            preferences.isPremiumCachedFlow.collect { cached ->
                _isPremium.value = cached
            }
        }
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client setup successful")
                    queryAvailableProducts()
                    queryPurchases()
                } else {
                    Log.w(TAG, "Billing setup warning: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing client disconnected, will retry on next action")
            }
        })
    }

    private fun queryAvailableProducts() {
        // Query Subscriptions
        val subProductList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val subParams = QueryProductDetailsParams.newBuilder()
            .setProductList(subProductList)
            .build()

        billingClient.queryProductDetailsAsync(subParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val map = _productDetails.value.toMutableMap()
                productDetailsList.forEach { details ->
                    map[details.productId] = details
                }
                _productDetails.value = map
            }
        }

        // Query In-App One-Time (Lifetime)
        val inAppProductList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_LIFETIME)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val inAppParams = QueryProductDetailsParams.newBuilder()
            .setProductList(inAppProductList)
            .build()

        billingClient.queryProductDetailsAsync(inAppParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val map = _productDetails.value.toMutableMap()
                productDetailsList.forEach { details ->
                    map[details.productId] = details
                }
                _productDetails.value = map
            }
        }
    }

    fun queryPurchases() {
        if (!billingClient.isReady) {
            startConnection()
            return
        }

        // Check active subscriptions
        val subsParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(subsParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchasesList(purchases)
            }
        }

        // Check active in-app purchases
        val inAppParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(inAppParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchasesList(purchases)
            }
        }
    }

    private fun handlePurchasesList(purchases: List<Purchase>) {
        var hasActivePremium = false
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                hasActivePremium = true
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            }
        }

        if (hasActivePremium) {
            setPremiumState(true)
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase successfully acknowledged")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let { handlePurchasesList(it) }
                _billingStatusMessage.value = "Purchase completed successfully! Enjoy Snake Premium."
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "Purchase cancelled by user")
            }
            else -> {
                Log.e(TAG, "Purchase error: ${billingResult.debugMessage}")
                _billingStatusMessage.value = "Purchase failed: ${billingResult.debugMessage}"
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String) {
        val details = _productDetails.value[productId]
        if (details == null) {
            _billingStatusMessage.value = "Connecting to Google Play Store..."
            queryAvailableProducts()
            return
        }

        val productDetailsParamsList = when (details.productType) {
            BillingClient.ProductType.SUBS -> {
                val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build()
                )
            }
            else -> {
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build()
                )
            }
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun restorePurchases(onComplete: (Boolean, String) -> Unit) {
        queryPurchases()
        onComplete(_isPremium.value, if (_isPremium.value) "Purchases restored successfully!" else "No prior purchases found.")
    }

    private fun setPremiumState(premium: Boolean) {
        _isPremium.value = premium
        externalScope.launch {
            preferences.setIsPremiumCached(premium)
        }
    }

    fun clearStatusMessage() {
        _billingStatusMessage.value = null
    }
}
