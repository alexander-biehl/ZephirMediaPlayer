package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaLocalDataSource implements MediaDataSource{

    private static final String TAG = MediaLocalDataSource.class.getSimpleName();

    private final MediaLoader mediaLoader;
    private final Executor executor;

    /**
     * Constructor taking in the object that will load our data
     * and an executor that will execute the work on a backend thread
     *
     * @param mediaLoader
     * @param executor
     */
    public MediaLocalDataSource(
            final MediaLoader mediaLoader,
            final Executor executor
            ) {
        this.mediaLoader = mediaLoader;
        this.executor = executor;
    }

    @Override
    public List<MediaItem> getMedia() {
        return Collections.emptyList();
    }
}
