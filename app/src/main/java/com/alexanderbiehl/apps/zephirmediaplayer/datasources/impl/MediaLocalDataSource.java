package com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl;

import android.content.Context;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaStoreLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.MediaDataSource;

import java.util.List;

public class MediaLocalDataSource implements MediaDataSource {

    private static final String TAG = MediaLocalDataSource.class.getSimpleName();

    private final MediaStoreLoader mediaStoreLoader;
    private final Context context;

    /**
     * Constructor taking in the object that will load our data
     * and an executor that will execute the work on a backend thread
     *
     * @param mediaStoreLoader
     * @param context
     */
    public MediaLocalDataSource(
            final MediaStoreLoader mediaStoreLoader,
            final Context context
    ) {
        this.mediaStoreLoader = mediaStoreLoader;
        this.context = context;
    }

    @Override
    public List<MediaItem> getMedia() {
        return mediaStoreLoader.getMedia(context);
    }
}
