package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.MainApp;
import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.CreatePlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.DeletePlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.GetPlaylistsUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.RenamePlaylistUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PlaylistsViewModel extends AndroidViewModel {

    private final GetPlaylistsUseCase getPlaylistsUseCase;
    private final CreatePlaylistUseCase createPlaylistUseCase;
    private final RenamePlaylistUseCase renamePlaylistUseCase;
    private final DeletePlaylistUseCase deletePlaylistUseCase;
    private final MutableLiveData<List<MediaItem>> playlists;
    private final MutableLiveData<UiMessage> uiMessages;
    private final AtomicLong messageId;

    public PlaylistsViewModel(@NonNull Application application) {
        super(application);
        MainApp app = (MainApp) application;
        this.getPlaylistsUseCase = app.getAppContainer().getGetPlaylistsUseCase();
        this.createPlaylistUseCase = app.getAppContainer().getCreatePlaylistUseCase();
        this.renamePlaylistUseCase = app.getAppContainer().getRenamePlaylistUseCase();
        this.deletePlaylistUseCase = app.getAppContainer().getDeletePlaylistUseCase();
        this.playlists = new MutableLiveData<>(new ArrayList<>());
        this.uiMessages = new MutableLiveData<>();
        this.messageId = new AtomicLong(0);
    }

    public LiveData<List<MediaItem>> getPlaylists() {
        return playlists;
    }

    public LiveData<UiMessage> getUiMessages() {
        return uiMessages;
    }

    public void loadPlaylists() {
        getPlaylistsUseCase.execute(result -> {
            if (result instanceof Result.Success<?> success && success.data != null) {
                @SuppressWarnings("unchecked")
                List<MediaItem> items = (List<MediaItem>) success.data;
                playlists.postValue(items);
            } else {
                // No playlists is a valid state; expose an empty list.
                playlists.postValue(new ArrayList<>());
            }
        });
    }

    public void createPlaylist(@NonNull String title) {
        createPlaylistUseCase.execute(title, result -> {
            if (result instanceof Result.Success<?>) {
                postMessage(R.string.playlist_created, title);
                loadPlaylists();
            } else {
                postMessage(R.string.playlist_create_failed, null);
            }
        });
    }

    public void renamePlaylist(@NonNull String mediaId, @NonNull String newTitle) {
        renamePlaylistUseCase.execute(mediaId, newTitle, result -> {
            if (result instanceof Result.Success<?>) {
                postMessage(R.string.playlist_renamed, newTitle);
                loadPlaylists();
            } else {
                postMessage(R.string.playlist_rename_failed, null);
            }
        });
    }

    public void deletePlaylist(@NonNull String mediaId, @NonNull String playlistTitle) {
        deletePlaylistUseCase.execute(mediaId, result -> {
            if (result instanceof Result.Success<?>) {
                postMessage(R.string.playlist_deleted, playlistTitle);
                loadPlaylists();
            } else {
                postMessage(R.string.playlist_delete_failed, null);
            }
        });
    }

    private void postMessage(int resId, @Nullable String formatArg) {
        uiMessages.postValue(new UiMessage(messageId.incrementAndGet(), resId, formatArg));
    }

    public static class UiMessage {
        public final long id;
        public final int resId;
        @Nullable
        public final String formatArg;

        public UiMessage(long id, int resId, @Nullable String formatArg) {
            this.id = id;
            this.resId = resId;
            this.formatArg = formatArg;
        }
    }
}


