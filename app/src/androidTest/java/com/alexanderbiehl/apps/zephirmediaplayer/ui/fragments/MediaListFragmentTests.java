package com.alexanderbiehl.apps.zephirmediaplayer.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.media3.common.MediaItem;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments.MediaListFragment;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers.MediaBrowserWrapper;
import com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers.MediaBrowserWrapperImpl;
import com.alexanderbiehl.apps.zephirmediaplayer.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MediaListFragmentTests {

//    @Mock
//    MediaBrowserWrapper mediaBrowser;
//    @Mock
//    MediaViewModel mediaViewModel;

//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    public void onCreateFragment_validateTitle() {
        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle(), R.style.Theme_AppCompat);
        // scenario.moveToState(Lifecycle.State.CREATED);
        onView(withId(R.id.fragment_media_item_list)).check(matches(withText("Root Folder")));
//        onView(withId(R.id.fragment_media_item_list)).check(matches(withText("Artists")));
//        onView(withId(R.id.fragment_media_item_list)).check(matches(withText("Playlists")));
    }

    @Test
    public void addsPlayableMediaItemToQueue() {
        MediaItem playableItem = TestUtils.createSongMediaItem();

        MediaBrowserWrapper mediaBrowser = mock(MediaBrowserWrapper.class);
        MediaViewModel mediaViewModel = mock(MediaViewModel.class);

        // FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle(), R.style.Theme_AppCompat);
        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launch(MediaListFragment.class,
                new Bundle(), new FragmentFactory() {

                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        if (className.equals(MediaListFragment.class.getName())) {
                            // Provide custom constructor or dependencies if needed
                            return new MediaListFragment(mediaViewModel, mediaBrowser);
                        }
                        return super.instantiate(classLoader, className);
                    }
                });
        scenario.onFragment(fragment -> {
//            fragment.mediaBrowser = mock(MediaBrowserWrapper.class);
//            fragment.mediaViewModel = mock(MediaViewModel.class);

            fragment.addMediaItemToQueue(playableItem);

            verify(fragment.mediaBrowser).addMediaItem(anyInt(), eq(playableItem));
            verify(fragment.mediaViewModel).addToQueue(eq(playableItem));
        });
    }

    @Test
    public void doesNotAddNonPlayableMediaItemToQueue() {
        MediaItem nonPlayableItem = MediaItem.fromUri("uri2");
        when(nonPlayableItem.mediaMetadata.isPlayable).thenReturn(false);

        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle());
        scenario.onFragment(fragment -> {
            fragment.mediaBrowser = mock(MediaBrowserWrapperImpl.class);
            fragment.mediaViewModel = mock(MediaViewModel.class);

            fragment.addMediaItemToQueue(nonPlayableItem);

            verify(fragment.mediaBrowser, never()).addMediaItem(anyInt(), any());
            verify(fragment.mediaViewModel, never()).addToQueue(any(MediaItem.class));
        });
    }

    @Test
    public void navigatesToNowPlayingOnFabClick() {
        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle());
        scenario.onFragment(fragment -> {
            NavController mockNavController = mock(NavController.class);
            Navigation.setViewNavController(fragment.requireView(), mockNavController);

            fragment.fab.performClick();

            verify(mockNavController).navigate(R.id.action_FirstFragment_to_SecondFragment);
        });
    }

    @Test
    public void opensSubFolderWhenMediaItemIsBrowsable() {
        MediaItem browsableItem = MediaItem.fromUri("uri3");
        when(browsableItem.mediaMetadata.isBrowsable).thenReturn(true);

        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle());
        scenario.onFragment(fragment -> {
            fragment.mediaBrowser = mock(MediaBrowserWrapperImpl.class);

            fragment.pushPathStack(browsableItem);

            verify(fragment.mediaBrowser).getChildren(eq(browsableItem.mediaId), anyInt(), anyInt(), isNull());
        });
    }

    @Test
    public void popsPathStackAndFinishesActivityWhenStackIsEmpty() {
        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle());
        scenario.onFragment(fragment -> {
            fragment.treeBackStack.clear();
            Activity mockActivity = mock(Activity.class);
            doReturn(mockActivity).when(fragment).requireActivity();

            fragment.popPathStack();

            verify(mockActivity).finish();
        });
    }
}
