package main.ui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import com.visus.R;
import com.visus.main.MainActivity;
import com.visus.main.NewSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by jonathanperry on 25/06/2017.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class NewSessionUI extends ActivityInstrumentationTestCase2<NewSession> {

    @Rule
    public ActivityTestRule<NewSession> newSessionActivityTestRule = new ActivityTestRule(NewSession.class);

    private NewSession newSessionActivity = null;

    @Override
    public void setUp() throws Exception {
        Intent intent = new Intent();
        intent.putExtra("ActiveUserId", 1);
        newSessionActivityTestRule.launchActivity(intent);
        newSessionActivity = newSessionActivityTestRule.getActivity();
    }

    public NewSessionUI() {
        super(NewSession.class);
    }

    @Test
    public void clickOk() {
        onView(withId(R.id.alert_dialog_new_session_btn_ok)).perform(click());
    }

    @Test
    public void clickCancel() {
        onView(withId(R.id.alert_dialog_new_session_btn_cancel)).perform(click());
    }

    @Test
    public void clickStartAndStop() {
        onView(withId(R.id.alert_dialog_new_session_btn_cancel)).perform(click());
        onView(withId(R.id.timer_btn)).perform(click());
        onView(withId(R.id.timer_stop_btn)).perform(click());
    }

}