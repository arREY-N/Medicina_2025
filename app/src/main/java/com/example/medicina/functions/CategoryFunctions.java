package com.example.medicina.functions;

import android.util.Log;
import com.example.medicina.model.Category; // Your Category data class
import com.example.medicina.model.UpsertResponse; // Reusing this
import com.example.medicina.network.ApiService;
import com.example.medicina.network.RetrofitClient;

import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class CategoryFunctions {
    private static final String TAG = "CategoryFunctions";

    public static List<Category> getAllCategories() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Category>> call = apiService.getAllCategoriesFromServer();
        try {
            Response<List<Category>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                Log.d(TAG, "Successfully fetched " + response.body().size() + " categories from API.");
                return response.body();
            } else {
                Log.e(TAG, "getAllCategories API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAllCategories: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public static long upsertCategory(Category category) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.upsertCategoryOnServer(category);
        try {
            Log.d(TAG, "Attempting to upsert category: " + category.getCategoryName());
            Response<UpsertResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse upsertResponse = response.body();
                if (upsertResponse.isSuccess() && upsertResponse.getId() != null) {
                    Log.d(TAG, "Server category upsert successful. ID: " + upsertResponse.getId());
                    return upsertResponse.getId().longValue();
                } else {
                    Log.e(TAG, "Server category upsert reported failure. Message: " + (upsertResponse.getMessage() != null ? upsertResponse.getMessage() : "N/A"));
                    return -1L;
                }
            } else {
                Log.e(TAG, "upsertCategory API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return -1L;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in upsertCategory: " + e.getMessage(), e);
            return -1L;
        }
    }

    public static boolean deleteCategory(int categoryId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.deleteCategoryOnServer(categoryId);
        try {
            Log.d(TAG, "Attempting to delete category ID: " + categoryId);
            Response<UpsertResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse deleteResponse = response.body();
                if (deleteResponse.isSuccess()) {
                    Log.d(TAG, "Server category delete successful for ID " + categoryId);
                    return true;
                } else {
                    Log.e(TAG, "Server category delete reported failure for ID " + categoryId + ". Message: " + (deleteResponse.getMessage() != null ? deleteResponse.getMessage() : "N/A"));
                    return false;
                }
            } else {
                Log.e(TAG, "deleteCategory API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in deleteCategory: " + e.getMessage(), e);
            return false;
        }
    }
}