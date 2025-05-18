package com.alexanderbiehl.apps.zephirmediaplayer.ui.fragments;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters.MyQueueRecyclerViewAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments.QueueFragment;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class QueueFragmentTest {

    @Mock
    MediaController mockMediaController;

    @Mock
    MediaViewModel mockMediaViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void removesItemFromQueue_whenRemoveFromQueueMenuClicked() {
        MediaItem item1 = MediaItem.fromUri("uri1");
        MediaItem item2 = MediaItem.fromUri("uri2");
        List<MediaItem> queue = Arrays.asList(item1, item2);

        MutableLiveData<List<MediaItem>> liveData = new MutableLiveData<>(queue);
        when(mockMediaViewModel.getQueue()).thenReturn(liveData);

        FragmentScenario<QueueFragment> scenario = FragmentScenario.launchInContainer(QueueFragment.class, new Bundle(), null);
        scenario.onFragment(fragment -> {
            fragment.mediaController = mockMediaController;
            fragment.mediaViewModel = mockMediaViewModel;
            fragment.queueAdapter = mock(MyQueueRecyclerViewAdapter.class);
            when(fragment.queueAdapter.getContextMenuItem()).thenReturn(item1);
            when(mockMediaController.getMediaItemCount()).thenReturn(2);
            when(mockMediaController.getMediaItemAt(0)).thenReturn(item1);
            when(mockMediaController.getMediaItemAt(1)).thenReturn(item2);

            android.view.MenuItem menuItem = mock(android.view.MenuItem.class);
            when(menuItem.getItemId()).thenReturn(R.id.remove_from_queue);


            verify(mockMediaController).removeMediaItem(0);
            verify(mockMediaViewModel).removeFromQueue(item1);
        });
    }

    @Test
    public void restoresQueueFromViewModel_whenControllerIsEmptyOnResume() {
        MediaItem item1 = MediaItem.fromUri("uri1");
        List<MediaItem> savedQueue = Collections.singletonList(item1);

        MutableLiveData<List<MediaItem>> liveData = new MutableLiveData<>(savedQueue);
        when(mockMediaViewModel.getQueue()).thenReturn(liveData);

        FragmentScenario<QueueFragment> scenario = FragmentScenario.launchInContainer(QueueFragment.class, new Bundle(), null);
        scenario.onFragment(fragment -> {
            fragment.mediaController = mockMediaController;
            fragment.mediaViewModel = mockMediaViewModel;
            fragment.queueAdapter = mock(MyQueueRecyclerViewAdapter.class);

            when(mockMediaController.getMediaItemCount()).thenReturn(0);

            fragment.updateQueue();

            verify(mockMediaController).addMediaItems(savedQueue);
        });
    }

    @Test
    public void doesNotRestoreQueue_whenControllerHasItems() {
        MediaItem item1 = MediaItem.fromUri("uri1");
        List<MediaItem> savedQueue = Collections.singletonList(item1);

        MutableLiveData<List<MediaItem>> liveData = new MutableLiveData<>(savedQueue);
        when(mockMediaViewModel.getQueue()).thenReturn(liveData);

        FragmentScenario<QueueFragment> scenario = FragmentScenario.launchInContainer(QueueFragment.class, new Bundle(), null);
        scenario.onFragment(fragment -> {
            fragment.mediaController = mockMediaController;
            fragment.mediaViewModel = mockMediaViewModel;
            fragment.queueAdapter = mock(MyQueueRecyclerViewAdapter.class);

            when(mockMediaController.getMediaItemCount()).thenReturn(1);

            fragment.updateQueue();

            verify(mockMediaController, never()).addMediaItems(anyList());
        });
    }

    @Test
    public void clickingQueueItem_removesPreviousItemsAndStartsPlaybackIfWasPlaying() {
        MediaItem item1 = MediaItem.fromUri("uri1");
        MediaItem item2 = MediaItem.fromUri("uri2");

        when(mockMediaController.getCurrentMediaItemIndex()).thenReturn(0);
        when(mockMediaController.isPlaying()).thenReturn(true);

        FragmentScenario<QueueFragment> scenario = FragmentScenario.launchInContainer(QueueFragment.class, new Bundle(), null);
        scenario.onFragment(fragment -> {
            fragment.mediaController = mockMediaController;
            fragment.mediaViewModel = mockMediaViewModel;
            fragment.queueAdapter = mock(MyQueueRecyclerViewAdapter.class);

            QueueFragment.QueueViewClickHandler handler = fragment.new QueueViewClickHandler();
            handler.onClick(1, item2);

            verify(mockMediaController).removeMediaItems(0, 1);
            verify(mockMediaViewModel).removeRangeFromQueue(0, 1);
            verify(mockMediaController).prepare();
            verify(mockMediaController).play();
        });
    }

    @Test
    public void clickingQueueItem_doesNotStartPlaybackIfWasNotPlaying() {
        MediaItem item1 = MediaItem.fromUri("uri1");
        MediaItem item2 = MediaItem.fromUri("uri2");

        when(mockMediaController.getCurrentMediaItemIndex()).thenReturn(0);
        when(mockMediaController.isPlaying()).thenReturn(false);

        FragmentScenario<QueueFragment> scenario = FragmentScenario.launchInContainer(QueueFragment.class, new Bundle(), null);
        scenario.onFragment(fragment -> {
            fragment.mediaController = mockMediaController;
            fragment.mediaViewModel = mockMediaViewModel;
            fragment.queueAdapter = mock(MyQueueRecyclerViewAdapter.class);

            QueueFragment.QueueViewClickHandler handler = fragment.new QueueViewClickHandler();
            handler.onClick(1, item2);

            verify(mockMediaController).removeMediaItems(0, 1);
            verify(mockMediaViewModel).removeRangeFromQueue(0, 1);
            verify(mockMediaController, never()).prepare();
            verify(mockMediaController, never()).play();
        });
    }
}
