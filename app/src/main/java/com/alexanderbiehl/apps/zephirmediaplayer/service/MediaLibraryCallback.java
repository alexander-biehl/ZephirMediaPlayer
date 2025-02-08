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
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.media.impl.MediaLocalDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.MediaRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (getIsInitialized()) {
            return Futures.immediateFuture(LibraryResult.ofItem(
                    MediaItemTree.getInstance().getRootItem(), params));
        } else {
            Log.d(TAG, "Library wasn't ready yet");
            return Futures.immediateFuture(LibraryResult.ofError(SessionError.ERROR_INVALID_STATE));
        }
    }

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetItem(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @NonNull String mediaId
    ) {
        Optional<MediaItem> optItem = MediaItemTree.getInstance().getItem(mediaId);
        return optItem.map(mediaItem ->
                        Futures.immediateFuture(LibraryResult.ofItem(mediaItem, null)))
                .orElseGet(() ->
                        Futures.immediateFuture(LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)));
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
        List<MediaItem> optChildren = MediaItemTree.getInstance().getChildren(parentId);
        return optChildren.isEmpty() ?
                Futures.immediateFuture(LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)) :
                Futures.immediateFuture(LibraryResult.ofItemList(optChildren, params));
    }

    @NonNull
    @Override
    public ListenableFuture<List<MediaItem>> onAddMediaItems(
            @NonNull MediaSession mediaSession,
            @NonNull MediaSession.ControllerInfo controller,
            @NonNull List<MediaItem> mediaItems
    ) {
        return Futures.immediateFuture(resolveMediaItems(mediaItems));
    }

    private List<MediaItem> resolveMediaItems(List<MediaItem> mediaItems) {
        List<MediaItem> playlist = new ArrayList<>();
        mediaItems.forEach(mediaItem -> {
            if (!mediaItem.mediaId.isEmpty()) {
                Optional<MediaItem> expandedItem = MediaItemTree.getInstance().expandItem(mediaItem);
                expandedItem.ifPresent(playlist::add);
            }
        });
        // sort by track number
        // may want to update this later for a multi-level sort, i.e. sort by artistEntity, then albumEntity,
        // then track number
        // playlistEntity.sort(Comparator.comparingInt(a -> a.mediaMetadata.trackNumber));
        return playlist;
    }
}
