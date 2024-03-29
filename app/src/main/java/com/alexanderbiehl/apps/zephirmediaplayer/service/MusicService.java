package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.SongManager;
import com.alexanderbiehl.apps.zephirmediaplayer.utilities.MediaStyleHelper;
import com.alexanderbiehl.apps.zephirmediaplayer.utilities.NotificationUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MusicService.class.getSimpleName();

    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    /**
     * ID That we use to keep track of our notification
     */
    private static final int NOTIF_ID = 1;

    private MediaPlayer mediaPlayer;
    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;
    private MediaSessionCompat mediaSession;
    private SongManager songManager;
    private MusicLibrary library;
    private final MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
        }

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);

        }

        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "mediaSessionCallback.onPlay called");
            startPlayback();
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "mediaSessionCallback.onPause called");

            pausePlayback();
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "mediaSessionCallback.onStop called");

            stopPlayback();
        }

        private void initMediaSessionMetadata() {
            //TODO
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            Log.d(TAG, "mediaSessionCallback.onPlayFromMediaId called.\nMediaId: " + mediaId);

            // String filename = "android.resource://" + getPackageName() + "/raw/use_me.mp3";
            try {
                AssetFileDescriptor afd = getResources().openRawResourceFd(Integer.parseInt(mediaId));
                //AssetFileDescriptor afd = getResources().
                if (afd == null) {
                    return;
                }

                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                } catch (IllegalStateException e) {
                    mediaPlayer.release();
                    initMediaPlayer();
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                }

                afd.close();
                //TODO figure out what media metadata needs to be set for each song
                // and how to set it
                initMediaSessionMetadata();
            } catch (IOException e) {
                return;
            }

//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//
//            }
            mediaPlayer.prepareAsync();

            // Work here with extras if needed
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            //TODO implement seekTo method to allow users to move to a specific position
        }
    };

    // Callback to the broadcast reciever that listens for changes in the
    // headphone state.
    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        songManager = new SongManager();
        List<HashMap<String,String>> songs = songManager.getPlayList();
        library = MusicLibrary.getInstance(getApplication());

        // TODO use the info from here https://developer.android.com/training/data-storage/shared/media#query-collection
        // and https://developer.android.com/training/data-storage/shared/media#check-for-updates
        // to check/load media items

        // TODO this link may be useful also:
        // https://developer.android.com/guide/topics/media/mediaplayer#viacontentresolver

        initAudioAttributesAndRequest();
        initMediaPlayer();
        initMediaSession();
        // initNoisyReceiver();
        initNotificationChannel();
    }

    /**
     * Initializes our AudioAttributes and AudioFocusRequest, which will be used
     * to request and check if our app has system audio focus.
     */
    private void initAudioAttributesAndRequest() {
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(this)
                .build();
    }

    /**
     * Initialize our MediaPlayer instance, including wakelocks, audio attributes, and
     * setting the OnPreparedListener
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioAttributes(audioAttributes);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setOnPreparedListener(this);
    }

    /**
     * Initialize our MediaSessionCompat object, which allows clients to connect to our
     * service, sets the mediaSessionCallback for communicating between the client and service,
     * as well as register a MediaButtonReceiver so that our service can receive MediaButton intents
     */
    private void initMediaSession() {
//        ComponentName mediaButtonReceiver = new ComponentName(
//                getApplicationContext(),
//                MediaButtonReceiver.class);

        mediaSession = new MediaSessionCompat(getApplicationContext(), TAG);
        mediaSession.setCallback(mediaSessionCallback);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);

        setSessionToken(mediaSession.getSessionToken());
    }

    private void initNoisyReceiver() {
        // Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, filter);
    }

    private void removeNoisyReceiver() {
        unregisterReceiver(noisyReceiver);
    }

    private void initNotificationChannel() {
        NotificationUtil.createNotificationChannel(getApplicationContext());
    }

    // This is the entrypoint of the service.  This method will take the Intent
    // that is passed to the Service and send it to the MediaButtonReceiver Class.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);

        // May end up wanting to replace this line with return START_STICKY
        // see https://developer.android.com/reference/android/app/Service#START_NOT_STICKY
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierarchy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        }
        //return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        // Browsing not allowed
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
            return;
        }

        // For now we just send null since this method is more important for auto apps
        // that need to browse the media we offer. Can look at implementing this later.
        result.sendResult(null);

        // List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        //NOTE: MediaItem objects delivered by the MediaBrowserService should not contain icon
        // bitmaps. Use a Uri instead by calling setIconUri(...) when you build the MediaDescription
        // for each item
        // Check if this is the root menu.
        // if (MY_MEDIA_ROOT_ID.equals(parentId)) {
        // Build the MediaItem objects for the top level,
        // and put them in the mediaItems list...
        // } else {
        // Examine the passed parentId to see which submenu we're at,
        // and put the children of that menu in the mediaItems list.
        // }
        // result.sendResult(mediaItems);
    }

    private boolean allowBrowsing(@NonNull String clientPackageName, int clientUid) {
        //TODO
        // logic for verification goes here. Note: this should return quickly with a non-null
        // value. User authentication and other slow processes should not run in onGetRoot().
        // Most business logic should be handled in onLoadChildren() method.
        return true;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocusRequest(audioFocusRequest);
        // unregisterReceiver(noisyReceiver);
        mediaSession.release();
        NotificationManagerCompat.from(this).cancel(NOTIF_ID);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (this.mediaPlayer != null) {
            //this.mediaPlayer.start();
            startPlayback();
        }
    }

    /**
     * Gets a reference to the system AudioManager, and attempts to request audio
     * focus for streaming music. Returns a boolean representing whether or not the request
     * succeeded
     *
     * @return true if it got audio focus, false otherwise
     */
    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(audioFocusRequest);

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * Updates the PlaybackStateCompat (used to represent that state of Playback to clients)
     * based on whether the passed in state parameter indicates that we want to be in a playing
     * state or paused state
     * @param state - integer representing the state that we would like to configure the Playback
     *              for
     */
    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackStateBuilder.setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_PAUSE
            );
        } else {
            playbackStateBuilder.setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_PLAY
            );
        }
        playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSession.setPlaybackState(playbackStateBuilder.build());
    }

    private void showPlayingNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(MusicService.this, mediaSession);
        if (builder == null) {
            return;
        }

        // add a Pause action to the notification
        builder.addAction(
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_pause,
                        "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        )
                )
        );
        builder.setStyle(
                new androidx.media.app.NotificationCompat
                        .MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSession.getSessionToken())
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        // NotificationManagerCompat.from(MusicService.this)
        // .notify(NOTIF_ID, builder.build());
        // put the service into the foreground
        startForeground(NOTIF_ID, builder.build());
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mediaSession);
        if (builder == null) {
            return;
        }

        builder.addAction(
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_play,
                        "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        )
                )
        );
        builder.setStyle(
                new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mediaSession.getSessionToken())
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }

    /**
     * Convenience method for pausing the mediaPlayer and handling notifications
     */
    private void pausePlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showPausedNotification();
            removeNoisyReceiver();
            // take the service out of the foreground, but leave the notification
            stopForeground(false);
        }
    }

    /**
     * Convenience method for starting the mediaPlayer and handling notifications
     */
    private void startPlayback() {
        // audio is handled by focus, which determines what app is making sounds currently.
        if (!successfullyRetrievedAudioFocus()) {
            return;
        }
        // insure our service is started
        startService(new Intent(getApplicationContext(), MusicService.class));
        // set our mediaSession to active so it is discoverable
        mediaSession.setActive(true);
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        showPlayingNotification();
        mediaPlayer.start();
        initNoisyReceiver();
    }

    /**
     * Convenience method for stopping the mediaPlayer and handling notifications
     */
    private void stopPlayback() {
        // stop the service so it's not taking up resources
        stopSelf();
        // set the mediaSession to inactive
        mediaSession.setActive(false);
        // stop the mediaPlayer
        mediaPlayer.stop();
        removeNoisyReceiver();
        // take the service out of the foreground
        stopForeground(false);
    }
}
