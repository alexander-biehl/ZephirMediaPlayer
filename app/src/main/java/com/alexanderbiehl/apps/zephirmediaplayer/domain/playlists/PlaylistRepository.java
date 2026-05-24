package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;

import java.util.List;

public interface PlaylistRepository {

    void create(@NonNull PlaylistEntity entity, @NonNull RepositoryCallback<Void> callback);

    void rename(@NonNull String mediaId, @NonNull String newTitle, @NonNull RepositoryCallback<Void> callback);

    void delete(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback);

    void getAll(@NonNull RepositoryCallback<List<PlaylistEntity>> callback);

    List<PlaylistEntity> getAll();

    PlaylistEntity getByMediaId(@NonNull String mediaId);

    List<MediaItem> getSongsByPlaylistId(@NonNull String mediaId);

    void addMediaItemsToPlaylist(
            @NonNull PlaylistEntity playlist,
            @NonNull List<MediaItem> mediaItems,
            @NonNull RepositoryCallback<Void> callback
    );
}



