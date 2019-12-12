package com.tomwo.app.baking;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tomwo.app.baking.ui.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 *  This test demos a user clicking an item in the MainListFragment which opens up the DetailActivity (& thus Fragment)
 */
@RunWith(AndroidJUnit4.class)
public class MainListTest
{
    private static final String TAG = "Brownies";  // Nutella Pie, Brownies, Yellow Cake, Cheesecake

    /**
     * ActivityTestRule is a rule provided by Android used for functional testing
     * of a single activity.
     * <p>
     * The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated
     * after the test and methods annotated with @After are complete
     *
     * this rule allows you to directly access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickListViewItem_OpenDetailActivity()
    {
        onView(withId(R.id.recipeList)).perform( RecyclerViewActions.actionOnItemAtPosition(1, click() ) );

        onView(withId(R.id.cardTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TAG)));
    }



}
