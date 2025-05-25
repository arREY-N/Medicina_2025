package com.example.medicina.functions;

import android.util.Log; // Import Log

import com.example.medicina.model.Medicine; // Assuming your Medicine model is here

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MedicineFunctions {

    // --- Base URL Configuration ---
    // IMPORTANT: Replace with your server URL base
    private static final String BASE_URL = "http://192.168.0.115/www/REYN_ITO_UNG_GAWA_KO_APIS_PHP/";
    private static final String SCRIPT_GET_ALL_MEDICINES = "get_medicines.php"; // Script to get all
    private static final String SCRIPT_SEARCH_MEDICINES = "search_medicines.php"; // Script for searching (by name or category)
    private static final String SCRIPT_UPSERT_MEDICINE = "add_medicine.php"; // Script for create/update

    private static final String TAG = "MedicineFunctions";

    /**
     * Fetches all medicines from the server.
     * WARNING: This is a blocking network operation. Call from a background thread.
     *
     * @return List of Medicine objects, or null/empty list on error.
     */
    public static List<Medicine> getAllMedicines() {
        String urlString = BASE_URL + SCRIPT_GET_ALL_MEDICINES;
        String jsonResponse = performGetRequest(urlString);
        if (jsonResponse != null) {
            return parseMedicinesListJsonResponse(jsonResponse);
        }
        return null; // Or new ArrayList<>();
    }

    /**
     * Searches for medicines by name.
     * WARNING: This is a blocking network operation. Call from a background thread.
     *
     * @param medicineName The name (or part of the name) to search for.
     * @return List of Medicine objects, or null/empty list on error.
     */
    public static List<Medicine> readMedicine(String medicineName) {
        if (medicineName == null || medicineName.trim().isEmpty()) {
            Log.w(TAG, "readMedicine: medicineName is empty or null.");
            return new ArrayList<>();
        }
        try {
            String encodedName = URLEncoder.encode(medicineName, StandardCharsets.UTF_8.name());
            String urlString = BASE_URL + SCRIPT_SEARCH_MEDICINES + "?name=" + encodedName; // Assuming PHP expects 'name' parameter
            String jsonResponse = performGetRequest(urlString);
            if (jsonResponse != null) {
                return parseMedicinesListJsonResponse(jsonResponse);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error encoding medicineName: " + e.getMessage(), e);
        }
        return null; // Or new ArrayList<>();
    }

    /**
     * Searches for medicines by category ID.
     * WARNING: This is a blocking network operation. Call from a background thread.
     *
     * @param categoryId The ID of the category to search for.
     * @return List of Medicine objects, or null/empty list on error.
     */
    public static List<Medicine> searchMedicineByCategory(int categoryId) {
        String urlString = BASE_URL + SCRIPT_SEARCH_MEDICINES + "?categoryId=" + categoryId; // Assuming PHP expects 'categoryId'
        String jsonResponse = performGetRequest(urlString);
        if (jsonResponse != null) {
            return parseMedicinesListJsonResponse(jsonResponse);
        }
        return null; // Or new ArrayList<>();
    }

    /**
     * Creates or updates a medicine on the server.
     * WARNING: This is a blocking network operation. Call from a background thread.
     *
     * @param medicine The Medicine object to upsert.
     * @return true if successful, false otherwise.
     */
    public static boolean upsertMedicine(Medicine medicine) {
        if (medicine == null) {
            Log.e(TAG, "upsertMedicine: Medicine object is null.");
            return false;
        }
        String urlString = BASE_URL + SCRIPT_UPSERT_MEDICINE;

        // Prepare POST data (example: form URL encoded)
        // Your PHP script will determine the expected format (JSON, form-data, etc.)
        Map<String, String> params = new HashMap<>();
        params.put("MedicineID", String.valueOf(medicine.getMedicineId())); // Send ID for updates
        params.put("Brand_Name", medicine.getBrandName());
        params.put("Generic_Name", medicine.getGenericName());
        params.put("Price", String.valueOf(medicine.getPrice()));
        params.put("CategoryID", String.valueOf(medicine.getCategoryId()));
        params.put("RegulationID", String.valueOf(medicine.getRegulationId()));
        params.put("Description", medicine.getDescription());
        
        // Add any other fields your Medicine model and PHP script expect

        String postData = getPostDataString(params);
        String jsonResponse = performPostRequest(urlString, postData);

        if (jsonResponse != null) {
            try {
                JSONObject responseObject = new JSONObject(jsonResponse);
                boolean error = responseObject.optBoolean("error", true); // Default to true if 'error' field is missing
                // String message = responseObject.optString("message", "Unknown response");
                // Log.d(TAG, "Upsert response message: " + message);
                return !error; // Success if error is false
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing upsert JSON response: " + e.getMessage(), e);
            }
        }
        return false;
    }


    // --- Private Helper Methods for Networking ---

    private static String performGetRequest(String urlString) {
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000); // 15 seconds
            conn.setConnectTimeout(15000); // 15 seconds
            // conn.connect(); // For GET, connect() is often implicit with getInputStream()

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "GET " + urlString + " - Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                return result.toString();
            } else {
                // Read error stream if not HTTP_OK
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                StringBuilder errorResult = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    errorResult.append(line);
                }
                in.close();
                Log.e(TAG, "GET " + urlString + " - Server error response: " + errorResult.toString());
                // Optionally, try to parse this as a JSON error from your API
                // return "{\"error\":true, \"message\":\"Server error: " + responseCode + "\"}";
                return null; // Indicate error
            }
        } catch (Exception e) {
            Log.e(TAG, "GET " + urlString + " - Exception: " + e.getMessage(), e);
            // return "{\"error\":true, \"message\":\"Exception: " + e.getMessage() + "\"}";
            return null; // Indicate error
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String performPostRequest(String urlString, String postData) {
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true); // Important for POST

            // Send POST data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "POST " + urlString + " - Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                return result.toString();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                StringBuilder errorResult = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    errorResult.append(line);
                }
                in.close();
                Log.e(TAG, "POST " + urlString + " - Server error response: " + errorResult.toString());
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "POST " + urlString + " - Exception: " + e.getMessage(), e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // --- Private Helper Methods for JSON Parsing ---

    private static List<Medicine> parseMedicinesListJsonResponse(String jsonString) {
        List<Medicine> medicinesList = new ArrayList<>();
        if (jsonString == null || jsonString.isEmpty()) {
            Log.e(TAG, "parseMedicinesListJsonResponse: Input JSON string is null or empty.");
            return medicinesList; // Return empty list
        }

        try {
            JSONObject jsonResponse = new JSONObject(jsonString);
            boolean error = jsonResponse.optBoolean("error", false); // Default to false if 'error' field is missing
            // String message = jsonResponse.optString("message", "");

            if (!error) {
                // Assuming your PHP script wraps the medicines in a "medicines" JSONArray
                // Adjust if your PHP script's JSON structure is different
                if (jsonResponse.has("medicines")) {
                    JSONArray medicinesJsonArray = jsonResponse.getJSONArray("medicines");

                    for (int i = 0; i < medicinesJsonArray.length(); i++) {
                        JSONObject medJson = medicinesJsonArray.getJSONObject(i);

                        int medId = medJson.getInt("MedicineID");
                        String brand = medJson.getString("Brand_Name");
                        String generic = medJson.getString("Generic_Name");
                        Integer catId = medJson.isNull("CategoryID") ? null : medJson.getInt("CategoryID");
                        Integer regId = medJson.isNull("RegulationID") ? null : medJson.getInt("RegulationID");
                        float price = (float) medJson.getLong("Price");
                        String desc = medJson.isNull("Description") ? null : medJson.getString("Description");
                        int quant = medJson.getInt("Quantity");
                        Medicine medicine = new Medicine(medId, brand, generic, catId, regId, price, desc, quant);
                        medicinesList.add(medicine);
                    }
                    Log.i(TAG, "Parsed " + medicinesList.size() + " medicines.");
                } else {
                    Log.w(TAG, "parseMedicinesListJsonResponse: 'medicines' JSONArray not found in response.");
                }
            } else {
                String errorMessage = jsonResponse.optString("message", "Unknown API error");
                Log.e(TAG, "API Error when fetching medicines list: " + errorMessage);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing error for medicines list: " + e.getMessage() + "\nRaw JSON: " + jsonString.substring(0, Math.min(jsonString.length(), 200)), e);
        }
        return medicinesList;
    }

    // Helper to build POST data string (form-urlencoded)
    private static String getPostDataString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error encoding POST data: " + e.getMessage(), e);
            return ""; // Or throw an exception
        }
        return result.toString();
    }
}