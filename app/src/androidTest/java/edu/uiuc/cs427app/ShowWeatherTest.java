package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import androidx.test.espresso.contrib.RecyclerViewActions;
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
public class ShowWeatherTest {
    private AuthenticationService mockAuthService;

    private SharedPreferences sharedPrefs;
    private ContextWrapper contextWrapper;

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
    public void showNewYorkCityWeather() throws InterruptedException {
        onView(withText("New York")).perform(click());
        onView(withText("Show Weather")).perform(click());

        Thread.sleep(1000);

        // Check that the city name is correct
        onView(withId(R.id.cityName)).check(matches(withText("New York")));

        // Check that the weather details have actual values
        onView(withId(R.id.dateTime)).check(matches(not(withText("")))).check(matches(not(withText("Local Date & Time: "))));
        onView(withId(R.id.temperature)).check(matches(not(withText("")))).check(matches(not(withText("Temperature: "))));
        onView(withId(R.id.weatherCondition)).check(matches(not(withText("")))).check(matches(not(withText("Weather: "))));
        onView(withId(R.id.humidity)).check(matches(not(withText("")))).check(matches(not(withText("Humidity: "))));
        onView(withId(R.id.wind)).check(matches(not(withText("")))).check(matches(not(withText("Wind: "))));

        Thread.sleep(1000);
    }

    @Test
    public void showNewYorkCityWeatherInsight() throws InterruptedException {
        onView(withText("New York")).perform(click());
        onView(withText("Show Weather")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.wxAiButton)).perform(click());

        // Wait for LLM responses to load
        Thread.sleep(1000);

        // Check that at least one question is generated
        onView(withId(R.id.questionContainer)).check(matches(hasMinimumChildCount(1)));
        onView(allOf(isDescendantOfA(withId(R.id.questionContainer)), withParentIndex(0))).check(matches(not(withText("")))).perform(click());

        // Wait for the answer to load
        Thread.sleep(1000);

        // Check that the answer is displayed
        onView(withId(R.id.responseTextView)).check(matches(isDisplayed())).check(matches(not(withText(""))));

        Thread.sleep(2000);
    }

    @Test
    public void showChampaignWeather() throws InterruptedException {
        onView(withText("Champaign")).perform(click());
        onView(withText("Show Weather")).perform(click());

        Thread.sleep(1000);

        // Assert that the city name is correct
        onView(withId(R.id.cityName)).check(matches(withText("Champaign")));

        // Check that the weather details have actual values
        onView(withId(R.id.dateTime)).check(matches(not(withText("")))).check(matches(not(withText("Local Date & Time: "))));
        onView(withId(R.id.temperature)).check(matches(not(withText("")))).check(matches(not(withText("Temperature: "))));
        onView(withId(R.id.weatherCondition)).check(matches(not(withText("")))).check(matches(not(withText("Weather: "))));
        onView(withId(R.id.humidity)).check(matches(not(withText("")))).check(matches(not(withText("Humidity: "))));
        onView(withId(R.id.wind)).check(matches(not(withText("")))).check(matches(not(withText("Wind: "))));
        Thread.sleep(1000);
    }

    @Test
    public void showChampaignWeatherInsight() throws InterruptedException {
        onView(withText("Champaign")).perform(click());
        onView(withText("Show Weather")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.wxAiButton)).perform(click());

        // Wait for LLM responses to load
        Thread.sleep(1000);

        // Check that at least one question is generated
        onView(withId(R.id.questionContainer)).check(matches(hasMinimumChildCount(1)));
        onView(allOf(isDescendantOfA(withId(R.id.questionContainer)), withParentIndex(0))).check(matches(not(withText("")))).perform(click());

        // Wait for the answer to load
        Thread.sleep(1000);

        // Check that the answer is displayed
        onView(withId(R.id.responseTextView)).check(matches(isDisplayed())).check(matches(not(withText(""))));

        Thread.sleep(2000);
    }

    @Test
    public void showInvalidCityWeather() throws InterruptedException {
        onView(withText("Invalid City")).perform(click());
        onView(withText("Show Weather")).perform(click());

        Thread.sleep(1000);

        // Assert that the city name is empty
        onView(withId(R.id.cityName)).check(matches(withText("")));

        // Check that the weather details are empty
        onView(withId(R.id.dateTime)).check(matches(withText("")));
        onView(withId(R.id.temperature)).check(matches(withText("")));
        onView(withId(R.id.weatherCondition)).check(matches(withText("")));
        onView(withId(R.id.humidity)).check(matches(withText("")));
        onView(withId(R.id.wind)).check(matches(withText("")));
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
