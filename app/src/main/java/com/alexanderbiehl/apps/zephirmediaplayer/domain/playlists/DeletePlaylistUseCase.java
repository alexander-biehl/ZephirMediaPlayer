package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;

import javax.inject.Inject;

public class DeletePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public DeletePlaylistUseCase(@NonNull PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback) {
        playlistRepository.delete(mediaId, callback);
    }
}

