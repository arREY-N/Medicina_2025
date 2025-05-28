package com.example.medicina.functions;

import android.util.Log;
import com.example.medicina.model.Order; // Your Order data class
import com.example.medicina.model.UpsertResponse; // Reusing this
import com.example.medicina.network.ApiService;
import com.example.medicina.network.RetrofitClient;

import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class OrderFunctions {
    private static final String TAG = "OrderFunctions";

    public static List<Order> getAllOrders() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Order>> call = apiService.getAllOrdersFromServer();
        try {
            Response<List<Order>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                Log.d(TAG, "Successfully fetched " + response.body().size() + " orders from API.");
                return response.body();
            } else {
                Log.e(TAG, "getAllOrders API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAllOrders: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public static long upsertOrder(Order order) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.upsertOrderOnServer(order);
        try {
            Log.d(TAG, "Attempting to upsert order ID: " + (order.getId() == null ? "NEW" : order.getId()));
            Response<UpsertResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse upsertResponse = response.body();
                if (upsertResponse.isSuccess() && upsertResponse.getId() != null) {
                    Log.d(TAG, "Server order upsert successful. ID: " + upsertResponse.getId());
                    return upsertResponse.getId().longValue();
                } else {
                    Log.e(TAG, "Server order upsert reported failure. Message: " + (upsertResponse.getMessage() != null ? upsertResponse.getMessage() : "N/A"));
                    return -1L;
                }
            } else {
                Log.e(TAG, "upsertOrder API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return -1L;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in upsertOrder: " + e.getMessage(), e);
            return -1L;
        }
    }

    public static boolean deleteOrder(int orderId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.deleteOrderOnServer(orderId);
        try {
            Log.d(TAG, "Attempting to delete order ID: " + orderId);
            Response<UpsertResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse deleteResponse = response.body();
                if (deleteResponse.isSuccess()) {
                    Log.d(TAG, "Server order delete successful for ID " + orderId);
                    return true;
                } else {
                    Log.e(TAG, "Server order delete reported failure for ID " + orderId + ". Message: " + (deleteResponse.getMessage() != null ? deleteResponse.getMessage() : "N/A"));
                    return false;
                }
            } else {
                Log.e(TAG, "deleteOrder API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in deleteOrder: " + e.getMessage(), e);
            return false;
        }
    }
}