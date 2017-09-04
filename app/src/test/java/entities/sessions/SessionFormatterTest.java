package entities.sessions;

//import android.support.test.InstrumentationRegistry;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.SmallTest;

import com.visus.entities.sessions.Session;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by jonathanperry on 30/07/2017.
 */
//@RunWith(AndroidJUnit4.class)
//@SmallTest
public class SessionFormatterTest {

    private Session.SessionFormatter sessionFormatter;

    @Before
    public void setUp() {
        this.sessionFormatter = new Session.SessionFormatter();
    }

    @After
    public void tearDown() {
        this.sessionFormatter = null;
    }

    @Test
    public void formatSessionDurationMinsAndSecsPositive() {
        assertTrue(sessionFormatter.formatSessionDuration(23, 48) > 0.0);
    }

    @Test
    public void formatSessionDurationsParamsPositive() {
        assertTrue(sessionFormatter.formatSessionDurations(47.26, 12, 6) > 0.0);
    }

    @Test
    public void formatSessionDurationMinsAndSecsInRange() {
        assertTrue(sessionFormatter.formatSessionDuration(25, 0) > 0.0 &&
            sessionFormatter.formatSessionDuration(6, 37) > 0.0);
    }

}
