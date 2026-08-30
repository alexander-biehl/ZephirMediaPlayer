package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;

import java.util.List;

import javax.inject.Inject;

public class AddMediaItemsToPlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public AddMediaItemsToPlaylistUseCase(@NonNull PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(
            @NonNull PlaylistEntity playlist,
            @NonNull List<MediaItem> mediaItems,
            @NonNull RepositoryCallback<Void> callback
    ) {
        if (mediaItems.isEmpty()) {
            callback.onComplete(new Result.Error<>("No media items supplied"));
            return;
        }
        playlistRepository.addMediaItemsToPlaylist(playlist, mediaItems, callback);
    }
}

