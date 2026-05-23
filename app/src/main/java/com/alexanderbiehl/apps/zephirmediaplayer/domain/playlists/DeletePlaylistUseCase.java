package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;

public class DeletePlaylistUseCase {

    private final PlaylistRepositoryGateway playlistRepository;

    public DeletePlaylistUseCase(@NonNull PlaylistRepositoryGateway playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback) {
        playlistRepository.delete(mediaId, callback);
    }
}

