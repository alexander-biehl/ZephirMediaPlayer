package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import androidx.media3.common.MediaItem;

import java.util.Collections;
import java.util.List;

public class MediaDbDataSource implements MediaDataSource{

    // TODO this should query the db on a background thread

    @Override
    public List<MediaItem> getMedia() {
        return Collections.emptyList();
    }
}
