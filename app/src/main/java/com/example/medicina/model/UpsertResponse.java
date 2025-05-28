package com.example.medicina.model;

import com.google.gson.annotations.SerializedName;

public class UpsertResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private Integer id; // Use Integer to allow for null if not returned

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getId() {
        return id;
    }

    // Optional: Setters if needed, though primarily for deserialization
}