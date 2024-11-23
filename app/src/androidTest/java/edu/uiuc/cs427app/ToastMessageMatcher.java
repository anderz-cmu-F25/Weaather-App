package edu.uiuc.cs427app;

import android.view.WindowManager;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * A custom matcher for Toast messages.
 * Matches Toast views that are displayed outside the normal view hierarchy.
 */
@SuppressWarnings("deprecation")
public class ToastMessageMatcher extends TypeSafeMatcher<Root> {

    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override
    public boolean matchesSafely(Root root) {
        if (root == null) {
            return false;
        }
        int type = root.getWindowLayoutParams().get().type;
        // Check if the root window type is TYPE_TOAST
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            return root.getDecorView().getWindowToken() == root.getDecorView().getApplicationWindowToken();
        }
        return false;
    }
}
