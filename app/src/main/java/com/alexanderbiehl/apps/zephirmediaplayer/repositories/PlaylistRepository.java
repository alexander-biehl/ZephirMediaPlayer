package com.alexanderbiehl.apps.zephirmediaplayer.repositories;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl.PlaylistDbDataSource;

import java.util.List;

public class PlaylistRepository {

    private static final String TAG = PlaylistRepository.class.getSimpleName();
    private final PlaylistDbDataSource dataSource;

    public PlaylistRepository(final PlaylistDbDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(PlaylistEntity entity, RepositoryCallback<Void> callback) {
        this.dataSource.createPlaylist(entity, callback);
    }

    public void getByMediaId(final String mediaId, RepositoryCallback<PlaylistEntity> callback) {
        this.dataSource.getByMediaId(mediaId, callback);
    }

    public PlaylistEntity getByMediaId(final String mediaId) {
        return this.dataSource.getByMediaId(mediaId);
    }

    public void getPlaylistSongsByMediaId(final String mediaId, RepositoryCallback<PlaylistSongs> callback) {
        this.dataSource.getPlaylistSongsByMediaId(mediaId, callback);
    }

    public PlaylistSongs getPlaylistSongsByMediaId(final String mediaId) {
        return this.dataSource.getPlaylistSongsByMediaId(mediaId);
    }

    public void getAll(RepositoryCallback<List<PlaylistEntity>> callback) {
        this.dataSource.getAll(callback);
    }

    public List<PlaylistEntity> getAll() {
        return this.dataSource.getAll();
    }
}
