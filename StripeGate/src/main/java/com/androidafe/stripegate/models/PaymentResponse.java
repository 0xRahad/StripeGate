package com.androidafe.stripegate.models;

public class PaymentResponse {
    private String id;
    private String client_secret;

    public PaymentResponse(String id, String client_secret) {
        this.id = id;
        this.client_secret = client_secret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public void setClientSecret(String client_secret) {
        this.client_secret = client_secret;
    }
}
