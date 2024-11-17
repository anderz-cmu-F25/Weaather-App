package edu.uiuc.cs427app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapService extends AsyncTask<String, Void, City> {

    /**
     * This method performs a background task to retrieve geographical data (latitude and longitude)
     * for a given address using the Google Maps Geocoding API. It sends an HTTP GET request to the API
     * and processes the JSON response to extract the location coordinates.
     *
     * @param strings A variable-length array of strings, where the first element is expected
     *                to be the address to geocode.
     * @return A {@link City} object containing the address, latitude, and longitude of the location.
     *         If the request fails or the address is invalid, returns a {@link City} object with
     *         placeholder values ("Invalid", Double.MIN_VALUE, Double.MIN_VALUE).
     *
     * Method Flow:
     * 1. Constructs a URL for the Google Maps Geocoding API with the provided address.
     * 2. Initiates an HTTP GET request to the API endpoint.
     * 3. Checks the HTTP response code:
     *    - If 200 (OK), reads the response data and parses it using the Gson library.
     *    - If not 200, logs an error.
     * 4. Parses the JSON response to extract the latitude and longitude.
     * 5. Returns a new {@link City} object with the extracted data or placeholder values if any error occurs.
     *
     * Error Handling:
     * - In case of network issues or an invalid response, the function catches an {@link IOException}
     *   and logs the error.
     *
     * Note:
     * - The Google Maps API key is hardcoded in the URL for ease of use by TA/Professor.
     * - API key will be disabled after posting of grade for the project.
     * - API requests are set with a timeout of 10 seconds for connection and reading.
     */
    @Override
    protected City doInBackground(String... strings) {
        try {
            //Make a GET request
            String urlString = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyD_WTcRw-orxo_kRR-p0BbrWSsP2Zemorc", strings[0]);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept", "application/json");

            //Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //If the response code is 200 (OK), read the input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                //Parse the JSON response using Gson
                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                //Example: Accessing a field from the response
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
