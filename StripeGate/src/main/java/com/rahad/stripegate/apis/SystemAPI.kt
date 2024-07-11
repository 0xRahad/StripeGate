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
package com.rahad.stripegate.apis
import com.rahad.stripegate.models.CustomerResponse
import com.rahad.stripegate.models.EphemeralResponse
import com.rahad.stripegate.models.PaymentResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SystemAPI {

    @POST("v1/customers")
    suspend fun getCustomerID(
        @Header("Authorization") token: String
    ): Response<CustomerResponse?>


    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKeys(
        @Header("Authorization") token: String,
        @Header("Stripe-Version") version: String,
        @Query("customer") customerID: String
    ): Response<EphemeralResponse?>



    @POST("v1/payment_intents")
    suspend fun getPaymentInfo(
        @Header("Authorization") token: String,
        @Query("customer") customerID: String,
        @Query("amount") amount: String,
        @Query("currency") currency: String,
        @Query("automatic_payment_methods[enabled]") value: Boolean

    ): Response<PaymentResponse?>
}
