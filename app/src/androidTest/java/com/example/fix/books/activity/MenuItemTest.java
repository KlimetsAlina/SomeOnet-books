package com.example.fix.books.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.fix.books.MainActivity_;
import com.example.fix.books.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class MenuItemTest extends ActivityInstrumentationTestCase2<MainActivity_> {

    MenuItemTest(){
        super(MainActivity_.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SmallTest
    public void testMenuItemClick() {
        // Click on an item from ActionBar
        onView(withId(R.id.action_chlg)).perform(click());

//        // Verify the correct item was clicked by checking the content of the status TextView
//        onView(withId(R.id.action_chlg)).check(matches(withText(R.string.changelog)));

    }
}
