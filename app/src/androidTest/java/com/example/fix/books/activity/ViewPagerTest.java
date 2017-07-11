package com.example.fix.books.activity;


import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.fix.books.MainActivity_;
import com.example.fix.books.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class ViewPagerTest extends ActivityInstrumentationTestCase2<MainActivity_> {

    public ViewPagerTest() {
        super(MainActivity_.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SmallTest
    public void testSwipeRight() {
        onView(withId(R.id.container)).perform(swipeLeft());
    }
}
