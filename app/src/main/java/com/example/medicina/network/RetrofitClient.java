package com.example.medicina.network;

import static android.content.ContentValues.TAG;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // IMPORTANT: Use your WAMP server's actual IP address, not localhost or 127.0.0.1
    // if you are running the app on an emulator or a separate physical device.
    // Example: "http://192.168.1.105/medicina_api/"
        private static final String BASE_URL = "http://192.168.0.115/Medisina_apis/"; // <<-- UPDATE THIS
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        Log.d(TAG, "Successfully fetched " + retrofit.toString() + " faegseagwsegesgesgsgsgsegsegesgsegesgfbd");

        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}