package com.alexanderbiehl.apps.zephirmediaplayer.ui.fragments;

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

import androidx.fragment.app.testing.FragmentScenario;
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

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addsPlayableMediaItemToQueue() {
        MediaItem playableItem = TestUtils.createSongMediaItem();

        FragmentScenario<MediaListFragment> scenario = FragmentScenario.launchInContainer(MediaListFragment.class, new Bundle(), R.style.Theme_AppCompat);
        scenario.onFragment(fragment -> {
            fragment.mediaBrowser = mock(MediaBrowserWrapper.class);
            fragment.mediaViewModel = mock(MediaViewModel.class);

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
