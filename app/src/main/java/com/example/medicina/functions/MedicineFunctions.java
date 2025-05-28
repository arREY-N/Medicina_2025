package com.example.medicina.functions;

import com.example.medicina.model.Medicine;

import com.example.medicina.network.ApiService;
import com.example.medicina.network.RetrofitClient;
import com.example.medicina.model.UpsertResponse;
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

    public static long upsertMedicine(Medicine medicine) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.upsertMedicineOnServer(medicine);

        try {
            Log.d(TAG, "Attempting to upsert medicine: " + (medicine.getId() == null ? "NEW" : "ID " + medicine.getId()) + " - " + medicine.getBrandName());
            Response<UpsertResponse> response = call.execute(); // Synchronous call

            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse upsertResponse = response.body();
                if (upsertResponse.isSuccess() && upsertResponse.getId() != null) {
                    Log.d(TAG, "Server upsert successful. Message: " + upsertResponse.getMessage() + ". Server ID: " + upsertResponse.getId());
                    return upsertResponse.getId().longValue(); // Return the ID from the server
                } else {
                    Log.e(TAG, "Server upsert reported failure or no ID. Message: " + (upsertResponse.getMessage() != null ? upsertResponse.getMessage() : "N/A"));
                    return -1L;
                }
            } else {
                String errorBodyString = "N/A";
                if (response.errorBody() != null) {
                    try {
                        errorBodyString = response.errorBody().string();
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body string", e);
                    }
                }
                Log.e(TAG, "upsertMedicine API call failed: " + response.code() + " - " + response.message() + ". Error Body: " + errorBodyString);
                return -1L;
            }
        } catch (IOException e) {
            Log.e(TAG, "Network IOException in upsertMedicine: " + e.getMessage(), e);
            return -1L;
        } catch (Exception e) { // Catch any other unexpected exceptions
            Log.e(TAG, "Unexpected exception in upsertMedicine: " + e.getMessage(), e);
            return -1L;
        }
    }


    public static boolean deleteMedicineFromServer(int medicineId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.deleteMedicineOnServer(medicineId);

        try {
            Log.d(TAG, "Attempting to delete medicine with ID: " + medicineId + " from server.");
            Response<UpsertResponse> response = call.execute(); // Synchronous call

            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse deleteResponse = response.body();
                if (deleteResponse.isSuccess()) {
                    Log.d(TAG, "Server delete successful for ID " + medicineId + ". Message: " + deleteResponse.getMessage());
                    return true;
                } else {
                    Log.e(TAG, "Server delete reported failure for ID " + medicineId + ". Message: " + (deleteResponse.getMessage() != null ? deleteResponse.getMessage() : "N/A"));
                    return false;
                }
            } else {
                String errorBodyString = "N/A";
                if (response.errorBody() != null) {
                    try {
                        errorBodyString = response.errorBody().string();
                    } catch (IOException e) { /* ignore */ }
                }
                Log.e(TAG, "deleteMedicineFromServer API call failed: " + response.code() + " - " + response.message() + ". Error Body: " + errorBodyString);
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Network IOException in deleteMedicineFromServer for ID " + medicineId + ": " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected exception in deleteMedicineFromServer for ID " + medicineId + ": " + e.getMessage(), e);
            return false;
        }
    }

    // Stubs for other methods if not yet implemented
    public static List<Medicine> readMedicine(String medicineName) {
        Log.w(TAG, "readMedicine not implemented yet");
        return Collections.emptyList();
    }
    public static List<Medicine> searchMedicineByCategory(int categoryId) {
        Log.w(TAG, "searchMedicineByCategory not implemented yet");
        return Collections.emptyList();
    }

}
