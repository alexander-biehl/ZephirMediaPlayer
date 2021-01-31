package com.curiositas.apps.zephirmediaplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat implements
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MusicService";

    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(getApplicationContext(), TAG);

        // Disabled because these 2 flags have been depreciated and are set
        // by default
        // Enable callbacks from MediaButtons and TransportControls
        /*mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );*/

        // set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                );
        mediaSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        //mediaSession.setCallback(new MySessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());
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
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        // Browsing not allowed
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
            return;
        }

        // Assume for example that the music catalog is already loaded/cached

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        //NOTE: MediaItem objects delivered by the MediaBrowserService should not contain icon
        // bitmaps. Use a Uri instead by calling setIconUri(...) when you build the MediaDescription
        // for each item
        // Check if this is the root menu.
        if (MY_MEDIA_ROOT_ID.equals(parentId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list.
        }
        result.sendResult(mediaItems);
    }

    private boolean allowBrowsing(@NonNull String clientPackageName, int clientUid) {
        // logic for verification goes here. Note: this should return quickly with a non-null
        // value. User authentication and other slow processes should not run in onGetRoot().
        // Most business logic should be handled in onLoadChildren() method.
        return true;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    // Callback to the broadcast reciever that listens for changes in the
    // headphone state.
    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    };

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }
    };

}
