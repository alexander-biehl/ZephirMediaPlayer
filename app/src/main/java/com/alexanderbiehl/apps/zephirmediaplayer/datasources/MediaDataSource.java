package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;

import java.util.List;

public interface MediaDataSource {

    List<MediaItem> getMedia();
}
