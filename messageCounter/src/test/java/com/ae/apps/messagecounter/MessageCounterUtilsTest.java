package com.ae.apps.messagecounter;

import com.ae.apps.messagecounter.utils.MessageCounterUtils;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by user on 6/10/2017.
 */

public class MessageCounterUtilsTest {

    @Test
    public void testGetIndexFromDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.DATE, 1);
        long expected = 170101l;

        long actual = MessageCounterUtils.getIndexFromDate(calendar.getTime());

        assertEquals(expected, actual);
    }
}
