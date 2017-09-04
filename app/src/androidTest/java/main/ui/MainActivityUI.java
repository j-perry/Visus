package main.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.visus.R;
import com.visus.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;

/**
 * Created by jonathanperry on 25/06/2017.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class MainActivityUI extends ActivityInstrumentationTestCase2<MainActivity> {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule(MainActivity.class);

    private MainActivity mainActivity = null;
    private ViewPager mainMenuPager;

    public MainActivityUI() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityRule.getActivity();
        mainMenuPager = (ViewPager) mainActivity.findViewById(com.visus.R.id.main_menu_pager);
    }

    @Test
    public void testPagerIsNotNull() {
        assertNotNull(mainMenuPager);
    }

    @Test
    public void testUserTargetsIsVisible() throws Exception {
        onView(withId(R.id.alert_dialog_user_targets_btn_ok)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserTargetClickCancel() throws Exception {
        onView(withId(R.id.alert_dialog_user_targets_btn_cancel)).perform(click());
    }

    @Test
    public void testUserTargetClickOk() throws Exception {
        onView(withId(R.id.alert_dialog_user_targets_btn_ok)).perform(click());
    }

    @Test
    public void testLatestActivitiesIsVisible() throws Exception {
        onView(allOf(withId(R.layout.fragment_main_menu_latest_activity), isDisplayed())); //.check(matches(isDisplayed()));
    }

    @Test
    public void testActivitiesIsVisible() throws Exception {
        //onView(withId(R.id.main_menu_pager)).perform(swipeLeft());
        onView(allOf(withId(R.layout.fragment_main_menu_activities), isDisplayed()));
    }

    @Test
    public void testClickNewSession() throws Exception {
        onView(withId(R.id.alert_dialog_user_targets_btn_cancel)).perform(click());
        onView(withId(R.id.new_session_menu)).perform(click());
    }

    @Test
    public void testSessionsClicked() throws Exception {
        onView(withId(R.id.alert_dialog_user_targets_btn_cancel)).perform(click());
        openContextualActionModeOverflowMenu();
        onView(withText("Sessions")).perform(click());
    }

    @Test
    public void testSettingsClicked() throws Exception {
        onView(withId(R.id.alert_dialog_user_targets_btn_cancel)).perform(click());
        openContextualActionModeOverflowMenu();
        onView(withText("Settings")).perform(click());
    }

}