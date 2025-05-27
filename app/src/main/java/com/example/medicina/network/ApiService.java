package com.example.medicina.network;

import android.util.Log;

import com.example.medicina.model.Medicine;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("get_all_medicines.php") // This is the endpoint relative to BASE_URL
    Call<List<Medicine>> getAllMedicinesFromServer();



}