package com.alexanderbiehl.apps.zephirmediaplayer.activities;

import android.content.ComponentName;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexanderbiehl.apps.zephirmediaplayer.Constants;
import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main.MusicQueueViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.service.MusicService2;
import com.alexanderbiehl.apps.zephirmediaplayer.utilities.StorageUtilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private MediaBrowserCompat mediaBrowser;
    private MediaMetadataCompat currentMetadata;
    private PlaybackStateCompat currentState;
    public List<String> subscribedIds;
    public MusicQueueViewModel viewModel;

    final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            subscribedIds = new ArrayList<>();
            MediaControllerCompat mediaController =
                    new MediaControllerCompat(BaseActivity.this, mediaBrowser.getSessionToken());
            MediaControllerCompat.setMediaController(BaseActivity.this, mediaController);
            mediaController.registerCallback(controllerCallback);
            subscribeToID(mediaBrowser.getRoot());

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
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            //super.onPlaybackStateChanged(state);
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //super.onMetadataChanged(metadata);
            updateMetadata(metadata);
        }
    };

    final MediaBrowserCompat.SubscriptionCallback subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            //super.onChildrenLoaded(parentId, children);
            // TODO need to update this so it changes depending on the media_id submitted
            if (children == null || children.isEmpty()) {
                // TODO requiry
                new RetryTask(mediaBrowser).execute(parentId);
            } else {
                // happy path
                Toast.makeText(getApplicationContext(), "Childrend Loaded", Toast.LENGTH_SHORT);
                viewModel.setQueue(children);
                onMediaItemsLoaded();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService2.class),
                connectionCallback,
                null);

        viewModel = new ViewModelProvider(this).get(MusicQueueViewModel.class);
        viewModel.getCurrentMediaID().observe(this, currentMediaID -> {
            List<MediaBrowserCompat.MediaItem> items = viewModel
                    .getQueue()
                    .getValue()
                    .stream()
                    .filter(mItem -> { return mItem.getMediaId() == currentMediaID; })
                    .collect(Collectors.toList());

            if (!items.isEmpty()) {
                onMediaItemSelected(items.get(0));
            } else {
                Log.e(TAG, "MediaID " + currentMediaID + " not found.");
            }
        });
        // Check that we have the permissions that we need
        StorageUtilities.verifyStoragePermission(this);
        // TODO need to add callback because music in not retrieved first time app is
        // loaded
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
        if (mediaBrowser != null && mediaBrowser.isConnected()) {
            unsubscribeFromAll();
        }
        mediaBrowser.disconnect();
    }

    public void onMediaItemSelected(@Nullable MediaBrowserCompat.MediaItem mediaItem) {
        if (mediaItem != null && mediaItem.isPlayable()) {

            // navigate to the NowPlayingFragment
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MEDIA_KEY, mediaItem.getMediaId());
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
            navController.navigate(R.id.action_MediaList_to_NowPlaying, bundle);

            getMediaController()
                    .getTransportControls()
                    .playFromMediaId(mediaItem.getMediaId(), null);
        } else if (mediaItem != null){
            subscribeToID(mediaItem.getMediaId());
        } else {
            Log.e(TAG, "Passed in null mediaItem");
        }
    }

    abstract void onMediaItemsLoaded();

    protected void updatePlaybackState(PlaybackStateCompat state) {
        currentState = state;
        if (state == null || state.getState() == PlaybackStateCompat.STATE_PAUSED ||
        state.getState() == PlaybackStateCompat.STATE_STOPPED) {
            setPausedState();
        } else {
            setPlayingState();
        }
    }

    abstract void setPausedState();

    abstract void setPlayingState();

    protected void updateMetadata(MediaMetadataCompat metadata) {
        currentMetadata = metadata;
        updateMetadataImpl();
    }

    abstract void updateMetadataImpl();

    protected MediaControllerCompat getController() {
        return MediaControllerCompat.getMediaController(this);
    }

    protected void subscribeToID(String parentID) {
        if (!subscribedIds.contains(parentID)) {
            subscribedIds.add(parentID);
            mediaBrowser.subscribe(parentID, subscriptionCallback);
        }
    }

    private void unsubscribeFromAll() {
        for (String parentID : subscribedIds) {
            mediaBrowser.unsubscribe(parentID);
        }
    }

    private class RetryTask extends AsyncTask<String, Void, Void> {

        WeakReference<MediaBrowserCompat> mediaBrowser;

        RetryTask(MediaBrowserCompat mediaBrowser) {
            this.mediaBrowser = new WeakReference<>(mediaBrowser);
        }

        @Override
        protected Void doInBackground(String... strings) {
            mediaBrowser.get().unsubscribe(strings[0]);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mediaBrowser.get().subscribe(strings[0], subscriptionCallback);
            return null;
        }
    }
}