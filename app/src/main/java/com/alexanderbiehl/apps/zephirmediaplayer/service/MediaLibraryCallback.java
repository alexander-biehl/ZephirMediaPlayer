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
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaLibraryCallback extends Observable.OnPropertyChangedCallback implements MediaLibraryService.MediaLibrarySession.Callback {

    private static final String TAG = MediaLibraryCallback.class.getSimpleName();
    private final Context context;
    private final MainApp mainApp;
    private final CompositeMediaRepository repository;


    public MediaLibraryCallback(@NonNull final Context context, @NonNull final MainApp mainApp) {
        this.context = context;
        this.mainApp = mainApp;
        this.repository = new CompositeMediaRepository(AppDatabase.getDatabase(context));

        if (this.mainApp.getStoreIsSynced().get()) {
            initializeData();
        } else {
            this.mainApp.getStoreIsSynced().addOnPropertyChangedCallback(this);
        }
    }

    private void initializeData() {
//        MediaRepository repo = new MediaRepository(
//                new MediaDbDataSource(
//                        AppDatabase.getDatabase(context),
//                        mainApp.getExec()
//                )
//        );

        /*repo.getMedia((result) -> {
            if (result instanceof Result.Success) {
                // TODO this implementation does not work with large amounts of media
                // TODO instead of loading everything at once, load all data into the db,
                // TODO and then just query what we need from db
                MediaItemTree.getInstance().initialize(((Result.Success<List<MediaItem>>) result).data);
            } else {
                Log.e(TAG, "There was an error receiving media: " +
                        ((Result.Error<?>) result).ex.toString());
            }
        });*/
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
                        LibraryResult.ofItem(MediaItemTree.getInstance().getRootItem(), params),
                this.mainApp.getExec());
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
        return Futures.submit(() -> resolveMediaItems(mediaItems), this.mainApp.getExec());
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


    @Override
    public void onPropertyChanged(Observable sender, int propertyId) {
        initializeData();
        // remove the listener since it's only for the initial load
        this.mainApp.getStoreIsSynced().removeOnPropertyChangedCallback(this);
    }
}
