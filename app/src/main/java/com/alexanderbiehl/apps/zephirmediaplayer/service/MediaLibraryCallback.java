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

    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(
            MediaLibraryService.MediaLibrarySession session,
            MediaSession.ControllerInfo browser,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        return Futures.immediateFuture(LibraryResult.ofItem(
                MediaItemTree.getInstance().getRootItem(), params));
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetItem(
            MediaLibraryService.MediaLibrarySession session,
            MediaSession.ControllerInfo browser,
            String mediaId
    ) {
        Optional<MediaItem> optItem = MediaItemTree.getInstance().getItem(mediaId);
        if (optItem.isEmpty()) {
            return Futures.immediateFuture(LibraryResult.ofError(SessionError.ERROR_BAD_VALUE));
        }
        return Futures.immediateFuture(LibraryResult.ofItem(optItem.get(), null));
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(
            MediaLibraryService.MediaLibrarySession session,
            MediaSession.ControllerInfo browser,
            String parentId,
            int page,
            int pageSize,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        List<MediaItem> optChildren = MediaItemTree.getInstance().getChildren(parentId);
        return optChildren.isEmpty() ?
                Futures.immediateFuture(LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)) :
                Futures.immediateFuture(LibraryResult.ofItemList(optChildren, params));
    }

    @Override
    public ListenableFuture<List<MediaItem>> onAddMediaItems(
            MediaSession mediaSession,
            MediaSession.ControllerInfo controller,
            List<MediaItem> mediaItems
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
        return playlist;
    }
}
