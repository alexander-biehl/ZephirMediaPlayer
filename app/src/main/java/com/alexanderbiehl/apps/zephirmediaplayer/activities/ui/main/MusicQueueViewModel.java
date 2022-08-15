package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main;

import android.app.Application;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class MusicQueueViewModel extends AndroidViewModel {

    private static final String TAG = MusicQueueViewModel.class.getSimpleName();

    private MutableLiveData<List<MediaBrowserCompat.MediaItem>> currentQueue;
    private MutableLiveData<String> currentMediaId;

    public MusicQueueViewModel(@NonNull Application application) {
        super(application);
        currentQueue = new MutableLiveData<>(new ArrayList<>());
        currentMediaId = new MutableLiveData<>();
    }

    public LiveData<List<MediaBrowserCompat.MediaItem>> getQueue() {
        return currentQueue;
    }

    public void setQueue(final List<MediaBrowserCompat.MediaItem> queue) {
        currentQueue.setValue(queue);
    }

    public LiveData<String> getCurrentMediaID() {
        return this.currentMediaId;
    }

    public void setCurrentMediaID(String mediaID) {
        this.currentMediaId.setValue(mediaID);
    }
}
