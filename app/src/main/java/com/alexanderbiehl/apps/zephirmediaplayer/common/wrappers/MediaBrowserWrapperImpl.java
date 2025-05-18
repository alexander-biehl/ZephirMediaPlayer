package com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.MediaLibraryService;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public class MediaBrowserWrapperImpl implements MediaBrowserWrapper {

    private final MediaBrowser mediaBrowser;

    public MediaBrowserWrapperImpl(@NonNull MediaBrowser mediaBrowser) {
        this.mediaBrowser = mediaBrowser;
    }

    @Override
    public void addMediaItems(List<MediaItem> mediaItems) {
        mediaBrowser.addMediaItems(mediaItems);
    }

    @Override
    public void addMediaItems(int index, List<MediaItem> mediaItem) {
        mediaBrowser.addMediaItems(index, mediaItem);
    }

    @Override
    public void addMediaItem(int index, MediaItem mediaItem) {
        mediaBrowser.addMediaItem(index, mediaItem);
    }

    @Override
    public void prepare() {
        mediaBrowser.prepare();
    }

    @Override
    public void play() {
        mediaBrowser.play();
    }

    @Override
    public void pause() {
        mediaBrowser.pause();
    }

    @Override
    public void stop() {
        mediaBrowser.stop();
    }

    @Override
    public void release() {
        mediaBrowser.release();
    }

    @Override
    public int getMediaItemCount() {
        return mediaBrowser.getMediaItemCount();
    }

    @Override
    public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> getChildren(
            @NonNull String parentId,
            int page,
            int pageSize,
            MediaLibraryService.LibraryParams params) {
        return mediaBrowser.getChildren(parentId, page, pageSize, params);
    }


}
