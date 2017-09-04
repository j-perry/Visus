package entities;

//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.MediumTest;

import com.visus.entities.Week;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jonathanperry on 22/07/2017.
 */
//@RunWith(AndroidJUnit4.class)
//@MediumTest
public class WeekTest {

    private Week week;

    @Before
    public void setUp() {
        week = new Week();
    }

    @After
    public void tearDown() {
        week = null;
    }

    @Test
    public void monthIsInBounds() {
        week.setMonth(6);
        assertTrue(week.getMonth() >= 1 && week.getMonth() <= 12);
    }

    @Test
    public void yearIsNotPrevious() {
        assertNotEquals("2016", week.getYear());
    }

    @Test
    public void isDateBeginningOfPresentWeek() {
        assertEquals("2017-08-12", week.beginning());
    }

    @Test
    public void isDateEndingOfPresentWeek() {
        assertEquals("2017-08-18", week.ending());
    }

    @Test
    public void isWeekStartDatesValid() {
        assertEquals("2017-08-12 to 2017-08-18", week.date().beginning() + " to " + week.date().ending());
    }

}