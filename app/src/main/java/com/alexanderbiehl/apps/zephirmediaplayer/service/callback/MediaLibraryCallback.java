package com.alexanderbiehl.apps.zephirmediaplayer.service.callback;

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

import com.alexanderbiehl.apps.zephirmediaplayer.domain.MediaItemUseCase;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class MediaLibraryCallback
        implements MediaLibraryService.MediaLibrarySession.Callback {

    private static final String TAG = MediaLibraryCallback.class.getSimpleName();
    private final MediaItemUseCase useCase;
    private final Executor executor;

    @Inject
    public MediaLibraryCallback(@NonNull final MediaItemUseCase useCase, @NonNull final Executor executor) {
        this.useCase = useCase;
        this.executor = executor;
    }


    @OptIn(markerClass = UnstableApi.class)
    @NonNull
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onGetLibraryRoot: session: " + session +
                    "\nbrowser: " + browser +
                    "\nparams: " + params);
        }
        return Futures.submit(() ->
                LibraryResult.ofItem(useCase.getRoot(), params), this.executor);
    }

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetItem(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @NonNull String mediaId
    ) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onGetItem:" +
                    "\nsession: " + session +
                    "\nbrowser: " + browser +
                    "\nmediaID: " + mediaId);
        }
        return Futures.submit(() -> {
            Optional<MediaItem> optItem = useCase.getItem(mediaId);
            return optItem.map(mediaItem ->
                            LibraryResult.ofItem(mediaItem, null))
                    .orElseGet(() ->
                            LibraryResult.ofError(SessionError.ERROR_BAD_VALUE));
        }, this.executor);
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
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onGetChildren:" +
                    "\nsession: " + session +
                    "\nbrowser: " + browser +
                    "\nparentId: " + parentId);
        }
        return Futures.submit(() -> {
            List<MediaItem> optChildren = useCase.getChildren(parentId);
            return optChildren.isEmpty() ?
                    LibraryResult.ofError(SessionError.ERROR_BAD_VALUE) :
                    LibraryResult.ofItemList(optChildren, params);
        }, this.executor);
    }

    @NonNull
    @Override
    public ListenableFuture<List<MediaItem>> onAddMediaItems(
            @NonNull MediaSession mediaSession,
            @NonNull MediaSession.ControllerInfo controller,
            @NonNull List<MediaItem> mediaItems
    ) {
        return Futures.submit(() -> resolveMediaItems(mediaItems), this.executor);
    }

    private List<MediaItem> resolveMediaItems(List<MediaItem> mediaItems) {
        List<MediaItem> playlist = new ArrayList<>();
        for (MediaItem mediaItem : mediaItems) {
            useCase.expandItem(mediaItem).ifPresent(playlist::add);
        }
        return playlist;
    }
}
