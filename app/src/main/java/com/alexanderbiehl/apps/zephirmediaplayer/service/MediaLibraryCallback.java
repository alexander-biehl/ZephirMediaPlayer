package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.databinding.Observable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionError;

import com.alexanderbiehl.apps.zephirmediaplayer.MainApp;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.CompositeMediaRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.MediaItemRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaLibraryCallback
        extends Observable.OnPropertyChangedCallback
        implements MediaLibraryService.MediaLibrarySession.Callback {

    private static final String TAG = MediaLibraryCallback.class.getSimpleName();
    private final Context context;
    private final MainApp mainApp;
    private final MediaItemRepository repository;


    public MediaLibraryCallback(@NonNull final Context context, @NonNull final MainApp mainApp) {
        this.context = context;
        this.mainApp = mainApp;
        this.repository = new MediaItemRepository(
                new CompositeMediaRepository(
                        AppDatabase.getDatabase(context)
                )
        );

        if (!this.mainApp.getStoreIsSynced().get()) {
            this.mainApp.getStoreIsSynced().addOnPropertyChangedCallback(this);
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    @NonNull
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        return Futures.submit(() ->
                LibraryResult.ofItem(repository.getRoot(), params), this.mainApp.getExec());
    }

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetItem(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @NonNull String mediaId
    ) {
        return Futures.submit(() -> {
            Optional<MediaItem> optItem = repository.getItem(mediaId);
            return optItem.map(mediaItem ->
                            LibraryResult.ofItem(mediaItem, null))
                    .orElseGet(() ->
                            LibraryResult.ofError(SessionError.ERROR_BAD_VALUE));
        }, this.mainApp.getExec());
    }

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @NonNull String parentId,
            int page,
            int pageSize,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        return Futures.submit(() -> {
            List<MediaItem> optChildren = repository.getChildren(parentId);
            return optChildren.isEmpty() ?
                    LibraryResult.ofError(SessionError.ERROR_BAD_VALUE) :
                    LibraryResult.ofItemList(optChildren, params);
        }, this.mainApp.getExec());
    }

    @NonNull
    @Override
    public ListenableFuture<List<MediaItem>> onAddMediaItems(
            @NonNull MediaSession mediaSession,
            @NonNull MediaSession.ControllerInfo controller,
            @NonNull List<MediaItem> mediaItems
    ) {
        return Futures.submit(() -> resolveMediaItems(mediaItems), this.mainApp.getExec());
    }

    private List<MediaItem> resolveMediaItems(List<MediaItem> mediaItems) {
        List<MediaItem> playlist = new ArrayList<>();
        for (MediaItem mediaItem : mediaItems) {
            repository.expandItem(mediaItem).ifPresent(playlist::add);
        }
        return playlist;
    }


    @Override
    public void onPropertyChanged(Observable sender, int propertyId) {

        // remove the listener since it's only for the initial load
        this.mainApp.getStoreIsSynced().removeOnPropertyChangedCallback(this);
    }
}
