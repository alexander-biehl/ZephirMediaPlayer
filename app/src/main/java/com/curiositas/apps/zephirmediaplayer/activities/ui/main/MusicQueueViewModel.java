package com.curiositas.apps.zephirmediaplayer.activities.ui.main;

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

    private LiveData<List<MediaBrowserCompat.MediaItem>> currentQueue;

    public MusicQueueViewModel(@NonNull Application application) {
        super(application);
        currentQueue = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<MediaBrowserCompat.MediaItem>> getQueue() {
        return currentQueue;
    }
}
