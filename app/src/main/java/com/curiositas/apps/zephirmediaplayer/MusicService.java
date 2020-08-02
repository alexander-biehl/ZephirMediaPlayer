package com.curiositas.apps.zephirmediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songList;
    private int songPosition;

    @Override
    public void onCreate() {
        // create the service
        super.onCreate();
        // initialize the song position
        this.songPosition = 0;
        // create player
        this.player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        this.player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        this.player.setOnPreparedListener(this);
        this.player.setOnCompletionListener(this);
        this.player.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
