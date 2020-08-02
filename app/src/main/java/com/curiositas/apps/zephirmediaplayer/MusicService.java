package com.curiositas.apps.zephirmediaplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songList;
    private int songPosition;
    private final IBinder musicBind = new MusicBinder();

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
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * Once the MediaPlayer is prepared, the onPrepared method with be automatically called
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // start playback
        mp.start();
    }

    /**
     * Setter method to pass in a list of songs
     * @param songList
     */
    public void setList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    /**
     * This will be part of the interaction between our Activity
     * and this Service.
     */
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        player.reset();
        // Next, get the song from the list, extract the ID for it using its Song object
        // and model it as a URI
        Song playSong = songList.get(songPosition);
        // get the songs ID
        long currSong = playSong.getID();
        // set the URI
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong
        );
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MusicService", "Error setting the data source", e);
        }
        player.prepareAsync();
    }

    /**
     * This will be called when the user selects a song from the list
     * @param songIndex
     */
    public void setSong(int songIndex) {
        songPosition = songIndex;
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int position) {
        player.seekTo(position);
    }

    public void go() {
        player.start();
    }

    /**
     * Goes back to play the previous song
     */
    public void playPrev() {
        songPosition--;
        if (songPosition <= 0) {
            songPosition = songList.size() - 1;
        }
        playSong();
    }

    /**
     * Play the next song in the list
     */
    public void playNext() {
        songPosition++;
        if (songPosition >= songList.size()) {
            songPosition = 0;
        }
        playSong();
    }
}
