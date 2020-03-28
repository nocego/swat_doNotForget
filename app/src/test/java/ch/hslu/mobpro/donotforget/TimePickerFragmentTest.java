package ch.hslu.mobpro.donotforget;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import static org.junit.Assert.*;

public class TimePickerFragmentTest {
    @Test
    public void getHourWithoutArguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //arrange
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Method getHour = TimePickerFragment.class.getDeclaredMethod("getHour");
        getHour.setAccessible(true);

        //act
        int hourFromMethod = (int) getHour.invoke(timePickerFragment);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        //assert
        assertEquals(currentHour, hourFromMethod);
    }

    @Test
    public void getMinuteWithoutArguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //arrange
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Method getMinute = TimePickerFragment.class.getDeclaredMethod("getMinute");
        getMinute.setAccessible(true);

        //act
        int minuteFromMethod = (int) getMinute.invoke(timePickerFragment);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);

        //assert
        assertEquals(currentMinute, minuteFromMethod);
    }

}