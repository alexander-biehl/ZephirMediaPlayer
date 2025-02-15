package com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl;

import android.content.Context;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.MediaDataSource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaLocalDataSource implements MediaDataSource {

    private static final String TAG = MediaLocalDataSource.class.getSimpleName();

    private final MediaLoader mediaLoader;
    private final Executor executor;
    private final Context context;

    /**
     * Constructor taking in the object that will load our data
     * and an executor that will execute the work on a backend thread
     *
     * @param mediaLoader
     * @param executor
     * @param context
     */
    public MediaLocalDataSource(
            final MediaLoader mediaLoader,
            final Executor executor,
            final Context context
    ) {
        this.mediaLoader = mediaLoader;
        this.executor = executor;
        this.context = context;
    }


    @Override
    public void getMedia(RepositoryCallback<List<MediaItem>> callback) {
        this.executor.execute(() -> {
            List<MediaItem> items = mediaLoader.getMedia(context);
            callback.onComplete(new Result.Success<>(items));
        });
    }

    @Override
    public List<MediaItem> getMediaSynchronous() {
        return Collections.emptyList();
    }
}
