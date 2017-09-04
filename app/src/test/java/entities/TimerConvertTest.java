package entities;

//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.MediumTest;

import android.util.Log;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import com.visus.entities.TimerConvert;

/**
 * Created by jonathanperry on 22/07/2017.
 */
//@RunWith(AndroidJUnit4.class)
//@MediumTest
public class TimerConvertTest {

    private TimerConvert timerConvert;

    @Before
    public void setup() {
        timerConvert = new TimerConvert();
    }

    @After
    public void tearDown() {
        timerConvert = null;
    }

    @Test
    public void minsToMillisecsEqualsHour() {
        timerConvert.minutesToMilliseconds(60);
        assertEquals(3600000, timerConvert.getMillisecondsFromMinutes());
    }

    @Test
    public void minsToMillisecsIsPositive() {
        timerConvert.minutesToMilliseconds(60);
        assertTrue(timerConvert.getMillisecondsFromSeconds() > 0);
    }

    @Test
    public void minsToMillisecsIsWhole() {
        timerConvert.minutesToMilliseconds(60);
        assertTrue(timerConvert.getMillisecondsFromSeconds() % 1 == 0);
    }

    @Test
    public void secsToMillisecsEqualsMinute() {
        timerConvert.secondsToMilliseconds(60);
        assertEquals(60000, timerConvert.getMillisecondsFromSeconds());
    }

    @Test
    public void secsToMillisecsEqualsHour() {
        timerConvert.secondsToMilliseconds(3600);
        assertEquals(3600000, timerConvert.getMillisecondsFromSeconds());
    }

    @Test
    public void secsToMillisecsIsPositive() {
        timerConvert.secondsToMilliseconds(3600);
        assertTrue(timerConvert.getMillisecondsFromSeconds() > 0);
    }

    @Test
    public void secsToMillisecsIsWhole() {
        timerConvert.secondsToMilliseconds(3600);
        assertTrue(timerConvert.getMillisecondsFromSeconds() % 1 == 0);
    }

    @Test
    public void minsAndSecondsToMillisecsIsPositive() {
        assertEquals(119000, timerConvert.minutesAndSecondsToMilliseconds(1, 59));
    }

    @Test
    public void minsAndSecsIsWhole() {
        assertTrue(timerConvert.minutesAndSecondsToMilliseconds(1, 59) % 1 == 0);
    }

    @Test
    public void minsAccumToHoursAccumIsPositive() {
        assertTrue(timerConvert.minutesAccumulatedToHoursAccumulated(250) > 0);
    }

    @Test
    public void minsAccumToHoursAccumIsWhole() {
        assertTrue((int) (timerConvert.minutesAccumulatedToHoursAccumulated(250) % 1) == 0);
    }

}
