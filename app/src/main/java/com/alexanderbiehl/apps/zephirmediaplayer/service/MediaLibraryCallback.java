package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionError;

import com.alexanderbiehl.apps.zephirmediaplayer.MainApp;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl.MediaLocalDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.MediaRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class MediaLibraryCallback implements MediaLibraryService.MediaLibrarySession.Callback {

    private static final String TAG = MediaLibraryCallback.class.getSimpleName();
    private final Context context;
    private final MainApp mainApp;

    private boolean isInitialized;

    public MediaLibraryCallback(@NonNull final Context context, @NonNull final MainApp mainApp) {
        this.context = context;
        this.mainApp = mainApp;
        this.isInitialized = false;
        initializeData();
    }

    private void initializeData() {
        MediaRepository repo = new MediaRepository(
                new MediaLocalDataSource(
                        new MediaLoader(),
                        this.mainApp.getExec(),
                        context
                ));

        repo.getMedia((result) -> {
            if (result instanceof Result.Success) {
                MediaItemTree.getInstance().initialize(((Result.Success<List<MediaItem>>) result).data);
                isInitialized = true;
            } else {
                Log.e(TAG, "There was an error receiving media: " +
                        ((Result.Error<?>) result).ex.toString());
            }
        });
    }

    /**
     * Synchronized method to check if we are initialized
     *
     * @return true if initialized.
     */
    private synchronized boolean getIsInitialized() {
        return this.isInitialized;
    }


    @OptIn(markerClass = UnstableApi.class)
    @NonNull
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        return Futures.submit(new Callable<LibraryResult<MediaItem>>() {
            @Override
            public LibraryResult<MediaItem> call() throws Exception {
                return LibraryResult.ofItem(MediaItemTree.getInstance().getRootItem(), params);
            }
        }, this.mainApp.getExec());
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
            Optional<MediaItem> optItem = MediaItemTree.getInstance().getItem(mediaId);
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
            List<MediaItem> optChildren = MediaItemTree.getInstance().getChildren(parentId);
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
        return Futures.submit(() -> {
            return resolveMediaItems(mediaItems);
        }, this.mainApp.getExec());
    }

    private List<MediaItem> resolveMediaItems(List<MediaItem> mediaItems) {
        List<MediaItem> playlist = new ArrayList<>();
        mediaItems.forEach(mediaItem -> {
            if (!mediaItem.mediaId.isEmpty()) {
                Optional<MediaItem> expandedItem = MediaItemTree.getInstance().expandItem(mediaItem);
                expandedItem.ifPresent(playlist::add);
            }
        });
        return playlist;
    }


}
