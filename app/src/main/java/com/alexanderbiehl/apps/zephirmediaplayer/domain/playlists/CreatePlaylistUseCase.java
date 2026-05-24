package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.PLAYLIST_PREFIX;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;

import java.util.UUID;

public class CreatePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    public CreatePlaylistUseCase(@NonNull PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(@NonNull String title, @NonNull RepositoryCallback<Void> callback) {
        String normalizedTitle = title.trim();
        if (normalizedTitle.isEmpty()) {
            callback.onComplete(new Result.Error<>("Playlist title must not be empty"));
            return;
        }

        PlaylistEntity entity = new PlaylistEntity();
        entity.title = normalizedTitle;
        entity.mediaId = PLAYLIST_PREFIX + UUID.randomUUID();
        entity.numTracks = 0;
        entity.durationMs = 0L;
        playlistRepository.create(entity, callback);
    }
}

