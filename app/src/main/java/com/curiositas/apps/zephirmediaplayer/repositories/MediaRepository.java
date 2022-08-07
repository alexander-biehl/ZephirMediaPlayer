package com.curiositas.apps.zephirmediaplayer.repositories;

import android.app.Application;
import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.curiositas.apps.zephirmediaplayer.MainApp;
import com.curiositas.apps.zephirmediaplayer.dataloaders.MediaLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaRepository {

    private static final String TAG = MediaRepository.class.getSimpleName();
    private final Executor executor;
    private final Context context;
    private List<MediaMetadataCompat> media;
    private List<Callback> callbacks;

    private boolean isReady;
    private final Object readyLock;
    private final Object mediaLock;

    public MediaRepository(Application application) {
        callbacks = new ArrayList<>();
        this.executor = ((MainApp) application).getExec();
        this.context = application.getApplicationContext();
        isReady = false;
        readyLock = new Object();
        mediaLock = new Object();
        loadMedia();
    }

    private void loadMedia() {
        this.executor.execute(() -> {
            Log.d(TAG, "Starting media load");
            List<MediaMetadataCompat> metadata = MediaLoader.getMedia(context);
            setMedia(metadata);
            notifyListeners();
            Log.d(TAG, "Completed media load");
        });
    }

    public boolean isReady() {
        synchronized (readyLock) {
            return isReady;
        }
    }

    private void setIsReady(boolean isReady) {
        synchronized (readyLock) {
            this.isReady = isReady;
        }
    }

    public List<MediaMetadataCompat> getMedia() {
        synchronized (mediaLock) {
            return media;
        }
    }

    private void setMedia(final List<MediaMetadataCompat> media) {
        synchronized (mediaLock) {
            this.media = media;
        }
    }

    public interface Callback {
        void onReady();
    }

    public void subscribe(final Callback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void unsubscribe(final Callback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    public void notifyListeners() {
        for (Callback call : callbacks) {
            call.onReady();
        }
    }
}
