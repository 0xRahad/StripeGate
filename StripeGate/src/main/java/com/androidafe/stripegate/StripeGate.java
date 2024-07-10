/*
 *
 *   Created Md Rahadul Islam on 12/16/23, 3:16 PM
 *   Copyright Ⓒ 2023. All rights reserved Ⓒ 2023 http://freefuninfo.com/
 *   Last modified: 12/16/23, 3:04 PM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 *
 */

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

    private PaymentSheet paymentSheet;
    private String customerID, ephemeralKey, clientSecret, amount, currency;
    Context context;
    ProgressDialog dialog;

    private static String SECRET_KEY;
    private static String PUBLISHABLE_KEY;
    private boolean paymentSuccessful;
    private PaymentResultListener paymentResultListener;
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

         dialog = new ProgressDialog(context);
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



    public void Integrate(String amount, String currency) {
        if (PUBLISHABLE_KEY == null || SECRET_KEY == null) {
            Log.e("error", "Publishable Key or Secret Key is empty ");
            return; // Return early if keys are null
        }
        this.amount = amount;
        this.currency = currency;
        PaymentConfiguration.init(context, PUBLISHABLE_KEY);
        createCustomerID();
        getPaymentInfo(this.amount, this.currency); // Use class fields here
    }

    // Overloaded method to accept double for the amount
    public void Integrate(double amount, String currency) {
        Integrate(String.valueOf((long) (amount * 100)), currency);
    }


    public void Apply(final PaymentResultListener paymentResultListener) {

        if (PUBLISHABLE_KEY == null || SECRET_KEY == null) {
            Log.d("error", "Publishable Key or Secret Key is empty ");
            paymentResultListener.onPaymentResult(false);
            return; // Return early if keys are null
        }

        dialog.show();
        createCustomerID();
        getPaymentInfo(amount,currency);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                PaymentFlow();
            }
        },2000);

    }



    private void createCustomerID(){
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

    private void getEphemeralKey(String id){
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
                    Log.d("res", "onError:"+response.errorBody());
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
            paymentSuccessful = true;
            Toast.makeText(context, "Payment Successfull", Toast.LENGTH_SHORT).show();
        }else {
            paymentSuccessful = false;
            Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT).show();
        }
        if (paymentResultListener != null) {
            paymentResultListener.onPaymentResult(paymentSuccessful);
        }
    }

    public interface PaymentResultListener {
        void onPaymentResult(boolean isSuccessful);
    }

}
