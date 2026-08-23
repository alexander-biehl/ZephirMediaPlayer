package com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl;

import android.content.Context;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dataloaders.MediaStoreLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.MediaDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class MediaLocalDataSource implements MediaDataSource {

    private static final String TAG = MediaLocalDataSource.class.getSimpleName();

    private final MediaStoreLoader mediaStoreLoader;
    private final Context context;

    @Inject
    public MediaLocalDataSource(
            final MediaStoreLoader mediaStoreLoader,
            @ApplicationContext final Context context
    ) {
        this.mediaStoreLoader = mediaStoreLoader;
        this.context = context;
    }

    @Override
    public List<MediaItem> getMedia() {
        return mediaStoreLoader.getMedia(context);
    }
}
