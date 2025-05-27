package com.alexanderbiehl.apps.zephirmediaplayer.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments.MediaListFragment;

import org.junit.Test;

public class MediaListFragmentTests {

    @Test
    public void testMediaListFragmentComponents() {
        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(
                MediaListFragment.class,
                new Bundle(),
                R.style.Theme_AppCompat
        );

        onView(withId(R.id.list)).check(matches(isDisplayed()));
        onView(withId(R.id.toNowPlaying)).check(matches(isDisplayed()));
    }
}
