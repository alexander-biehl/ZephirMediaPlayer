package com.alexanderbiehl.apps.zephirmediaplayer.util;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;

import com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers.MediaBrowserWrapper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public class TestMediaBrowser implements MediaBrowserWrapper {
    @Override
    public void addMediaItems(List<MediaItem> mediaItems) {

    }

    @Override
    public void addMediaItems(int index, List<MediaItem> mediaItem) {

    }

    @Override
    public void addMediaItem(int index, MediaItem mediaItem) {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void release() {

    }

    @Override
    public int getMediaItemCount() {
        return 0;
    }

    @Override
    public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> getChildren(@NonNull String parentId, int page, int pageSize, MediaLibraryService.LibraryParams params) {
        return null;
    }
}
