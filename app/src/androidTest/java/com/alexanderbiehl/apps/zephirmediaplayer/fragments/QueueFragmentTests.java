package com.alexanderbiehl.apps.zephirmediaplayer.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments.QueueFragment;

import org.junit.Test;

public class QueueFragmentTests {

//    @Rule
//    public ActivityScenarioRule<MainActivity> activityScenarioRule =
//            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Test
//    public void testQueueFragmentWithActivity() {
//        activityScenarioRule.getScenario().onActivity(activity -> {
//            FragmentScenario<QueueFragment> fragmentScenario = FragmentScenario.launchInContainer(
//                    QueueFragment.class,
//                    new Bundle(),
//                    R.style.Theme_AppCompat // Use the appropriate theme
//            );
//            onView(withId(R.id.queue_list)).check(matches(isDisplayed()));
//        });
//    }


    // TODO thoughts: This might be due to the fact that the layout file just has a
    // recyclerView as the root element with nothing else. Perhaps without any data
    // the view is not rendered. Maybe try wrapping in another element
    @Test
    public void testQueueFragmentComponents() {
        // Launch the QueueFragment in a container
        FragmentScenario<QueueFragment> fragmentScenario = FragmentScenario.launchInContainer(
                QueueFragment.class,
                new Bundle(),
                R.style.Theme_AppCompat // Use the appropriate theme
        );

        // Check if the queue list is displayed
        onView(withId(R.id.queue_list)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testQueueFragmentComponents() {
//        // Create a custom factory that passes required dependencies to the fragment
//        FragmentFactory fragmentFactory = new FragmentFactory() {
//            @Override
//            public Fragment instantiate(ClassLoader classLoader, String className) {
//                if (className.equals(QueueFragment.class.getName())) {
//                    QueueFragment fragment = new QueueFragment();
//                    // Initialize fragment with any dependencies it needs
//                    // that it would normally get from the activity
//                    return fragment;
//                }
//                return super.instantiate(classLoader, className);
//            }
//        };
//
//        FragmentScenario<QueueFragment> fragmentScenario = FragmentScenario.launchInContainer(
//                QueueFragment.class,
//                new Bundle(),
//                R.style.Theme_AppCompat,
//                fragmentFactory
//        );
//
//        onView(withId(R.id.queue_list)).check(matches(isDisplayed()));
//    }

//    @Test
//    public void testQueueFragmentComponents() {
//        // Launch the activity from a background thread
//        ActivityScenario<MainActivity> scenario = androidx.test.core.app.ApplicationProvider
//                .getApplicationContext()
//                .getMainExecutor()
//                .executeBlocking(() -> ActivityScenario.launch(MainActivity.class));
//
//        scenario.onActivity(activity -> {
//            FragmentScenario<QueueFragment> fragmentScenario = FragmentScenario.launchInContainer(
//                    QueueFragment.class,
//                    new Bundle(),
//                    R.style.Theme_AppCompat // Use the appropriate theme
//            );
//            onView(withId(R.id.queue_list)).check(matches(isDisplayed()));
//        });
//
//        scenario.close();
//    }

//    @Test
//    public void testQueueFragmentComponents() {
//        // Use ActivityScenario with a real AppCompatActivity
//        ActivityScenario<TestActivity> activityScenario = ActivityScenario.launch(TestActivity.class);
//
//        activityScenario.onActivity(activity -> {
//            // Manually add your fragment to the activity
//            QueueFragment fragment = new QueueFragment();
//            activity.getSupportFragmentManager().beginTransaction()
//                    .add(android.R.id.content, fragment)
//                    .commitNow();
//
//            // Now test the fragment
//            onView(withId(R.id.queue_list)).check(matches(isDisplayed()));
//        });
//    }
//
//    // Define a basic test activity that extends AppCompatActivity
//    public static class TestActivity extends AppCompatActivity {
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(new FrameLayout(this));
//        }
//    }
}
