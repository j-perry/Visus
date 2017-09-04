package main.ui;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import com.visus.R;
import com.visus.main.MainActivity;
import com.visus.main.Sessions;
import com.visus.main.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by jonathanperry on 25/06/2017.
 */

@RunWith(AndroidJUnit4.class)
@MediumTest
public class SessionsUI extends ActivityInstrumentationTestCase2<Sessions> {

    @Rule
    public ActivityTestRule<Sessions> sessionsActivityTestRule = new ActivityTestRule(Sessions.class);

    private Sessions sessionsActivity = null;
    private ViewPager sessionsPager;

    public SessionsUI() {
        super(Sessions.class);
    }

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra("ActiveUserId", 1);
        intent.putExtra("DisplayNotification", true);
        sessionsActivityTestRule.launchActivity(intent);
        sessionsActivity = sessionsActivityTestRule.getActivity();
        sessionsPager = (ViewPager) sessionsActivity.findViewById(R.id.sessions_pager);
    }

    @Test
    public void clickMenuNewSession() {
        onView(allOf(withId(R.id.alert_dialog_previous_sessions_btn_cancel))).perform(click());
        onView(allOf(withId(R.id.new_session_menu))).perform(click());
    }

    @Test
    public void clickOptionsMenuSettings() {
        onView(allOf(withId(R.id.alert_dialog_previous_sessions_btn_cancel))).perform(click());
        openContextualActionModeOverflowMenu();
        onView(allOf(withText("Settings"))).perform(click());
    }

    @Test
    public void clickGoBack() {
        onView(allOf(withId(R.id.alert_dialog_previous_sessions_btn_cancel))).perform(click());
        onView(withId(android.R.id.home)).perform(click());
    }

    @Test
    public void isTodayVisible() {
        onView(allOf(withId(R.layout.fragment_sessions_today), isDisplayed()));
    }

    @Test
    public void isThisWeekVisible() {
        onView(allOf(withId(R.layout.fragment_sessions_this_week), isDisplayed()));
    }

    @Test
    public void isThisMonthVisible() {
        onView(allOf(withId(R.layout.fragment_sessions_this_month), isDisplayed()));
    }

    @Test
    public void isThisYearVisible() {
        onView(allOf(withId(R.layout.fragment_sessions_this_year), isDisplayed()));
    }

}