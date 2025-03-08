package com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl;

import android.content.Context;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.MediaDataSource;

import java.util.List;

public class MediaLocalDataSource implements MediaDataSource {

    private static final String TAG = MediaLocalDataSource.class.getSimpleName();

    private final MediaLoader mediaLoader;
    private final Context context;

    /**
     * Constructor taking in the object that will load our data
     * and an executor that will execute the work on a backend thread
     *
     * @param mediaLoader
     * @param context
     */
    public MediaLocalDataSource(
            final MediaLoader mediaLoader,
            final Context context
    ) {
        this.mediaLoader = mediaLoader;
        this.context = context;
    }

    @Override
    public List<MediaItem> getMedia() {
        return mediaLoader.getMedia(context);
    }
}
