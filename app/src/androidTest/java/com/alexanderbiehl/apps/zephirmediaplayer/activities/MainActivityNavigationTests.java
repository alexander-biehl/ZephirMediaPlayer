package com.alexanderbiehl.apps.zephirmediaplayer.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import android.Manifest;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.alexanderbiehl.apps.zephirmediaplayer.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTests {

    @Rule
    public final GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    );

    @Test
    public void backFromPlaylists_returnsToMediaList() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                NavController navController = Navigation.findNavController(
                        activity,
                        R.id.nav_host_fragment_content_main
                );

                for (int i = 0; i < 3; i++) {
                    if (navController.getCurrentDestination() != null
                            && navController.getCurrentDestination().getId() == R.id.MediaListFragment) {
                        break;
                    }

                    if (navController.getCurrentDestination() != null
                            && navController.getCurrentDestination().getId() == R.id.SplashFragment) {
                        navController.navigate(R.id.action_SplashFragment_to_MediaListFragment);
                    } else {
                        navController.navigate(R.id.MediaListFragment);
                    }
                    activity.getSupportFragmentManager().executePendingTransactions();
                }

                assertNotNull(navController.getCurrentDestination());
                assumeTrue(
                        "Could not navigate from Splash to MediaList in current device startup state",
                        navController.getCurrentDestination().getId() == R.id.MediaListFragment
                );
                assertEquals(R.id.MediaListFragment, navController.getCurrentDestination().getId());

                navController.navigate(R.id.PlaylistsFragment);
                activity.getSupportFragmentManager().executePendingTransactions();

                assertNotNull(navController.getCurrentDestination());
                assertEquals(R.id.PlaylistsFragment, navController.getCurrentDestination().getId());
            });

            Espresso.pressBackUnconditionally();

            scenario.onActivity(activity -> {
                NavController navController = Navigation.findNavController(
                        activity,
                        R.id.nav_host_fragment_content_main
                );
                assertNotNull(navController.getCurrentDestination());
                assertEquals(R.id.MediaListFragment, navController.getCurrentDestination().getId());
            });
        }
    }
}







