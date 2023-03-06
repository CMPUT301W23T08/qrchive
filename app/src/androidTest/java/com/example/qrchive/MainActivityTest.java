package com.example.qrchive;

import static org.junit.Assert.assertEquals;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.content.Context;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.qrchive.Activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /** Test navigation from the Home fragment to the Codes Fragment
     */
    @Test
    public void testNavigationToCodeFragment() {
        // Assert Codes Fragment TextView Title Does not exist yet
        onView(withId(R.id.sorted_by_card)).check(doesNotExist());

        // Navigate to the codes fragment by clicking on Menu Item
        onView(withId(R.id.menu_item_codes)).perform(click());

        // Assert Codes Fragment Title TextView has appeared.
        onView(withId(R.id.sorted_by_card)).check(matches(isDisplayed()));
    }

    /** Basic Test to assert that the package loaded is qrchive.
    */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.qrchive", appContext.getPackageName());
    }
}
