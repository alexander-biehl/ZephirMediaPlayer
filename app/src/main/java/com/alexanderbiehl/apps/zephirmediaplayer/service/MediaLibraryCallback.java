package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Context;
import android.provider.MediaStore;
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
import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.MediaLocalDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.MediaRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MediaLibraryCallback implements MediaLibraryService.MediaLibrarySession.Callback {

    private static final String TAG = MediaLibraryCallback.class.getSimpleName();
    private final Context context;

    public MediaLibraryCallback(@NonNull final Context context, @NonNull final MainApp mainApp) {
        this.context = context;
        MediaRepository repo = new MediaRepository(
                new MediaLocalDataSource(
                        new MediaLoader(),
                        mainApp.getExec(),
                        context
                ));
        repo.getMedia((result) -> {
            if (result instanceof Result.Success) {
                MediaItemTree.getInstance().initialize(((Result.Success<List<MediaItem>>) result).data);
            } else {
                Log.e(TAG, "There was an error receiving media: " +
                        ((Result.Error<?>)result).ex.toString());
            }
        });
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
