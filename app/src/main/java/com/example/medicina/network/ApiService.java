package com.example.medicina.network;

import com.example.medicina.model.Medicine;
import com.example.medicina.model.UpsertResponse; // Reusing for delete response
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;         // Import for @Field
import retrofit2.http.FormUrlEncoded; // Import for @FormUrlEncoded
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("get_all_medicines.php")
    Call<List<Medicine>> getAllMedicinesFromServer();

    @POST("upsert_medicine.php")
    Call<UpsertResponse> upsertMedicineOnServer(@Body Medicine medicine);

    // New method for deleting medicine
    @FormUrlEncoded // Indicates that the request body will be form-URL-encoded
    @POST("delete_medicine.php") // Your PHP endpoint for deleting
    Call<UpsertResponse> deleteMedicineOnServer(@Field("id") int medicineId); // "id" is the name of the POST field
}