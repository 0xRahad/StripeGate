package com.androidafe.stripegate.interfaces;

import com.androidafe.stripegate.models.CustomerResponse;
import com.androidafe.stripegate.models.EphemeralResponse;
import com.androidafe.stripegate.models.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitInstance {

    @POST("v1/customers")
    Call<CustomerResponse> getCustomerID(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("v1/ephemeral_keys")
    Call<EphemeralResponse> getEphemeralKeys (
            @Header("Authorization") String token,
            @Header("Stripe-Version") String version,
            @Field("customer") String customerID
    );


    @FormUrlEncoded
    @POST("v1/payment_intents")
    Call<PaymentResponse> getPaymentInfo (
            @Header("Authorization") String token,
            @Field("customer") String customerID,
            @Field("amount") String amount,
            @Field("currency") String currency,
            @Field("automatic_payment_methods[enabled]") boolean value

    );
}
