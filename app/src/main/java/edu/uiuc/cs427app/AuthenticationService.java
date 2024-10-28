package edu.uiuc.cs427app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationService {

    private static final String ACCOUNT_TYPE = "edu.uiuc.cs427app";
    private static final String PREFS_NAME = "UserPreferences";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";

    private AccountManager accountManager;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    // initialize for AuthenticationService
    public AuthenticationService(Context context) {
        this.accountManager = AccountManager.get(context);
        this.dbHelper = new DatabaseHelper(context);
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Register a new user
    public boolean register(String username, String password) {
        Account account = new Account(username, ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(account, password, null)) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            dbHelper.getWritableDatabase().insert("users", null, values);
            return true;
        }
        return false;
    }

    // Login an existing user
    public boolean login(String username, String password) {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        for (Account account : accounts) {
            if (account.name.equals(username)) {
                String storedPassword = accountManager.getPassword(account);
                return storedPassword != null && storedPassword.equals(password);
            }
        }
        return false;
    }

    // Fetch user data by username
    public User getUser(String username) {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                "users",
                new String[]{"username", "password"},
                "username = ?",
                new String[]{username},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndex("password"));
            User user = new User(username, password);
            user.getCityList().addAll(getCitiesForUser(username));
            cursor.close();
            return user;
        }
        return null;
    }

    // Save theme preferences for the current user
    public void saveUserTheme(String username, String buttonColor, String backgroundColor) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username + "_" + BUTTON_COLOR_KEY, buttonColor);
        editor.putString(username + "_" + BACKGROUND_COLOR_KEY, backgroundColor);
        editor.apply();
    }

    // Load theme preferences for the current user
    public String[] loadUserTheme(String username) {
        String buttonColor = sharedPreferences.getString(username + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = sharedPreferences.getString(username + "_" + BACKGROUND_COLOR_KEY, "Default");
        return new String[]{buttonColor, backgroundColor};
    }

    // Add a city for a user
    public void addCity(String username, City city) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        /* need to implement city member functions
        values.put("city_name", city.getName());
        values.put("latitude", city.getLatitude());
        values.put("longitude", city.getLongitude());
         */
        dbHelper.getWritableDatabase().insert("cities", null, values);
    }

    // Remove a city for a user
    public void removeCity(String username, String cityName) {
        dbHelper.getWritableDatabase().delete("cities", "username = ? AND city_name = ?", new String[]{username, cityName});
    }

    // Fetch all cities for a user
    private List<City> getCitiesForUser(String username) {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().query(
                "cities",
                new String[]{"city_name", "latitude", "longitude"},
                "username = ?",
                new String[]{username},
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                // add one city to city list
                //cityList.add(new City(cityName, latitude, longitude));
            }
            cursor.close();
        }
        return cityList;
    }

    // Inner Database Helper class
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "cs427app.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT);");
            db.execSQL("CREATE TABLE cities (username TEXT, city_name TEXT, latitude REAL, longitude REAL, PRIMARY KEY (username, city_name));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS cities");
            onCreate(db);
        }
    }
}
