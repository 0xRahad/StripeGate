/*
 *
 *   Created Md Rahadul Islam on 12/16/23, 3:16 PM
 *   Copyright Ⓒ 2023. All rights reserved Ⓒ 2023 http://freefuninfo.com/
 *   Last modified: 12/15/23, 11:47 AM
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
