package com.curiositas.apps.zephirmediaplayer.service;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;

import java.io.IOException;

public class PlaybackManager implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener {

    private static final String TAG = PlaybackManager.class.getSimpleName();

    private final Context context;
    private int state;
    private boolean playOnFocusGain;
    private volatile MediaMetadataCompat currentMetadata;

    private MediaPlayer mediaPlayer;
    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;

    private final Callback callback;
    private final AudioManager audioManager;

    public PlaybackManager(@NonNull Context context,Callback callback) {
        this.context = context;
        this.callback = callback;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // init audioFocusRequest
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

    public boolean isPlaying() {
        return playOnFocusGain || (mediaPlayer != null && mediaPlayer.isPlaying());
    }

    public MediaMetadataCompat getCurrentMedia() {
        return currentMetadata;
    }

    public String getCurrentMediaId() {
        return currentMetadata == null ? null : currentMetadata.getDescription().getMediaId();
    }

    public int getCurrentStreamPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public void play(MediaMetadataCompat metadata) {
        String mediaId = metadata.getDescription().getMediaId();
        boolean mediaChanged = (currentMetadata == null || !getCurrentMediaId().equals(mediaId));

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            //mediaPlayer.setAudioStreamType();
            mediaPlayer.setWakeMode(context.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnCompletionListener(this);
        } else {
            if (mediaChanged) {
                mediaPlayer.reset();
            }
        }

        if (mediaChanged) {
            currentMetadata = metadata;
            try {
                mediaPlayer.setDataSource(context.getApplicationContext(),
                        Uri.parse(MusicLibrary.getSongUri(mediaId)));
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (audioFocusGained()) {
            playOnFocusGain = false;
            mediaPlayer.start();
            state = PlaybackStateCompat.STATE_PLAYING;
            updatePlaybackState();
        } else {
            playOnFocusGain = true;
        }
    }

    public void pause() {
        if (isPlaying()) {
            mediaPlayer.pause();
            audioManager.abandonAudioFocus(this);
        }
        state = PlaybackStateCompat.STATE_PAUSED;
        updatePlaybackState();
    }

    public void stop() {
        state = PlaybackStateCompat.STATE_STOPPED;
        updatePlaybackState();
        // give up audio focus
        audioManager.abandonAudioFocusRequest(audioFocusRequest);
        // release resources
        releaseMediaPlayer();
    }

    private boolean audioFocusGained() {
        return audioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        boolean gotFullFocus = false;
        boolean canDuck = false;
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            gotFullFocus = true;
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
        }

        if (gotFullFocus || canDuck) {
            if (mediaPlayer != null) {
                if (playOnFocusGain) {
                    playOnFocusGain = false;
                    mediaPlayer.start();
                    state = PlaybackStateCompat.STATE_PLAYING;
                    updatePlaybackState();
                }
                float volume = canDuck ? 0.2f : 1.0f;
                mediaPlayer.setVolume(volume, volume);
            }
        } else if (state == PlaybackStateCompat.STATE_PLAYING) {
            mediaPlayer.pause();
            state = PlaybackStateCompat.STATE_PAUSED;
            updatePlaybackState();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stop();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT  | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        if (isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    private void updatePlaybackState() {
        if (callback == null) {
            return;
        }
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions())
                .setState(state, 0l, SystemClock.elapsedRealtime());

    }

    public interface Callback {
        void onPlaybackStatusChanged(PlaybackStateCompat state);
    }
}
