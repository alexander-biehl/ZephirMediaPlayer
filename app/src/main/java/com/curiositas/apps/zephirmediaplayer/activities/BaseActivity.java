package com.curiositas.apps.zephirmediaplayer.activities;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.curiositas.apps.zephirmediaplayer.service.MusicService2;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private MediaBrowserCompat mediaBrowser;
    private MediaMetadataCompat currentMetadata;
    private PlaybackStateCompat currentState;

    final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            mediaBrowser.subscribe(mediaBrowser.getRoot(), subscriptionCallback);
            MediaControllerCompat mediaController =
                    new MediaControllerCompat(BaseActivity.this, mediaBrowser.getSessionToken());
            MediaControllerCompat.setMediaController(BaseActivity.this, mediaController);
            mediaController.registerCallback(controllerCallback);
            Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_LONG);
            Log.d(TAG, "Connected");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Toast.makeText(getApplicationContext(), "Connection suspended!", Toast.LENGTH_LONG);
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT);
        }
    };

    final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionDestroyed() {
            //super.onSessionDestroyed();
            updatePlaybackState(null);
            //adapter.notifyDataSetChanged();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            //super.onPlaybackStateChanged(state);

            updatePlaybackState(state);
            //adapter.notifyDataSetChanged();
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //super.onMetadataChanged(metadata);
            updateMetadata(metadata);
            //adapter.notifyDatasetChanged();
        }
    };

    final MediaBrowserCompat.SubscriptionCallback subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            //super.onChildrenLoaded(parentId, children);
            // TODO notify that we've loaded the data
            Toast.makeText(getApplicationContext(), "Childrend Loaded", Toast.LENGTH_SHORT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService2.class),
                connectionCallback,
                null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getController() != null) {
            getController().unregisterCallback(controllerCallback);
        }
        if (mediaBrowser != null && mediaBrowser.isConnected() && currentMetadata != null) {
            mediaBrowser.unsubscribe(currentMetadata.getDescription().getMediaId());
        }
        mediaBrowser.disconnect();
    }

    protected void onMediaItemSelected(MediaBrowserCompat.MediaItem mediaItem) {
        if (mediaItem.isPlayable()) {
            getMediaController()
                    .getTransportControls()
                    .playFromMediaId(mediaItem.getMediaId(), null);
        }
    }

    protected void updatePlaybackState(PlaybackStateCompat state) {
        currentState = state;
        if (state == null || state.getState() == PlaybackStateCompat.STATE_PAUSED ||
        state.getState() == PlaybackStateCompat.STATE_STOPPED) {
            // todo make play button visible or decide how to communicate that
        } else {
            // TODO
        }
    }

    protected void updateMetadata(MediaMetadataCompat metadata) {
        currentMetadata = metadata;
        // TODO update metdata display of adapter
        // adapter.notifyDataSetChanged();
    }

    protected MediaControllerCompat getController() {
        return MediaControllerCompat.getMediaController(this);
    }
}