package com.ae.apps.messagecounter;

import android.os.Build;
import android.test.suitebuilder.annotation.LargeTest;

import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP_MR1,
packageName = "com.ae.apps.messagecounter")
public class CounterDataBaseTest {

    private CounterDataBaseAdapter dbHelper;

    @Before
    public void setup(){
        dbHelper = CounterDataBaseAdapter.getInstance(RuntimeEnvironment.application);
    }

    @After
    public void tearDown(){
        resetSingleton(CounterDataBaseAdapter.class, "sInstance");
    }

    private void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testShouldAddMessageToIndex(){
        long result = dbHelper.addMessageSentCounter(20181107, 1);
        long expected = 1;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldNotAddMesssageToIndex(){
        long result = dbHelper.addMessageSentCounter(20181107, 0);
        long expected = -1;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldGetMessageCountForIndex(){
        dbHelper.addMessageSentCounter(20181107, 1);
        long result = dbHelper.getCountValueForDay(20181107);
        long expected = 1;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldGetMultipleMessageCountForIndex_singleAdd(){
        dbHelper.addMessageSentCounter(20181107, 4);
        long result = dbHelper.getCountValueForDay(20181107);
        long expected = 4;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldGetMultipleMessageCountForIndex_multipleAdds(){
        dbHelper.addMessageSentCounter(20181107, 1);
        dbHelper.addMessageSentCounter(20181107, 1);
        dbHelper.addMessageSentCounter(20181107, 1);
        dbHelper.addMessageSentCounter(20181107, 1);
        long result = dbHelper.getCountValueForDay(20181107);
        long expected = 4;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldAddToIgnoreTable(){
        long result = dbHelper.addNumberToIgnore("test name", "9999999999");
        long expected = 1;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldCheckNotIgnoredNumber(){
        boolean result = dbHelper.checkIgnoredNumber("999999999");
        boolean expected = false;
        assertEquals(expected, result);
    }

    @Test
    public void testShouldCheckIgnoredNumber(){
        dbHelper.addNumberToIgnore("test name", "999999999");
        boolean result = dbHelper.checkIgnoredNumber("999999999");
        boolean expected = true;
        assertEquals(expected, result);
    }
}
