package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionError;

import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MediaLibraryCallback implements MediaLibraryService.MediaLibrarySession.Callback {

    private final Context context;

    public MediaLibraryCallback(@NonNull Context context) {
        this.context = context;
        MediaItemTree.getInstance().initialize(
                MediaLoader.getMedia(this.context)
        );
    }

    @NonNull
    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(
            @NonNull MediaLibraryService.MediaLibrarySession session,
            @NonNull MediaSession.ControllerInfo browser,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        return Futures.immediateFuture(LibraryResult.ofItem(
                MediaItemTree.getInstance().getRootItem(), params));
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
