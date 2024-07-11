package com.rahad.stripegate.models

data class PaymentResponse(
    val id:String,
    val `object`:String,
    val amount:String,
    val amount_received:String,
    val application:String,
    val cancellation_reason:String,
    val capture_method:String,
    val client_secret:String,
    val confirmation_method:String,
    val customer:String,
)