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

}
