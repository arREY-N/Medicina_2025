package com.example.medicina.network;

import com.example.medicina.model.Medicine;
import com.example.medicina.model.UpsertResponse; // Import the new response model
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST; // For creating/updating

public interface ApiService {
    @GET("get_all_medicines.php")
    Call<List<Medicine>> getAllMedicinesFromServer();

    // Add this for upserting medicine
    @POST("upsert_medicine.php") // Your PHP endpoint for upserting
    Call<UpsertResponse> upsertMedicineOnServer(@Body Medicine medicine);
}