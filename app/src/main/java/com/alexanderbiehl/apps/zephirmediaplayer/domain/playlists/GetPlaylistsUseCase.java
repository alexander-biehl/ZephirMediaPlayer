package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class GetPlaylistsUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public GetPlaylistsUseCase(@NonNull PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(@NonNull RepositoryCallback<List<MediaItem>> callback) {
        playlistRepository.getAll(result -> {
            if (result instanceof Result.Success<?> success && success.data != null) {
                @SuppressWarnings("unchecked")
                List<PlaylistEntity> entities = (List<PlaylistEntity>) success.data;
                List<MediaItem> items = entities.stream()
                        .map(PlaylistEntity::toItem)
                        .collect(Collectors.toList());
                callback.onComplete(new Result.Success<>(items));
            } else if (result instanceof Result.Error<?> error) {
                callback.onComplete(new Result.Error<>(error.ex));
            } else {
                callback.onComplete(new Result.Success<>(List.of()));
            }
        });
    }
}

