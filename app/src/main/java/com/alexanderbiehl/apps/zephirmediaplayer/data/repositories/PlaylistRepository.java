package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import android.util.Log;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.PlaylistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.PlaylistRepositoryGateway;

import java.util.List;
import java.util.stream.Collectors;

public class PlaylistRepository implements PlaylistRepositoryGateway {

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

    public void rename(final String mediaId, final String newTitle, RepositoryCallback<Void> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "rename: mediaId=" + mediaId + ", newTitle=" + newTitle);
        }
        this.dataSource.renamePlaylist(mediaId, newTitle, callback);
    }

    public void delete(final String mediaId, RepositoryCallback<Void> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "delete: mediaId=" + mediaId);
        }
        this.dataSource.deletePlaylist(mediaId, callback);
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
        SongEntity[] songs = dataSource.getSongsByPlaylistMediaId(mediaId);
        if (songs == null || songs.length == 0) {
            return List.of();
        }
        return java.util.Arrays.stream(songs).map(SongEntity::toItem).collect(Collectors.toList());
    }

    public MediaItem getById(String mediaId) {
        return PlaylistEntity.toItem(dataSource.getByMediaId(mediaId));
    }

    public void addMediaItemsToPlaylist(
            final PlaylistEntity playlist,
            final List<MediaItem> mediaItems,
            RepositoryCallback<Void> callback
    ) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "addMediaItemsToPlaylist: playlist=" + playlist + ", items=" + (mediaItems == null ? 0 : mediaItems.size()));
        }
        this.dataSource.addMediaItemsToPlaylist(playlist, mediaItems, callback);
    }
}
