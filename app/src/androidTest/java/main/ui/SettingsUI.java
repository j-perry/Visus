package main.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.visus.R;
import com.visus.main.MainActivity;
import com.visus.main.Sessions;
import com.visus.main.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by jonathanperry on 25/06/2017.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class SettingsUI extends ActivityInstrumentationTestCase2<Settings> {

    @Rule
    public ActivityTestRule<Settings> settingsActivityTestRule = new ActivityTestRule(Settings.class);

    private Settings settingsActivity = null;
    private ViewPager settingsMenuPager;

    public SettingsUI() {
        super(Settings.class);
    }

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra("ActiveUserId", 1);
        settingsActivityTestRule.launchActivity(intent);
        settingsActivity = settingsActivityTestRule.getActivity();
        settingsMenuPager = (ViewPager) settingsActivity.findViewById(R.id.settings_pager);
    }

    @Test
    public void clickGoBack() {
        onView(withId(android.R.id.home)).perform(click());
    }

    @Test
    public void isGeneralVisible() {
        onView(allOf(withId(R.layout.fragment_settings_general), isDisplayed()));
    }

    @Test
    public void isAboutVisible() {
        onView(allOf(withId(R.layout.fragment_settings_about), isDisplayed()));
    }

    @Test
    public void isDailyTargetEditTextVisible() {
        onView(allOf(withId(R.id.settings_history_target_day), isDisplayed()));
    }

    @Test
    public void isMonthlyTargetEditTextVisible() {
        onView(withId(R.id.settings_history_target_month)).check(matches(isDisplayed()));
    }

    @Test
    public void clickSave() {
        onView(withId(R.id.settings_save_all)).perform(click());
    }

}
