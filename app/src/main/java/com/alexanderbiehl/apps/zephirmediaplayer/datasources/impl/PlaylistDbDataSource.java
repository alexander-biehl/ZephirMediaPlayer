package com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl;

import android.util.Log;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;

import java.util.List;
import java.util.concurrent.Executor;

public class PlaylistDbDataSource {

    private static final String TAG = PlaylistDbDataSource.class.getSimpleName();

    private final PlaylistDao dao;
    private final Executor executor;

    public PlaylistDbDataSource(
            PlaylistDao playlistDao,
            Executor executor
    ) {
        this.dao = playlistDao;
        this.executor = executor;
    }

    public void createPlaylist(PlaylistEntity entity, RepositoryCallback<Void> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Async createPlaylist " + entity);
        }
        this.executor.execute(() -> {
            Long id = createPlaylist(entity);
            if ((id != null)) {
                callback.onComplete(new Result.Success<>());
            } else {
                callback.onComplete(new Result.Error<>("Failed to insert playlist"));
            }
        });
    }

    /**
     * Attempts to insert a new PlaylistEntity record in the database. Returns a Long id
     * if successful
     *
     * @param entity - the playlist entity to insert
     * @return Long id if successful.
     */
    public Long createPlaylist(PlaylistEntity entity) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "createPlaylist - " + entity);
        }
        return dao.insert(entity);
    }

    public void getByMediaId(final String mediaId, RepositoryCallback<PlaylistEntity> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Async Playlist getByMediaId, mediaId: " + mediaId);
        }
        this.executor.execute(() -> {
            PlaylistEntity entity = getByMediaId(mediaId);
            if (entity != null) {
                callback.onComplete(new Result.Success<>(entity));
            } else {
                callback.onComplete(new Result.Error<>("No playlist entities found for mediaId: " +
                        mediaId));
            }
        });
    }

    public PlaylistEntity getByMediaId(final String mediaId) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Playlist getByMediaId, mediaId: " + mediaId);
        }
        return dao.getByMediaId(mediaId);
    }

    public void getPlaylistSongsByMediaId(final String mediaId, RepositoryCallback<PlaylistSongs> callback) {
        this.executor.execute(() -> {
            PlaylistSongs ps = getPlaylistSongsByMediaId(mediaId);
            if (ps != null && ps.playlistEntity != null && ps.songEntities != null) {
                callback.onComplete(new Result.Success<>(ps));
            } else {
                callback.onComplete(new Result.Error<>("Unable to locate PlaylistSongs by mediaId: " + mediaId));
            }
        });
    }

    public PlaylistSongs getPlaylistSongsByMediaId(final String mediaId) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getPlaylistSongsByMediaId - mediaId: " + mediaId);
        }
        return dao.getPlaylistSongsByMediaId(mediaId);
    }

    public void getAll(RepositoryCallback<List<PlaylistEntity>> callback) {
        this.executor.execute(() -> {
            List<PlaylistEntity> entities = getAll();
            if (entities != null && !entities.isEmpty()) {
                callback.onComplete(new Result.Success<>(entities));
            } else {
                callback.onComplete(new Result.Error<>("No Playlist found"));
            }
        });
    }

    public List<PlaylistEntity> getAll() {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Get all Playlists");
        }
        List<PlaylistEntity> entities = dao.getAllPlaylists();
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Retrieved " + entities.size() + " playlist entities.");
        }
        return entities;
    }
}
