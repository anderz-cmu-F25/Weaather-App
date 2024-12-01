package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

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
public class LogoutTest {

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

        // Step 2: Logout
        onView(withId(R.id.buttonLogout)).perform(click());

        // Assert: Verify that LoginActivity is displayed again
        onView(withId(R.id.loginLayout)).check(matches(isDisplayed()));
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
