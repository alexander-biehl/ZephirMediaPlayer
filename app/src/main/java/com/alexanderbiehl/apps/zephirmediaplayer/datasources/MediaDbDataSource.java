package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;

import java.util.List;

public class MediaDbDataSource implements MediaDataSource {


    // TODO this should query the db on a background thread

    @Override
    public void getMedia(RepositoryCallback<List<MediaItem>> callback) {

    }
}
