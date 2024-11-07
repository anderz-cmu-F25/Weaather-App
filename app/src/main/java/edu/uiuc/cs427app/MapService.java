package edu.uiuc.cs427app;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapService extends AsyncTask<String, Void, City> {

    @Override
    protected City doInBackground(String... strings) {
        try {
            // Make a GET request
            String urlString = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", strings[0], strings[1]);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept", "application/json");

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // If the response code is 200 (OK), read the input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                // Parse the JSON response using Gson
                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                // Example: Accessing a field from the response
                if (jsonResponse.has("status") && jsonResponse.get("status").getAsString().equals("OK")) {
                    JsonObject location = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                    return new City(strings[0], location.get("lat").getAsDouble(), location.get("lng").getAsDouble());
                }
            } else {
                Log.e("HttpRequest", "GET request failed. Response Code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("HttpRequest", "Error: " + e.getMessage());
        }

        return new City("Invalid", Double.MIN_VALUE, Double.MIN_VALUE);
    }
}
