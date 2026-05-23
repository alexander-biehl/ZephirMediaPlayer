package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.PlaylistRepository;

public class DeletePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    public DeletePlaylistUseCase(@NonNull PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback) {
        playlistRepository.delete(mediaId, callback);
    }
}

