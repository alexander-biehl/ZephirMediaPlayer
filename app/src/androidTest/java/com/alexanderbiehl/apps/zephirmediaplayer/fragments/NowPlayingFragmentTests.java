package com.alexanderbiehl.apps.zephirmediaplayer.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments.NowPlayingFragment;

import org.junit.Test;

public class NowPlayingFragmentTests {

    @Test
    public void testNowPlayingFragmentComponents() {
        FragmentScenario<NowPlayingFragment> scenario = FragmentScenario.launchInContainer(
                NowPlayingFragment.class,
                new Bundle(),
                R.style.Theme_AppCompat // Use the appropriate theme
        );

        onView(withId(R.id.go_to_queue_fab)).check(matches(isDisplayed()));
        onView(withId(R.id.button_second)).check(matches(isDisplayed()));
        onView(withId(R.id.player_view)).check(matches(isDisplayed()));
        onView(withId(R.id.controls_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.artist_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.album_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.song_text_view)).check(matches(isDisplayed()));
    }
}
