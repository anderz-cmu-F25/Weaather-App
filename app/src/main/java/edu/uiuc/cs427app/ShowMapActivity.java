package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ShowMapActivity extends AppCompatActivity implements View.OnClickListener {
    // Define SharedPreferences constants for saving user settings
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";

    private String currentUsername; // Add this field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get username from intent
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            // Fallback to shared preferences if not in intent
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            currentUsername = prefs.getString("lastLoggedInUser", "default");
        }

        // Load user-specific UI settings
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        // Reference the details ConstraintLayout to apply user customizations
        ConstraintLayout wxLayout = findViewById(R.id.mapLayout);

        // Apply the saved background color to the layout
        MainActivity.applyBackgroundColor(backgroundColor, wxLayout);
        MainActivity.applyButtonColors(this, buttonColor);

        String cityName = getIntent().getStringExtra("city");
        String currentUsername = getIntent().getStringExtra("username");

        ArrayList<City> cityList = new ArrayList<>();
        Gson gson = new Gson();
        String json = preferences.getString(currentUsername + "_cities", "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<City>>(){}.getType();
            cityList = gson.fromJson(json, type);
        }

        List<City> foundCityList = cityList.stream().filter(city -> city.getName().equals(cityName)).collect(Collectors.toList());;
        City city = foundCityList.isEmpty() ? new City("Invalid", Double.MIN_VALUE, Double.MIN_VALUE) : foundCityList.get(0);

        TextView cityNameView = findViewById(R.id.cityName);
        TextView latLongView = findViewById(R.id.latLong);

        cityNameView.setText(city.getName());
        double latitude = city.getLatitude();
        double longitude = city.getLongitude();
        String latLongText = "Latitude: " + latitude + ", Longitude: " + longitude;
        latLongView.setText(latLongText);

        // Load the interactive map in the WebView
        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Generate the map URL using the city's name
        String mapUrl = null;
        try {
            mapUrl = String.format("https://www.google.com/maps/embed/v1/place?key=AIzaSyD_WTcRw-orxo_kRR-p0BbrWSsP2Zemorc&q=%s", URLEncoder.encode(cityName, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String htmlContent = String.format("<html><body><iframe src=%s width=100%% height=100%%></iframe></body></html>", mapUrl);
        // Load the map URL into the WebView
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    private String getApiKey() {
        try {
            // Get the ApplicationInfo object
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            // Retrieve the metadata
            Bundle metaData = appInfo.metaData;
            if (metaData != null) {
                return metaData.getString("com.google.android.geo.API_KEY");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        Intent intent;
    }
}
