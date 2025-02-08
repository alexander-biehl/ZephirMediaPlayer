package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import androidx.media3.common.MediaItem;

import java.util.List;

public interface MediaDataSource {

    List<MediaItem> getMedia();
}
