package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import android.util.Log;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.PlaylistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.PlaylistSongs;

import java.util.List;

public class PlaylistRepository {

    private static final String TAG = PlaylistRepository.class.getSimpleName();
    private final PlaylistDbDataSource dataSource;

    public PlaylistRepository(final PlaylistDbDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(PlaylistEntity entity, RepositoryCallback<Void> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "create: with callback" + entity);
        }
        this.dataSource.createPlaylist(entity, callback);
    }

    public void getByMediaId(final String mediaId, RepositoryCallback<PlaylistEntity> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getByMediaId: with callback" + mediaId);
        }
        this.dataSource.getByMediaId(mediaId, callback);
    }

    public PlaylistEntity getByMediaId(final String mediaId) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getByMediaId: " + mediaId);
        }
        return this.dataSource.getByMediaId(mediaId);
    }

    public void getPlaylistSongsByMediaId(final String mediaId, RepositoryCallback<PlaylistSongs> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getPlaylistSongsByMediaId: with callback" + mediaId);
        }
        this.dataSource.getPlaylistSongsByMediaId(mediaId, callback);
    }

    public PlaylistSongs getPlaylistSongsByMediaId(final String mediaId) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getPlaylistSongsByMediaId: " + mediaId);
        }
        return this.dataSource.getPlaylistSongsByMediaId(mediaId);
    }

    public void getAll(RepositoryCallback<List<PlaylistEntity>> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getAll: with callback");
        }
        this.dataSource.getAll(callback);
    }

    public List<PlaylistEntity> getAll() {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getAll: ");
        }
        return this.dataSource.getAll();
    }

    public List<MediaItem> getSongsByPlaylistId(String mediaId) {
        return List.of();
    }

    public MediaItem getById(String mediaId) {
        return null;
    }
}
