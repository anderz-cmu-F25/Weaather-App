package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.lang.reflect.Field;

// Import the ToastMessageMatcher from its own file
import edu.uiuc.cs427app.ToastMessageMatcher;

@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentedTest {

    // Mock instance of the AuthenticationService to simulate behavior during tests
    private AuthenticationService mockAuthService;

    // Use ActivityScenarioRule instead of ActivityTestRule
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Setup method to initialize the mocked AuthenticationService and define its behavior.
     * This runs before each test case.
     */
    @Before
    public void setUp() {
        // Create a mock instance of AuthenticationService
        mockAuthService = Mockito.mock(AuthenticationService.class);

        // Define behavior for mock methods
        Mockito.when(mockAuthService.login("validUser", "validPassword")).thenReturn(true); // Simulate successful login
        Mockito.when(mockAuthService.login("invalidUser", "wrongPassword")).thenReturn(false); // Simulate login failure

        // Inject the mocked service into the LoginActivity
        activityScenarioRule.getScenario().onActivity(this::injectMockAuthService);
    }

    /**
     * Test case to verify the login functionality with valid credentials.
     * Expects a success toast message to appear.
     */
    @Test
    public void testLoginSuccess() {
        // Simulate user entering valid credentials and clicking the login button
        onView(withId(R.id.userName)).perform(replaceText("validUser"));
        onView(withId(R.id.password)).perform(replaceText("validPassword"));
        onView(withId(R.id.login)).perform(click());

        // Assert: Verify the success toast message
        onView(withText("Login successful!"))
                .inRoot(new ToastMessageMatcher()) // Match the toast message
                .check(matches(withText("Login successful!"))); // Verify the toast text
    }

    /**
     * Test case to verify the login functionality with invalid credentials.
     * Expects a failure toast message to appear.
     */
    @Test
    public void testLoginFailure() {
        // Simulate user entering invalid credentials and clicking the login button
        onView(withId(R.id.userName)).perform(replaceText("invalidUser"));
        onView(withId(R.id.password)).perform(replaceText("wrongPassword"));
        onView(withId(R.id.login)).perform(click());

        // Wait briefly to ensure the toast message appears
        try {
            Thread.sleep(1000); // Delay to allow the toast to be displayed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert: Verify the failure toast message
        onView(withText("Login failed. Check your credentials."))
                .inRoot(new ToastMessageMatcher()) // Match the toast message
                .check(matches(withText("Login failed. Check your credentials."))); // Verify the toast text
    }

    /**
     * Test case to verify the behavior when the username and password fields are empty.
     * Expects a validation toast message to appear.
     */
    @Test
    public void testEmptyFields() {
        // Simulate user clicking the login button without entering any credentials
        onView(withId(R.id.login)).perform(click());

        // Wait briefly to ensure the toast message appears
        try {
            Thread.sleep(1000); // Delay to allow the toast to be displayed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert: Verify the validation toast message
        onView(withText("Please enter both username and password"))
                .inRoot(new ToastMessageMatcher()) // Match the toast message
                .check(matches(withText("Please enter both username and password"))); // Verify the toast text
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
