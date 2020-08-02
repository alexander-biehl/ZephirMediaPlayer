package com.curiositas.apps.zephirmediaplayer.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

import com.curiositas.apps.zephirmediaplayer.MusicController;
import com.curiositas.apps.zephirmediaplayer.MusicService;
import com.curiositas.apps.zephirmediaplayer.R;
import com.curiositas.apps.zephirmediaplayer.SongAdapter;
import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private ArrayList<Song> songList;
    private ListView songView;

    private MusicController controller;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private boolean paused = false;
    private boolean playbackPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<>();
        getSongList();

        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        SongAdapter songAdapter = new SongAdapter(this, songList);
        songView.setAdapter(songAdapter);

        setController();
    }

    // connect to the music service
    private ServiceConnection musicConnection = new ServiceConnection() {

        /**
         * This callback defines what happens when the Activity instance has successfully connected
         * to the Service Instance.
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            // get the service
            musicService = binder.getService();
            // pass the song list
            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            // when the Activity instance starts, we want to create our Intent object if it doesn't
            // exist yet
            playIntent = new Intent(this, MusicService.class);
            // then we bind the ServiceConnection that we created to the Intent
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            // and start it up
            startService(playIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            // insures that the controller gets displayed to the user when the come back to the app
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicService.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicService = null;
                System.exit(0);
                break;
            case R.id.action_settings:
                // TODO
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setController() {
        controller = new MusicController(this);

        // set up the Prev and Next click listeners so it knows what to
        // do when the "Next" and "Previous" buttons are clicked
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        // set the controller to work on media playback in this app
        controller.setMediaPlayer(this);
        // set its anchor view to be the list of songs
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    /**
     * Tell the MusicService to play the next song
     */
    private void playNext() {
        musicService.playNext();
        // if user interacts with the play controls while the music is paused, the music player
        // can behave erratically
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    /**
     * Tell the MusicService to play the previous song
     */
    private void playPrev() {
        musicService.playPrev();
        // if user interacts with the play controls while the music is paused, the music player
        // can behave erratically
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    /**
     * Helper method to retrieve audio file information
     */
    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        // now iterate over the results
        if (musicCursor != null && musicCursor.moveToFirst()) {
            // get columns
            int titleColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.TITLE
            );
            int idColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media._ID
            );
            int artistColumn = musicCursor.getColumnIndex(
                    MediaStore.Audio.Media.ARTIST
            );
            // add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, title, artist));
            } while (musicCursor.moveToNext());
        }
    }

    public void songPicked(View view) {
        musicService.setSong(Integer.parseInt(view.getTag().toString()));
        musicService.playSong();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show();
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPlaying()) {
            return musicService.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound && musicService.isPlaying()) {
            return musicService.getPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicService != null && musicBound) {
            return musicService.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
