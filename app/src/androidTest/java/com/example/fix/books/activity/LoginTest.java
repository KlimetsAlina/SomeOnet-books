package com.example.fix.books.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.fix.books.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

public class LoginTest extends ActivityInstrumentationTestCase2<Login_>  {

    public LoginTest() {
        super(Login_.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SmallTest
    public void testLoginButtonBehavior() {
        onView(withText(R.string.login)).check(matches(not(isEnabled())));
        onView(withId(R.id.input_email)).perform(typeText("aaa"));
        onView(withId(R.id.input_password)).perform(typeText("123"));
//        onView(withText(R.string.login)).check(matches((isEnabled())));
    }
}