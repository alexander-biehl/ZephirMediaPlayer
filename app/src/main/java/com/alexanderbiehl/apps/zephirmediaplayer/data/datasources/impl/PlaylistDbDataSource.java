package com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl;

import android.util.Log;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.m2m.PlaylistSongM2M;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaylistDbDataSource {

    private static final String TAG = PlaylistDbDataSource.class.getSimpleName();

    private final PlaylistDao dao;
    private final SongDao songDao;
    private final Executor executor;

    public PlaylistDbDataSource(
            PlaylistDao playlistDao,
            SongDao songDao,
            Executor executor
    ) {
        this.dao = playlistDao;
        this.songDao = songDao;
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

    public void renamePlaylist(
            final String mediaId,
            final String newTitle,
            RepositoryCallback<Void> callback
    ) {
        this.executor.execute(() -> {
            try {
                PlaylistEntity entity = dao.getByMediaId(mediaId);
                if (entity == null) {
                    callback.onComplete(new Result.Error<>("Playlist not found"));
                    return;
                }
                entity.title = newTitle;
                dao.update(entity);
                callback.onComplete(new Result.Success<>());
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        });
    }

    public void deletePlaylist(final String mediaId, RepositoryCallback<Void> callback) {
        this.executor.execute(() -> {
            try {
                PlaylistEntity entity = dao.getByMediaId(mediaId);
                if (entity == null) {
                    callback.onComplete(new Result.Error<>("Playlist not found"));
                    return;
                }
                if (entity.id != null) {
                    dao.deletePlaylistSongMappings(entity.id);
                }
                dao.delete(entity);
                callback.onComplete(new Result.Success<>());
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        });
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

    public SongEntity[] getSongsByPlaylistMediaId(final String mediaId) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "getSongsByPlaylistMediaId - mediaId: " + mediaId);
        }
        return dao.getSongsByPlaylistMediaId(mediaId);
    }

    public void addMediaItemsToPlaylist(
            final PlaylistEntity playlist,
            final List<MediaItem> mediaItems,
            RepositoryCallback<Void> callback
    ) {
        this.executor.execute(() -> {
            try {
                if (playlist == null || mediaItems == null || mediaItems.isEmpty()) {
                    callback.onComplete(new Result.Error<>("No playlist or media items supplied"));
                    return;
                }

                PlaylistSongs existing = dao.getPlaylistSongsByMediaId(playlist.mediaId);
                if (existing == null || existing.playlistEntity == null) {
                    callback.onComplete(new Result.Error<>("Playlist not found"));
                    return;
                }
                int nextOrder = existing.songEntities != null
                        ? existing.songEntities.size()
                        : 0;
                AtomicInteger orderCounter = new AtomicInteger(nextOrder);

                PlaylistSongM2M[] links = mediaItems.stream()
                        .map(item -> songDao.getByMediaId(item.mediaId))
                        .filter(Objects::nonNull)
                        .map(song -> {
                            PlaylistSongM2M link = new PlaylistSongM2M();
                            // link.playlistId = playlist.id;
                            link.playlistId = existing.playlistEntity.id;
                            link.songId = song.id;
                            link.order = orderCounter.getAndIncrement();
                            return link;
                        })
                        .toArray(PlaylistSongM2M[]::new);

                if (links.length == 0) {
                    callback.onComplete(new Result.Error<>("No matching songs found to add to playlist"));
                    return;
                }

                dao.insertPlaylistSongs(links);
                callback.onComplete(new Result.Success<>());
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        });
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
