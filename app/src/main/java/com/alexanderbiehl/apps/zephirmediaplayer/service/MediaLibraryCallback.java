package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;

import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

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
        return MediaLibraryService.MediaLibrarySession.Callback.super.onGetLibraryRoot(session, browser, params);
    }

    @Override
    public ListenableFuture<LibraryResult<MediaItem>> onGetItem(
            MediaLibraryService.MediaLibrarySession session,
            MediaSession.ControllerInfo browser,
            String mediaId
    ) {
        return MediaLibraryService.MediaLibrarySession.Callback.super.onGetItem(session, browser, mediaId);
    }

    @Override
    public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(
            MediaLibraryService.MediaLibrarySession session,
            MediaSession.ControllerInfo browser,
            String parentId,
            int page,
            int pageSize,
            @Nullable MediaLibraryService.LibraryParams params
    ) {
        return MediaLibraryService.MediaLibrarySession.Callback.super.onGetChildren(session, browser, parentId, page, pageSize, params);
    }

    @Override
    public ListenableFuture<List<MediaItem>> onAddMediaItems(
            MediaSession mediaSession,
            MediaSession.ControllerInfo controller,
            List<MediaItem> mediaItems
    ) {
        return MediaLibraryService.MediaLibrarySession.Callback.super.onAddMediaItems(mediaSession, controller, mediaItems);
    }
}
