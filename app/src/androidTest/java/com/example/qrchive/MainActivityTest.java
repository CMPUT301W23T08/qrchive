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

    /** Test navigation to the Home fragment
     */
    @Test
    public void testNavigationToHomeFragment() {
        // Assert Codes Fragment TextView Title Does not exist yet
        onView(withId(R.id.sorted_by_card)).check(doesNotExist());

        // Navigate to the codes fragment by clicking on Menu Item
        onView(withId(R.id.menu_item_home)).perform(click());

        // Assert Codes Fragment Title TextView has appeared.
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
    }

    /** Test navigation to the Home fragment
     */
    @Test
    public void testNavigationToMapsActivity() {
        // Assert Codes Fragment TextView Title Does not exist yet
        onView(withId(R.id.sorted_by_card)).check(doesNotExist());

        // Open the dropdown menu by clicking on the triple bar menu icon
        onView(withId(R.id.menu_item_dropdown)).perform(click());

        // Assert Codes Maps Icon is displayed.
        onView(withId(R.id.menu_dropdown_map)).check(matches(isDisplayed()));

        // Open the maps activity by clicking on the map icon
        onView(withId(R.id.menu_dropdown_map)).perform(click());
    }

    /** Test navigation to the Settings Fragment
     */
    @Test
    public void testNavigationToSettingsFragment() {
        // Assert Codes Fragment TextView Title Does not exist yet
        onView(withId(R.id.fragment_settings_title)).check(doesNotExist());

        // Open the dropdown menu by clicking on the triple bar menu icon
        onView(withId(R.id.menu_item_dropdown)).perform(click());

        // Assert Codes Maps Icon is displayed.
        onView(withId(R.id.menu_dropdown_settings)).check(matches(isDisplayed()));

        // Open the maps activity by clicking on the map icon
        onView(withId(R.id.menu_dropdown_settings)).perform(click());

        // Assert the settings title appears
        onView(withId(R.id.fragment_settings_title)).check(matches(isDisplayed()));
    }

    /** Test navigation to the  Fragment
     */
    @Test
    public void testNavigationToScanFragment() {
        // Assert The reset button has not appear
        onView(withId(R.id.fragment_scan_reset_button)).check(doesNotExist());

        // Open the scan fragment by clicking on the scan icon
        onView(withId(R.id.menu_item_scan)).perform(click());
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
