package com.example.medicina.network;

import com.example.medicina.model.Account;
import com.example.medicina.model.Category;
import com.example.medicina.model.Medicine;
import com.example.medicina.model.Order;
import com.example.medicina.model.UpsertResponse; // Reusing for delete response
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;         // Import for @Field
import retrofit2.http.FormUrlEncoded; // Import for @FormUrlEncoded
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    //Medicine
    @GET("get_all_medicines.php")
    Call<List<Medicine>> getAllMedicinesFromServer();

    @POST("upsert_medicine.php")
    Call<UpsertResponse> upsertMedicineOnServer(@Body Medicine medicine);

    // New method for deleting medicine
    @FormUrlEncoded // Indicates that the request body will be form-URL-encoded
    @POST("delete_medicine.php") // Your PHP endpoint for deleting
    Call<UpsertResponse> deleteMedicineOnServer(@Field("id") int medicineId); // "id" is the name of the POST field


    //Accounts
    @GET("get_all_accounts.php")
    Call<List<Account>> getAllAccountsFromServer();

    @POST("upsert_account.php")
    Call<UpsertResponse> upsertAccountOnServer(@Body Account account);

    @FormUrlEncoded
    @POST("delete_account.php")
    Call<UpsertResponse> deleteAccountOnServer(@Field("id") int accountId);

    //Category
    @GET("get_all_categories.php")
    Call<List<Category>> getAllCategoriesFromServer();

    @POST("upsert_category.php")
    Call<UpsertResponse> upsertCategoryOnServer(@Body Category category);

    @FormUrlEncoded
    @POST("delete_category.php")
    Call<UpsertResponse> deleteCategoryOnServer(@Field("id") int categoryId);

    //Orders
    @GET("get_all_orders.php")
    Call<List<Order>> getAllOrdersFromServer();

    @POST("upsert_order.php")
    Call<UpsertResponse> upsertOrderOnServer(@Body Order order);

    @FormUrlEncoded
    @POST("delete_order.php")
    Call<UpsertResponse> deleteOrderOnServer(@Field("id") int orderId);

}