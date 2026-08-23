package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.PlaylistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.PlaylistRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private static final String TAG = PlaylistRepositoryImpl.class.getSimpleName();
    private final PlaylistDbDataSource dataSource;

    @Inject
    public PlaylistRepositoryImpl(final PlaylistDbDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(@NonNull PlaylistEntity entity, @NonNull RepositoryCallback<Void> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "create: with callback" + entity);
        }
        this.dataSource.createPlaylist(entity, callback);
    }

    public void rename(@NonNull final String mediaId, @NonNull final String newTitle, @NonNull RepositoryCallback<Void> callback) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "rename: mediaId=" + mediaId + ", newTitle=" + newTitle);
        }
        this.dataSource.renamePlaylist(mediaId, newTitle, callback);
    }

    public void delete(@NonNull final String mediaId, @NonNull RepositoryCallback<Void> callback) {
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

    public PlaylistEntity getByMediaId(@NonNull final String mediaId) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getByMediaId: " + mediaId);
        }
        return this.dataSource.getByMediaId(mediaId);
    }

    public void getAll(@NonNull RepositoryCallback<List<PlaylistEntity>> callback) {
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

    public List<MediaItem> getSongsByPlaylistId(@NonNull String mediaId) {
        SongEntity[] songs = dataSource.getSongsByPlaylistMediaId(mediaId);
        if (songs == null || songs.length == 0) {
            return List.of();
        }
        return Arrays.stream(songs).map(SongEntity::toItem).collect(Collectors.toList());
    }

    public MediaItem getById(String mediaId) {
        return PlaylistEntity.toItem(dataSource.getByMediaId(mediaId));
    }

    public void addMediaItemsToPlaylist(
            @NonNull final PlaylistEntity playlist,
            @NonNull final List<MediaItem> mediaItems,
            @NonNull RepositoryCallback<Void> callback
    ) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "addMediaItemsToPlaylist: playlist=" + playlist + ", items=" + (mediaItems == null ? 0 : mediaItems.size()));
        }
        this.dataSource.addMediaItemsToPlaylist(playlist, mediaItems, callback);
    }
}
