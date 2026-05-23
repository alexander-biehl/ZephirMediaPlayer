package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;

public class RenamePlaylistUseCase {

    private final PlaylistRepositoryGateway playlistRepository;

    public RenamePlaylistUseCase(@NonNull PlaylistRepositoryGateway playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(
            @NonNull String mediaId,
            @NonNull String newTitle,
            @NonNull RepositoryCallback<Void> callback
    ) {
        String normalizedTitle = newTitle.trim();
        if (normalizedTitle.isEmpty()) {
            callback.onComplete(new Result.Error<>("Playlist title must not be empty"));
            return;
        }
        playlistRepository.rename(mediaId, normalizedTitle, callback);
    }
}

