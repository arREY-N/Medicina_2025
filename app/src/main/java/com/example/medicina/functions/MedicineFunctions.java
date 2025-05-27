package com.example.medicina.functions;

import com.example.medicina.model.Medicine;

import com.example.medicina.network.ApiService;
import com.example.medicina.network.RetrofitClient;
import java.util.List;
import java.util.Collections;
import java.io.IOException;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Response;

public class MedicineFunctions {

    // BASE_URL is now managed by RetrofitClient
    // private static final String BASE_URL = "http://192.168.1.105/medicina_api/";
    private static final String TAG = "MedicineFunctions";


    public static List<Medicine> getAllMedicines() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Medicine>> call = apiService.getAllMedicinesFromServer();

        try {
            // Execute the call synchronously (this function is not a suspend function)
            // This should be called from a background thread (e.g., by Repository's coroutine)
            Response<List<Medicine>> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                Log.d(TAG, "Successfully fetched " + response.body().size() + " medicines from API.");
                return response.body();
            } else {
                Log.e(TAG, "API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) {
                    Log.e(TAG, "Error body: " + response.errorBody().string());
                }
                return Collections.emptyList(); // Return empty list on failure
            }
        } catch (IOException e) {
            Log.e(TAG, "Network IOException in getAllMedicines: " + e.getMessage(), e);
            return Collections.emptyList(); // Return empty list on network exception
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAllMedicines: " + e.getMessage(), e);
            return Collections.emptyList(); // Return empty list on other exceptions
        }
    }

    public static List<Medicine> readMedicine(String medicineName) {
        return null;
    }
    public static List<Medicine> searchMedicineByCategory(int categoryId) {
        return null;
    }
    public static boolean upsertMedicine(Medicine medicine){
        return false;
    }

}
