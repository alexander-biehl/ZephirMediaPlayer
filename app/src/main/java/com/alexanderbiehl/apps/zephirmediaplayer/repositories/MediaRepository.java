package com.alexanderbiehl.apps.zephirmediaplayer.repositories;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.MediaDataSource;

import java.util.List;

/**
 * This class represents the Single Source of Truth for Media Data. How it
 * gets that Media data depends on the implementation of the underlying data
 * source, and does not matter to whoever is calling this class.
 */
public class MediaRepository {

    private static final String TAG = MediaRepository.class.getSimpleName();

    private final MediaDataSource dataSource;

    public MediaRepository(final MediaDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Retrieves the list of media items from the underlying data source
     * @return List<MediaItem>
     */
    public void getMedia(RepositoryCallback<List<MediaItem>> callback) {
        this.dataSource.getMedia(callback);
    }

}
