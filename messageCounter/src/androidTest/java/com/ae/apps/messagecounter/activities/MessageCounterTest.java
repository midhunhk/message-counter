
package com.ae.apps.messagecounter.activities;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ae.apps.messagecounter.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

/**
 * Android Unit Test
 */
@RunWith(AndroidJUnit4.class)
public class MessageCounterTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void loadCounter(){
        onView(withId(R.id.menu_counter))
                .perform(click());

        /*onView(withId(R.id.hero_card01))
                .check(matches(isDisplayed()));*/
    }

    @Test
    public void loadIgnoredContacts(){
        onView(withId(R.id.menu_ignore))
                .perform(click());

        onView(withId(R.id.btnShowIgnoreDialog))
                .check(matches(isDisplayed()));
    }
}
