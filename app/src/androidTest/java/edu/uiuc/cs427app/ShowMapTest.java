package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ShowMapTest {
    private AuthenticationService mockAuthService;

    private SharedPreferences sharedPrefs;
    private ContextWrapper contextWrapper;

    private static final String LAT_LONG_DEFAULT_TEXT = "Latitude: 0.0, Longitude: 0.0";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        // Clear the shared preference
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences.Editor editor = appContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE).edit();
        editor.clear().commit();

        String username = "newuser";
        String password = "password123";
        City newYork = new City("New York", 40.7128, -74.0060);
        City champaign = new City("Champaign", 40.1164, -88.2434);
        City invalidCity = new City("Invalid City", 0, 0);
        List<City> cities = Arrays.asList(newYork, champaign, invalidCity);
        Gson gson = new Gson();

        // Add two predefined cities to the user list
        editor.putString(String.format("%s_cities", username), gson.toJson(cities));
        editor.apply();

        // Simulate successful login
        mockAuthService = Mockito.mock(AuthenticationService.class);
        Mockito.when(mockAuthService.login(username, password)).thenReturn(true);
        activityScenarioRule.getScenario().onActivity(this::injectMockAuthService);

        // Login
        onView(withId(R.id.userName))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
    }

    @Test
    public void showNewYorkCityMap() throws InterruptedException {
        onView(withText("New York")).perform(click());
        onView(withText("Show Map")).perform(click());
        Thread.sleep(1000);

        // Check that the city name is correct
        onView(withId(R.id.cityName)).check(matches(withText("New York")));

        // Check that the map details have actual values
        onView(withId(R.id.latLong)).check(matches(not(withText("")))).check(matches(not(withText(LAT_LONG_DEFAULT_TEXT))));
        Thread.sleep(1000);

        onView(withId(R.id.webView)).check(matches(isDisplayed())).check(matches(not(withText(""))));
//        onWebView().withElement(findElement(Locator.ID, "webView")).check(webMatches(getCurrentUrl(), containsString("New York")));
//        Thread.sleep(5000);
    }

    @Test
    public void showChampaignMap() throws InterruptedException {
        onView(withText("Champaign")).perform(click());
        onView(withText("Show Map")).perform(click());

        Thread.sleep(1000);

        // Assert that the city name is correct
        onView(withId(R.id.cityName)).check(matches(withText("Champaign")));

        // Check that the map details have actual values
        onView(withId(R.id.latLong)).check(matches(not(withText("")))).check(matches(not(withText(LAT_LONG_DEFAULT_TEXT))));
        Thread.sleep(1000);
    }


    @Test
    public void showInvalidCityMap() throws InterruptedException {
        onView(withText("Invalid City")).perform(click());
        onView(withText("Show Map")).perform(click());

        Thread.sleep(1000);

        // Assert that the city name is empty
        onView(withId(R.id.cityName)).check(matches(withText("Invalid City")));

        // Check that the map details are empty
        onView(withId(R.id.latLong)).check(matches(not(withText("")))).check(matches((withText(LAT_LONG_DEFAULT_TEXT))));
    }

    /**
     * Injects the mocked AuthenticationService into the LoginActivity using reflection.
     *
     * @param activity The instance of LoginActivity where the mock will be injected.
     */
    private void injectMockAuthService(LoginActivity activity) {
        try {
            // Access the private authService field in LoginActivity
            Field authServiceField = LoginActivity.class.getDeclaredField("authService");
            authServiceField.setAccessible(true); // Make the private field accessible
            authServiceField.set(activity, mockAuthService); // Replace the real service with the mock
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to inject mock AuthenticationService", e);
        }
    }
}
