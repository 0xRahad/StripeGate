package com.androidafe.stripegate.models;

public class EphemeralResponse {

    private String id;

    public EphemeralResponse(String id) {
        this.id = id;
    }

    public String getEphemeralKey() {
        return id;
    }

    public void setEphemeralKey(String id) {
        this.id = id;
    }
}
