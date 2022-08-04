package com.curiositas.apps.zephirmediaplayer.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.List;

public class MusicService2 extends MediaBrowserServiceCompat {

    private static final String TAG = MusicService2.class.getSimpleName();

    private MediaSessionCompat mediaSession;
    private PlaybackManager playbackManager;

    final MediaSessionCompat.Callback sessionCallbacks = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            //super.onPlay();
            if (playbackManager.getCurrentMediaId() != null) {
                onPlayFromMediaId(playbackManager.getCurrentMediaId(), null);
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            //super.onPlayFromMediaId(mediaId, extras);
            mediaSession.setActive(true);
            MediaMetadataCompat metadata = MusicLibrary.getMetadata(MusicService2.this, mediaId);
            mediaSession.setMetadata(metadata);
            playbackManager.play(metadata);
        }

        @Override
        public void onPause() {
           //super.onPause();
            playbackManager.pause();
        }

        @Override
        public void onSkipToNext() {
            //super.onSkipToNext();
            onPlayFromMediaId(MusicLibrary.getNextSong(playbackManager.getCurrentMediaId()), null);
        }

        @Override
        public void onSkipToPrevious() {
            //super.onSkipToPrevious();
            onPlayFromMediaId(MusicLibrary.getPreviousSong(playbackManager.getCurrentMediaId()), null);
        }

        @Override
        public void onStop() {
            super.onStop();
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // start new mediaSession
        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setCallback(sessionCallbacks);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                mediaButtonIntent,
                0);
        mediaSession.setMediaButtonReceiver(pendingIntent);
        setSessionToken(mediaSession.getSessionToken());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playbackManager.stop();
        mediaSession.release();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(MusicLibrary.getMediaItems());
    }
}
