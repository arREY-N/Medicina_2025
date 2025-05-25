package com.example.medicina.functions;

import android.util.Patterns; // For email validation

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class AccountFunctions {

    // Make sure this is HTTPS in production!
    private static final String BASE_URL = "http://192.168.0.115/REYN_ITO_UNG_GAWA_KO_APIS_PHP/";
    private static final int CONNECT_TIMEOUT = 15000; // 15 seconds
    private static final int READ_TIMEOUT = 10000;    // 10 seconds

    // JSON response keys
    private static final String JSON_KEY_STATUS = "status";
    private static final String JSON_KEY_MESSAGE = "message";

    // JSON response values
    private static final String STATUS_SUCCESS = "success";
    private static final String MESSAGE_NO_ACCOUNT = "no_account";
    private static final String MESSAGE_WRONG_PASSWORD = "wrong_password";
    private static final String MESSAGE_USERNAME_TAKEN = "username_taken";
    private static final String MESSAGE_EMAIL_TAKEN = "email_taken";
    private static final String MESSAGE_UNKNOWN_ERROR = "Unknown error";


    private static JSONObject makePostRequest(String endpoint, Map<String, String> params) throws NetworkErrorException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(out.length);
            // conn.connect(); // getOutputStream implicitly connects

            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder responseString = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseString.append(line);
                    }
                }
                return new JSONObject(responseString.toString());
            } else {
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                }
                throw new NetworkErrorException("Server error: " + responseCode + " - " + errorResponse.toString());
            }
        } catch (Exception e) { // Catch IOException, JSONException, MalformedURLException etc.
            throw new NetworkErrorException("Network or parsing error: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static boolean handleLogin(String username, String password)
            throws NoAccountException, WrongPasswordException, NetworkErrorException {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        try {
            JSONObject jsonResponse = makePostRequest("login.php", params);
            String status = jsonResponse.getString(JSON_KEY_STATUS);

            if (STATUS_SUCCESS.equals(status)) {
                return true; // Login successful
            } else {
                String message = jsonResponse.optString(JSON_KEY_MESSAGE, MESSAGE_UNKNOWN_ERROR);
                if (MESSAGE_NO_ACCOUNT.equals(message)) {
                    throw new NoAccountException();
                } else if (MESSAGE_WRONG_PASSWORD.equals(message)) {
                    throw new WrongPasswordException();
                } else {
                    throw new NetworkErrorException("Login failed: " + message);
                }
            }
        } catch (NoAccountException | WrongPasswordException e) {
            throw e; // Re-throw specific exceptions
        } catch (NetworkErrorException e) { // Already a NetworkErrorException from makePostRequest
            throw e;
        } catch (Exception e) { // Catch other potential issues like JSONException if status key missing
            throw new NetworkErrorException("Error processing login response: " + e.getMessage(), e);
        }
    }

    public static boolean handleSignUp(String username, String password, String email)
            throws UsernameTakenException, EmailTakenException, NetworkErrorException, IllegalArgumentException {

        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Corrected import
            throw new IllegalArgumentException("Invalid email format.");
        }

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);

        try {
            JSONObject jsonResponse = makePostRequest("signup.php", params);
            String status = jsonResponse.getString(JSON_KEY_STATUS);

            if (STATUS_SUCCESS.equals(status)) {
                return true; // Signup successful
            } else {
                String message = jsonResponse.optString(JSON_KEY_MESSAGE, "Unknown error during signup");
                if (MESSAGE_USERNAME_TAKEN.equals(message)) {
                    throw new UsernameTakenException();
                } else if (MESSAGE_EMAIL_TAKEN.equals(message)) {
                    throw new EmailTakenException();
                } else {
                    throw new NetworkErrorException("Signup failed: " + message);
                }
            }
        } catch (UsernameTakenException | EmailTakenException e) {
            throw e; // Re-throw specific exceptions
        } catch (NetworkErrorException e) {
            throw e;
        } catch (Exception e) { // Catch other potential issues like JSONException if status key missing
            throw new NetworkErrorException("Error processing signup response: " + e.getMessage(), e);
        }
    }
}
