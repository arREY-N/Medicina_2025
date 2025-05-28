package com.example.medicina.functions;

import static java.util.regex.Pattern.matches;

import com.example.medicina.model.Account;
import com.example.medicina.model.Repository;
import java.util.List;
import android.util.Log;
import com.example.medicina.model.Account;
import com.example.medicina.model.UpsertResponse; // Reusing this model
import com.example.medicina.network.ApiService;
import com.example.medicina.network.RetrofitClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.StateFlow;

public class AccountFunctions {

    private static final String TAG = "AccountFunctions";

    public static Account handleLogin(String username, String password) throws MedicinaException {
        List<Account> accounts = Repository.getAccountsAtOnce();

        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                return account;
            }
        }
        throw new MedicinaException("Incorrect username or password");
    }

    public static String capitalizeWords(String str) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static boolean handleSignUp(
            String p_firstname, String p_lastname,
            String p_middlename, String username,
            String password, String confirmPassword) throws MedicinaException {

        List<Account> accounts = Repository.getAccountsAtOnce();

        String firstname = capitalizeWords(p_firstname);
        String lastname = capitalizeWords(p_lastname);
        String middlename = capitalizeWords(p_middlename);

        if(firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || password.isEmpty()){
            throw new MedicinaException("Please fill up all information fields!");
        }

        if(!username.matches("^[a-zA-Z0-9]+$")){
            throw new MedicinaException("Username can only contain letters and numbers!");
        }

        if(username.length() < 8){
            throw new MedicinaException("Username must be at least 8 characters long!");
        }

        if(password.length() < 8){
            throw new MedicinaException("Password must be at least 8 characters long!");
        }

        if(!password.equals(confirmPassword)){
            throw new MedicinaException("Please retype password!");
        }

        for (Account account : accounts) {
            if (account.getUsername().equals(username.trim())) {
                throw new MedicinaException("Username already in use!");
            }
            if(account.getFirstname().equals(firstname.trim()) && account.getLastname().equals(lastname.trim()) && account.getMiddlename().equals(middlename.trim())){
                throw new MedicinaException("User found! Log-in instead.");
            }
        }

        return true;
    }

    public static boolean handleSignUp(
            int id,
            String p_firstname, String p_lastname,
            String p_middlename, String username,
            String password, String confirmPassword) throws MedicinaException {

        List<Account> accounts = Repository.getAccountsAtOnce();

        String firstname = capitalizeWords(p_firstname);
        String lastname = capitalizeWords(p_lastname);
        String middlename = capitalizeWords(p_middlename);

        if(firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || password.isEmpty()){
            throw new MedicinaException("Please fill up all information fields!");
        }

        if(!username.matches("^[a-zA-Z0-9]+$")){
            throw new MedicinaException("Username can only contain letters and numbers!");
        }

        if(username.length() < 8){
            throw new MedicinaException("Username must be at least 8 characters long!");
        }

        for (Account account : accounts) {
            int accountId = (account.getId() != null) ? account.getId() : 0;

            System.out.println("Account ID: " + accountId + "Username: " + account.getUsername());
            System.out.println("Check ID: " + id + "Username: " + username.trim());

            if (
                    account.getUsername().equals(username.trim()) &&
                            accountId != id) {
                throw new MedicinaException("Username already in use!");
            }
            if(account.getFirstname().equals(firstname.trim()) && account.getLastname().equals(lastname.trim()) && account.getMiddlename().equals(middlename.trim()) && accountId != id){
                throw new MedicinaException("User found! Log-in instead.");
            }
        }

        if(password.length() < 8){
            throw new MedicinaException("Password must be at least 8 characters long!");
        }

        if(!password.equals(confirmPassword)){
            throw new MedicinaException("Please retype password!");
        }

        return true;
    }


    public static List<Account> getAllAccounts() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Account>> call = apiService.getAllAccountsFromServer();
        try {
            Response<List<Account>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                Log.d(TAG, "Successfully fetched " + response.body().size() + " accounts from API.");
                return response.body();
            } else {
                Log.e(TAG, "getAllAccounts API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAllAccounts: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public static long upsertAccount(Account account) { // Takes your Account object
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.upsertAccountOnServer(account); // Sends your Account object
        try {
            Log.d(TAG, "Attempting to upsert account: " + account.getUsername()); // Use getter if fields are private
            Response<UpsertResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse upsertResponse = response.body();
                if (upsertResponse.isSuccess() && upsertResponse.getId() != null) {
                    Log.d(TAG, "Server account upsert successful. ID: " + upsertResponse.getId());
                    return upsertResponse.getId().longValue();
                } else {
                    Log.e(TAG, "Server account upsert reported failure. Message: " + (upsertResponse.getMessage() != null ? upsertResponse.getMessage() : "N/A"));
                    return -1L;
                }
            } else {
                Log.e(TAG, "upsertAccount API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return -1L;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in upsertAccount: " + e.getMessage(), e);
            return -1L;
        }
    }

    public static boolean deleteAccount(int accountId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UpsertResponse> call = apiService.deleteAccountOnServer(accountId);
        try {
            Log.d(TAG, "Attempting to delete account ID: " + accountId);
            Response<UpsertResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                UpsertResponse deleteResponse = response.body();
                if (deleteResponse.isSuccess()) {
                    Log.d(TAG, "Server account delete successful for ID " + accountId);
                    return true;
                } else {
                    Log.e(TAG, "Server account delete reported failure for ID " + accountId + ". Message: " + (deleteResponse.getMessage() != null ? deleteResponse.getMessage() : "N/A"));
                    return false;
                }
            } else {
                Log.e(TAG, "deleteAccount API call failed: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) Log.e(TAG, "Error body: " + response.errorBody().string());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in deleteAccount: " + e.getMessage(), e);
            return false;
        }
    }



}
