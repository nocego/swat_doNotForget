package ch.hslu.mobpro.donotforget;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import static org.junit.Assert.*;

public class DatePickerFragmentTest {
    @Test
    public void getYearWithoutArguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //arrange
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Method getYear = DatePickerFragment.class.getDeclaredMethod("getYear");
        getYear.setAccessible(true);

        //act
        int yearFromMethod = (int) getYear.invoke(datePickerFragment);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        //assert
        assertEquals(currentYear, yearFromMethod);
    }

    @Test
    public void getMonthWithoutArguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //arrange
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Method getMonth = DatePickerFragment.class.getDeclaredMethod("getMonth");
        getMonth.setAccessible(true);

        //act
        int monthFromMethod = (int) getMonth.invoke(datePickerFragment);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        //assert
        assertEquals(currentMonth, monthFromMethod);
    }

    @Test
    public void getDayWithoutArguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //arrange
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Method getDay = DatePickerFragment.class.getDeclaredMethod("getDay");
        getDay.setAccessible(true);

        //act
        int dayFromMethod = (int) getDay.invoke(datePickerFragment);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        //assert
        assertEquals(currentDay, dayFromMethod);
    }
}