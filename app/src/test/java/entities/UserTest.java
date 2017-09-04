package entities;

//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.MediumTest;

import com.visus.entities.User;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by jonathanperry on 22/07/2017.
 */
//@RunWith(AndroidJUnit4.class)
//@MediumTest
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @After
    public void tearDown() {
        user = null;
    }

    @Test
    public void isUserActive() {
        user.setActive(1);
        assertTrue(user.getActive() == 1);
    }

    @Test
    public void isTargetDayInRangeMax() {
        user.setTargetDay(29);

        assertTrue(user.getTargetDay() == 31 ||
                user.getTargetDay() == 30 ||
                user.getTargetDay() == 29 ||
                user.getTargetDay() == 28);
    }

    @Test
    public void isTargetDayInRangeMin() {
        user.setTargetDay(3);
        assertTrue(user.getTargetDay() >= 1);
    }

    @Test
    public void isTargetMonthInRangeMin() {
        user.setTargetMonth(1);
        assertTrue(user.getTargetMonth() >= 1);
    }

    @Test
    public void isTargetMonthInRangeMax() {
        user.setTargetMonth(9);
        assertTrue(user.getTargetMonth() <= 12);
    }

}