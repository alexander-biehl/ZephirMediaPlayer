package com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public interface MediaBrowserWrapper {
    void addMediaItems(List<MediaItem> mediaItems);

    void addMediaItems(int index, List<MediaItem> mediaItem);

    void addMediaItem(int index, MediaItem mediaItem);

    void prepare();

    void play();

    void pause();

    void stop();

    void release();

    int getMediaItemCount();

    ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> getChildren(
            @NonNull String parentId,
            int page,
            int pageSize,
            MediaLibraryService.LibraryParams params);
}
