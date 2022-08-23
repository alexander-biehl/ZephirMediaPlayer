package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.utilities.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicService2 extends MediaBrowserServiceCompat {

    private static final String TAG = MusicService2.class.getSimpleName();

    private MediaSessionCompat mediaSession;
    private PlaybackManager playbackManager;
    private MusicLibrary library;
    private final MusicLibrary.Callback libraryCallback = new MusicLibrary.Callback() {
        @Override
        public void onReady(boolean isReady) {
            libraryReady = true;
        }
    };;
    private boolean isStarted;
    private boolean libraryReady;

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
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
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

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            super.onAddQueueItem(description);
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description, int index) {
            super.onAddQueueItem(description, index);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            super.onRemoveQueueItem(description);
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

        playbackManager = new PlaybackManager(this, state -> {
            mediaSession.setPlaybackState(state);
            updatePlaybackState(state);
        });
        isStarted = false;
        libraryReady = false;
        library = MusicLibrary.getInstance(getApplication());
        library.subscribe(libraryCallback);

        NotificationUtil.createNotificationChannel(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        library.unsubscribe(libraryCallback);
        library.release();
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
        if (!library.getIsReady()) {
            result.sendResult(new ArrayList<>());
        } else {
            result.sendResult(MusicLibrary.getItemsForId(parentId));
        }
    }

    private void updatePlaybackState(final PlaybackStateCompat state) {
        if (state == null || state.getState() == PlaybackStateCompat.STATE_STOPPED ||
        state.getState() == PlaybackStateCompat.STATE_PAUSED) {
            stopForeground(state.getState() == PlaybackStateCompat.STATE_STOPPED);
            stopSelf();
        }

        MediaMetadataCompat metadata = playbackManager.getCurrentMedia();
        if (metadata == null) {
            return;
        }

        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationUtil.CHANNEL_ID);
        MediaDescriptionCompat description = metadata.getDescription();

        builder
                .setChannelId(NotificationUtil.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(mediaSession.getController().getSessionActivity())
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                //.setLargeIcon(MusicLibrary.getAlbumBitmap(this, description.getMediaId()))
                .setOngoing(isPlaying)
                .setWhen(isPlaying ? System.currentTimeMillis() - state.getPosition() : 0)
                .setShowWhen(isPlaying);

        // if skip to previous action is enabled
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            builder.addAction(
                    new NotificationCompat.Action(
                            R.drawable.ic_notif_previous,
                            "Previous",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    )
            );
        }

        if (isPlaying) {
            builder.addAction(
                    new NotificationCompat.Action(
                            R.drawable.ic_notif_pause,
                            "Pause",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_PAUSE
                            )
                    )
            );
        } else {
            builder.addAction(
                    new NotificationCompat.Action(
                            R.drawable.ic_notif_play,
                            "Play",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_PLAY
                            )
                    )
            );
        }

        // if skip to next action is enabled
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            builder.addAction(
                    new NotificationCompat.Action(
                            R.drawable.ic_notif_next,
                            "Next",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                    )
            );
        }

        // set the style after adding the actions
        builder.setStyle(
                new androidx.media.app.NotificationCompat
                        .MediaStyle()
                        .setShowActionsInCompactView(1,0,2)
                        .setMediaSession(mediaSession.getSessionToken()));
        Notification notification = builder.build();

        if (isPlaying && !isStarted) {
            startService(new Intent(getApplicationContext(), MusicService2.class));
            startForeground(NotificationUtil.CHANNEL, notification);
        } else {
            if (!isPlaying) {
                stopForeground(false);
                isStarted = false;
            }
            NotificationManagerCompat.from(this).notify(NotificationUtil.CHANNEL, notification);
        }
    }
}
