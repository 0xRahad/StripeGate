package com.androidafe.stripegate;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import com.androidafe.stripegate.models.CustomerResponse;
import com.androidafe.stripegate.models.EphemeralResponse;
import com.androidafe.stripegate.models.PaymentResponse;
import com.androidafe.stripegate.modules.PARAMS;
import com.androidafe.stripegate.modules.ProgressDialog;
import com.androidafe.stripegate.modules.RetrofitClient;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.PaymentSheetResultCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripeGate {

    PaymentSheet paymentSheet;
    String customerID, ephemeralKey, clientSecret, amount, currency;
    Context context;
    ProgressDialog dialog = new ProgressDialog();

    public static String SECRET_KEY;
    public static String PUBLISHABLE_KEY;

    public static void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public static void setPublishableKey(String publishableKey) {
        PUBLISHABLE_KEY = publishableKey;
    }


    public StripeGate(Context context) {
        this.context = context;
        initPaymentSheet();

        // Check if PUBLISHABLE_KEY and SECRET_KEY are not null
        if (PUBLISHABLE_KEY == null || SECRET_KEY == null) {
            Log.d("error", "Publishable Key or Secret Key is empty: ");
        }
    }

    private void initPaymentSheet() {
        PaymentSheetResultCallback paymentSheetCallback = new PaymentSheetResultCallback() {
            @Override
            public void onPaymentSheetResult(@NonNull PaymentSheetResult paymentSheetResult) {
                StripeGate.this.onPaymentResult(paymentSheetResult);
            }
        };

        paymentSheet = new PaymentSheet((ComponentActivity) context, paymentSheetCallback);
    }



    public void Integrate(String amount, String currency){

        if (PUBLISHABLE_KEY == null || SECRET_KEY == null) {
            Log.e("error", "Publishable Key or Secret Key is empty ");
            return; // Return early if keys are null
        }
        this.amount = amount;
        this.currency = currency;
        PaymentConfiguration.init(context, PUBLISHABLE_KEY);
        createCustomerID();
        getPaymentInfo(amount,currency);
    }

    public void Apply() {

        if (PUBLISHABLE_KEY == null || SECRET_KEY == null) {
            Log.d("error", "Publishable Key or Secret Key is empty ");
            return; // Return early if keys are null
        }

        dialog.showDialog(context);
        createCustomerID();
        getPaymentInfo(amount,currency);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismissDialog();
                PaymentFlow();
            }
        },2000);

    }


    public void createCustomerID(){
        Call<CustomerResponse> call = RetrofitClient.getInstance().getAPI().getCustomerID("Bearer "+ SECRET_KEY);
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                if (response.isSuccessful()){
                    CustomerResponse cm = response.body();
                    customerID = cm.getId();
                    getEphemeralKey(customerID);
                }else {
                    Log.d("res", "onResponse: failed");
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                Log.d("res", "onError: failed"+t.getLocalizedMessage());
            }
        });
    }

    public void getEphemeralKey(String id){
        Call<EphemeralResponse> keys = RetrofitClient.getInstance().getAPI().getEphemeralKeys("Bearer "+SECRET_KEY
                ,"2023-10-16",customerID);

        keys.enqueue(new Callback<EphemeralResponse>() {
            @Override
            public void onResponse(Call<EphemeralResponse> call, Response<EphemeralResponse> response) {
                if (response.isSuccessful()){
                    EphemeralResponse ephemeralResponse = response.body();
                    ephemeralKey = ephemeralResponse.getEphemeralKey();

                }else {
                    Log.d("res", "onResponse: failed");
                }
            }

            @Override
            public void onFailure(Call<EphemeralResponse> call, Throwable t) {
                Log.d("res", "ephemeral: failed"+t.getLocalizedMessage());
            }
        });

    }

    public void getPaymentInfo(String amount, String currency){
        Call<PaymentResponse> paymentInfo = RetrofitClient.getInstance().getAPI().getPaymentInfo("Bearer "+SECRET_KEY,
                customerID, amount,currency,true);
        paymentInfo.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful()){
                    PaymentResponse res = response.body();
                    clientSecret = res.getClientSecret();
                }else {
                    Log.d("res", "onError: client secret not found");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.d("res", "onError: failed"+t.getLocalizedMessage());
            }
        });
    }

    public void PaymentFlow() {
        if (ephemeralKey != null && clientSecret != null && customerID != null) {
            paymentSheet.presentWithPaymentIntent(
                    clientSecret, new PaymentSheet.Configuration("My Company",
                            new PaymentSheet.CustomerConfiguration(
                                    customerID,
                                    ephemeralKey
                            ))
            );
        } else {
            Log.d("res", "EphemeralKey, ClientSecret, or CustomerID is null. Cannot initiate payment flow.");
        }
    }

    public void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            customerID = null;
            ephemeralKey = null;
            Toast.makeText(context, "Payment Successfull", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT).show();
        }
    }

}
