package entities.sessions;

//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.MediumTest;

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
//@MediumTest
public class SessionTest {

    private Session session;

    @Before
    public void setUp() {
        this.session = new Session();
    }

    @After
    public void tearDown() {
        this.session = new Session();
    }

    @Test
    public void isUserIdPositive() {
        session.setUserId(1);
        assertTrue(session.getUserId() > 0);
    }

    @Test
    public void isDurationMinsAndSecsPositive() {
        session.setDurationMinutes(7);
        session.setDurationSeconds(13);
        assertTrue(session.getDurationMinutes() > 0 && session.getDurationSeconds() > 0);
    }

    @Test
    public void isDurationMinsPositive() {
        session.setDurationMinutes(7);
        assertTrue(session.getDurationMinutes() > 0);
    }

    @Test
    public void isDurationSecsPositive() {
        session.setDurationSeconds(13);
        assertTrue(session.getDurationSeconds() > 0);
    }

    @Test
    public void isSetTimeMinsAndSecsPositive() {
        session.setDurationMinutes(23);
        session.setDurationSeconds(7);
        assertTrue(session.getDurationMinutes() + session.getDurationSeconds() > 0.0);
    }

    @Test
    public void isSetTimeHourPositive() {
        session.setTimeHour(3);
        assertTrue(session.getTimeHour() > 0);
    }

    @Test
    public void isSetTimeMinsPositive() {
        session.setTimeMinutes(16);
        assertTrue(session.getTimeMinutes() > 0);
    }

    @Test
    public void isDayNoInRange() {
        session.setDayNo(31);
        assertTrue(session.getDayNo() == 28 ||
                session.getDayNo() == 29 ||
                session.getDayNo() == 30 ||
                session.getDayNo() == 31);
    }

    @Test
    public void isSetDayValid() {
        session.setDay("Monday");
        assertEquals("Monday", session.getDay());
    }

    @Test
    public void isSetMonthValid() {
        session.setMonth("February");
        assertEquals("February", session.getMonth());
    }

    @Test
    public void isDateInValidFormat() {
        session.setDate("12-08-2017");
        assertEquals("12-08-2017", session.getDate());
    }

    @Test
    public void isSetYearValid() {
        session.setYear(2017);
        assertEquals(2017, session.getYear());
    }

    @Test
    public void isOverviewHoursPositive() {
        session.setOverviewHours(3);
        assertTrue(session.getOverviewHours() > 0);
    }

    @Test
    public void isSetOverviewNoActivitiesPositive() {
        session.setOverviewNoActivities(7);
        assertTrue(session.getOverviewNoActivities() > 0);
    }

    @Test
    public void isSetOverviewNoSessionsPositive() {
        session.setOverviewNoSessions(18);
        assertTrue(session.getOverviewNoSessions() > 0);
    }

}