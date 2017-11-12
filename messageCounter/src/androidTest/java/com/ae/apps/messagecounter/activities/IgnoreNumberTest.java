package com.ae.apps.messagecounter.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.ae.apps.messagecounter.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class IgnoreNumberTest {

    private static final String IGNORE_NAME = "Name";
    private static final String IGNORE_NUMBER = "123";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ignoreNumberTest() {
        ViewInteraction bottomNavigationItemView = onView(withId(R.id.menu_ignore));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatButton = onView(withId(R.id.btnShowIgnoreDialog));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withText("Add To Ignore List"),
                        childAtPosition(
                                allOf(withId(R.id.addTripDialogHeader),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Add To Ignore List")));

        ViewInteraction appCompatEditText = onView(withId(R.id.txtIgnoreNumber));
        appCompatEditText.perform(replaceText(IGNORE_NUMBER), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.txtIgnoreName));
        appCompatEditText2.perform(replaceText(IGNORE_NAME), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(withId(R.id.btnIgnoreContact));
        appCompatButton2.perform(click());

        ViewInteraction textView2 = onView(allOf(withId(R.id.ignoreItemNumber), withText(IGNORE_NUMBER)));
        textView2.check(matches(withText(IGNORE_NUMBER)));

        ViewInteraction textView3 = onView(allOf(withId(R.id.ignoreItemName), withText(IGNORE_NAME)));
        textView3.check(matches(withText(IGNORE_NAME)));
    }

    //@Test
    public void ignoreDeleteNumberTest() {
        ViewInteraction bottomNavigationItemView = onView(withId(R.id.menu_ignore));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatButton = onView(withId(R.id.btnShowIgnoreDialog));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(withId(R.id.txtIgnoreNumber));
        appCompatEditText.perform(replaceText(IGNORE_NUMBER), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.txtIgnoreName));
        appCompatEditText2.perform(replaceText(IGNORE_NAME), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(withId(R.id.btnIgnoreContact));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.ignoreItemName), withText(IGNORE_NUMBER),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText(IGNORE_NUMBER)));

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnRemoveIgnoredItem),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction textView2 = onView(withId(R.id.empty_view));
        textView2.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
