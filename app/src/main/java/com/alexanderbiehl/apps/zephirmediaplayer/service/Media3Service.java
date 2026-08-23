package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;

import com.alexanderbiehl.apps.zephirmediaplayer.service.callback.MediaLibraryCallback;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Media3Service extends MediaLibraryService {

    @Inject
    MediaLibraryCallback mediaLibraryCallback;

    private MediaLibrarySession mediaLibrarySession;

    @Override
    public void onCreate() {
        super.onCreate();

        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaLibrarySession = new MediaLibrarySession.Builder(
                this,
                player,
                mediaLibraryCallback
        ).build();
    }

    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        Player player = mediaLibrarySession.getPlayer();
        if (!player.getPlayWhenReady() ||
                player.getMediaItemCount() == 0 ||
                player.getPlaybackState() == Player.STATE_ENDED) {
            // stop the service if not playing, continue playing
            // in the background otherwise
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        mediaLibrarySession.getPlayer().release();
        mediaLibrarySession.release();
        mediaLibrarySession = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
    }
}