package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.View;

import androidx.annotation.VisibleForTesting;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class CheckUserSignup {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Mock
    private AuthenticationService mockAuthService;

    private Context context;
    private View decorView;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MockitoAnnotations.openMocks(this);

        // Initialize activity with mocked AuthenticationService and get decorView
        activityRule.getScenario().onActivity(activity -> {
            activity.setAuthenticationService(mockAuthService);
            decorView = activity.getWindow().getDecorView();
        });
    }

    /**
     * Test successful registration with valid credentials
     */
    @Test
    public void testSuccessfulRegistration() throws InterruptedException {
        // Arrange
        String username = "newuser";
        String password = "password123";
        when(mockAuthService.register(username, password)).thenReturn(true);

        // Act
        onView(withId(R.id.userName))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(password), closeSoftKeyboard());

        Thread.sleep(1000); // Brief pause for visibility

        onView(withId(R.id.register)).perform(click());

        Thread.sleep(1000); // Wait for toast

        // Assert
        onView(withText("Registration successful!"))
                .inRoot(withDecorView(not(is(decorView))))
                .check(matches(isDisplayed()));
    }

    /**
     * Test registration failure when username already exists
     */
    @Test
    public void testRegistrationWithExistingUsername() throws InterruptedException {
        // Arrange
        String username = "existinguser";
        String password = "password123";
        when(mockAuthService.register(username, password)).thenReturn(false);

        // Act
        onView(withId(R.id.userName))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(password), closeSoftKeyboard());

        Thread.sleep(1000); // Brief pause for visibility

        onView(withId(R.id.register)).perform(click());

        Thread.sleep(1000); // Wait for toast

        // Assert
        onView(withText("Registration failed. User may already exist."))
                .inRoot(withDecorView(not(is(decorView))))
                .check(matches(isDisplayed()));
    }

    /**
     * Test registration with empty username field
     */
    @Test
    public void testRegistrationWithEmptyUsername() throws InterruptedException {
        // Act
        onView(withId(R.id.userName))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password123"), closeSoftKeyboard());

        Thread.sleep(1000); // Brief pause for visibility

        onView(withId(R.id.register)).perform(click());

        Thread.sleep(1000); // Wait for toast

        // Assert
        onView(withText("Please enter both username and password"))
                .inRoot(withDecorView(not(is(decorView))))
                .check(matches(isDisplayed()));
    }

    /**
     * Test registration with empty password field
     */
    @Test
    public void testRegistrationWithEmptyPassword() throws InterruptedException {
        // Act
        onView(withId(R.id.userName))
                .perform(typeText("username"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(""), closeSoftKeyboard());

        Thread.sleep(1000); // Brief pause for visibility

        onView(withId(R.id.register)).perform(click());

        Thread.sleep(1000); // Wait for toast

        // Assert
        onView(withText("Please enter both username and password"))
                .inRoot(withDecorView(not(is(decorView))))
                .check(matches(isDisplayed()));
    }
}