package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class MediaViewModel extends ViewModel {

    private final MutableLiveData<MediaItem> currentMedia;
    private final MutableLiveData<List<MediaItem>> currentQueue;

    public MediaViewModel() {
        this.currentMedia = new MutableLiveData<>();
        this.currentQueue = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<MediaItem>> getQueue() {
        return this.currentQueue;
    }

    public void setQueue(final List<MediaItem> queue) {
        this.currentQueue.setValue(queue);
    }

    public LiveData<MediaItem> getCurrentMedia() {
        return this.currentMedia;
    }

    public void setCurrentMedia(final MediaItem item) {
        this.currentMedia.setValue(item);
    }
}
