package com.rahad.stripegate

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.MutableLiveData
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StripeGate(private val context: Context) {

    private val TAG = "StripeGate"

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerId: String
    private var ephemeralKey: String? = null
    private var clientSecret: String? = null
    private var dialog: ProgressDialog

    private val _amount = MutableLiveData<String>()
    private val _currency = MutableLiveData<String>()
    private val SECRET_KEY = MutableLiveData<String>()
    private val PUBLISHABLE_KEY = MutableLiveData<String>()
    private var paymentSuccessful: Boolean = false

    private lateinit var paymentResultListener: PaymentResultListener

    private val systemAPI = ApiService.getApiInterface()

    init {
        dialog = ProgressDialog(context)
        initPaymentSheet()
    }

    fun setSecretKey(key: String) {
        SECRET_KEY.postValue(key)
    }

    fun setPublishableKey(key: String) {
        PUBLISHABLE_KEY.postValue(key)
        PaymentConfiguration.init(context, key)
    }

    private fun initPaymentSheet() {
        val paymentSheetCallback = PaymentSheetResultCallback { paymentSheetResult ->
            onPaymentResult(paymentSheetResult)
        }
        paymentSheet = PaymentSheet(context as ComponentActivity, paymentSheetCallback)
    }

    fun integrate(amount: String, currency: String) {
        _amount.postValue(amount)
        _currency.postValue(currency)
    }

    fun applyPayment(paymentResultListener: PaymentResultListener) {
        this.paymentResultListener = paymentResultListener
        if (PUBLISHABLE_KEY.value.isNullOrEmpty() || SECRET_KEY.value.isNullOrEmpty()) {
            Log.e(TAG, "Please setup publishable key and secret key")
            return
        }
        createCustomerId()
    }

    private fun createCustomerId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dialog.showOnMainThread()
                val response = systemAPI.getCustomerID("Bearer ${SECRET_KEY.value}")
                if (response.isSuccessful) {
                    customerId = response.body()!!.id
                    Log.d(TAG, "Customer ID created: $customerId")
                    getEphemeralKey(customerId)
                } else {
                    logErrorAndDismiss("Failed to create customer ID", response.errorBody()?.string())
                }
            } catch (e: Exception) {
                logExceptionAndDismiss("createCustomerId", e)
            }
        }
    }

    private fun getEphemeralKey(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = systemAPI.getEphemeralKeys("Bearer ${SECRET_KEY.value}", "2024-06-20", id)
                if (response.isSuccessful) {
                    ephemeralKey = response.body()!!.secret
                    Log.d(TAG, "Ephemeral key received: $ephemeralKey")
                    getPaymentInfo(_amount.value!!, _currency.value!!, id)
                } else {
                    logErrorAndDismiss("Failed to get ephemeral key", response.errorBody()?.string())
                }
            } catch (e: Exception) {
                logExceptionAndDismiss("getEphemeralKey", e)
            }
        }
    }

    private fun getPaymentInfo(amount: String, currency: String, customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = systemAPI.getPaymentInfo(
                    token = "Bearer ${SECRET_KEY.value}",
                    customerID = customerId,
                    amount = amount,
                    currency = currency,
                    value = true
                )
                if (response.isSuccessful) {
                    clientSecret = response.body()!!.client_secret
                    Log.d(TAG, "Client secret received: ${response.body().toString()}")
                    paymentFlow()
                } else {
                    logErrorAndDismiss("Failed to get payment info", response.errorBody()?.string())
                }
            } catch (e: Exception) {
                logExceptionAndDismiss("getPaymentInfo", e)
            }
        }
    }

    private fun paymentFlow() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (clientSecret != null && ephemeralKey != null && ::customerId.isInitialized) {
                    paymentSheet.presentWithPaymentIntent(
                        clientSecret!!,
                        PaymentSheet.Configuration(
                            "Asian Group Distributor",
                            PaymentSheet.CustomerConfiguration(customerId, ephemeralKey!!)
                        )
                    )
                } else {
                    Log.e(TAG, "paymentFlow: Missing required data")
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                logExceptionAndDismiss("paymentFlow", e)
            }
        }
    }

    private fun onPaymentResult(paymentSheetResult: PaymentSheetResult) {
        paymentSuccessful = paymentSheetResult is PaymentSheetResult.Completed
        paymentResultListener.onPaymentResult(paymentSuccessful)
        dialog.dismiss()
        if (paymentSuccessful) resetKeys()
    }

    private fun resetKeys() {
        customerId = ""
        ephemeralKey = ""
    }

    private fun logErrorAndDismiss(message: String, error: String?) {
        Log.e(TAG, "$message: $error")
        dialog.dismissOnMainThread()
    }

    private fun logExceptionAndDismiss(functionName: String, e: Exception) {
        Log.e(TAG, "Exception in $functionName: ${e.message}")
        dialog.dismissOnMainThread()
    }

    private fun ProgressDialog.showOnMainThread() {
        CoroutineScope(Dispatchers.Main).launch { show() }
    }

    private fun ProgressDialog.dismissOnMainThread() {
        CoroutineScope(Dispatchers.Main).launch { dismiss() }
    }

    interface PaymentResultListener {
        fun onPaymentResult(isSuccessful: Boolean)
    }
}
