package com.curiositas.apps.zephirmediaplayer.activities;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.Bundle;

import com.curiositas.apps.zephirmediaplayer.service.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.media.AudioManagerCompat;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;

import com.curiositas.apps.zephirmediaplayer.R;

public class MediaPlayerActivity extends AppCompatActivity {

    private MediaBrowserCompat mediaBrowser;
    private ImageView playPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // create MediaBrowserServiceCompat
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                connectionCallbacks,
                null); // optional bundle
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the mediaSession")
        if (getController() != null) {
            getController().unregisterCallback(controllerCallback);
        }
        mediaBrowser.disconnect();
    }


    /**
     * Convenience method so that we don't have to write that whole line out a bunch
     * of times
     * @return {@code MediaControllerCompat} - the MediaController for this Activity
     */
    private MediaControllerCompat getController() {
        return MediaControllerCompat.getMediaController(MediaPlayerActivity.this);
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    // get the token for the MediaSession
                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                    // Create a MediaControllerCompat
                    MediaControllerCompat mediaController =
                            new MediaControllerCompat(MediaPlayerActivity.this, // context
                                    token);

                    // Save the controller
                    MediaControllerCompat.setMediaController(MediaPlayerActivity.this, mediaController);

                    // Finish building the UI
                    buildTransportControls();
                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                }
            };

    void buildTransportControls() {
        //Grab the view for the play/pause button
        playPause = (ImageView) findViewById(R.id.btn_play);

        // Attach a listener to the button
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SInce this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly
                int pbState = getController().getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    getController().getTransportControls().pause();
                } else {
                    getController().getTransportControls().play();
                }
            }
        });

        MediaControllerCompat mediaController = getController();

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                }
            };
}