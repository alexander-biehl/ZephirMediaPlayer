package com.alexanderbiehl.apps.zephirmediaplayer.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alexanderbiehl.apps.zephirmediaplayer.Constants;
import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.service.MusicService;
import com.alexanderbiehl.apps.zephirmediaplayer.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity implements
SeekBar.OnSeekBarChangeListener {
//        extends Activity
//implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = MusicPlayerActivity.class.getSimpleName();

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;

    private MediaBrowserCompat mediaBrowser;
    // Media Player
    //private  MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
    //private SongManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        // All player buttons
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                connectionCallbacks, null);
        // MediaPlayer
        //mp = new MediaPlayer();
        //songManager = new SongManager();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        //mp.setOnCompletionListener(this); // Important

        // Getting all songs list
        //songsList = songManager.getPlayList();

        // By default play first song
        //playSong(0);

        initClickHandlers();

        // retrieve extras from intent
        Intent intent = getIntent();
        String uriStr = intent.getStringExtra(Constants.URI_EXTRA);
        if (uriStr != null) {
            Log.d(TAG, "Retrieved URI String: " + uriStr);
        } else {
            Log.d(TAG, "Unable to retrieve URI string");
        }
    }

    private void initClickHandlers() {
        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for if already playing
//                if (mp.isPlaying()) {
//                    if (mp != null) {
//                        mp.pause();
//                        // changing button image to play button
//                        btnPlay.setImageResource(R.drawable.btn_play);
//                    }
//                } else {
//                    // Resume song
//                    if (mp != null) {
//                        mp.start();
//                        // changing button image to pause button
//                        btnPlay.setImageResource(R.drawable.btn_pause);
//                    }
//                }
            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                // get current song position
//                int currentPosition = mp.getCurrentPosition();
//                // check if seekForward time is lesser than song duration
//                if (currentPosition + seekForwardTime <= mp.getDuration()) {
//                    // forward song
//                    mp.seekTo(currentPosition + seekForwardTime);
//                } else {
//                    // forward to end position
//                    mp.seekTo(mp.getDuration());
//                }
            }
        });

        /**
         * Backwards button click event
         * Backward song to specified seconds
         */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
//                if (currentSongIndex < (songsList.size() - 1)) {
//                    playSong(currentSongIndex + 1);
//                    currentSongIndex++;
//                } else {
//                    // play first song
//                    playSong(0);
//                    currentSongIndex = 0;
//                }
            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                if (currentSongIndex > 0) {
//                    playSong(currentSongIndex - 1);
//                    currentSongIndex--;
//                } else {
//                    // play last song
//                    playSong(songsList.size() - 1);
//                    currentSongIndex = songsList.size() - 1;
//                }
            }
        });

        /**
         * Button click event for Repeat button
         */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                if (isRepeat) {
//                    isRepeat = false;
//                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
//                    btnRepeat.setImageResource(R.drawable.btn_repeat);
//                } else {
//                    // make repeat to true
//                    isRepeat = true;
//                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
//                    // make shuffle false
//                    isShuffle = false;
//                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
//                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
//                }
            }
        });

        /**
         * Button click event for Shuffle button
         * Enable shuffle flag to true
         */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //getMediaController().getTransportControls().playFromUri(Uri.parse());
//                if (isShuffle) {
//                    isShuffle = false;
//                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
//                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
//                } else {
//                    // make shuffle true
//                    isShuffle = true;
//                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
//                    // make repeat false
//                    isRepeat = false;
//                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
//                    btnRepeat.setImageResource(R.drawable.btn_repeat);
//                }
            }
        });

        /**
         * Buton click event for Play list click event
         * Launces list activity which displays list of songs
         */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });
    }

    /**
     *  Receiving song index from playlist view and play song
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            //playSong(currentSongIndex);
        }
    }

    /**
     * Function to play a song
     * @param songIndex
     */
//    public void playSong(int songIndex) {
//        // play song
//        try {
//
//            //TODO update this to use the MusicBrowserCompat instead of directly
//            // loading
//
//            mp.reset();
//            mp.setDataSource(songsList.get(songIndex).get("songPath"));
//            mp.prepare();
//            mp.start();
//            // Display song title
//            String songTitle = songsList.get(songIndex).get("songTitle");
//            songTitleLabel.setText(songTitle);
//
//            // Change Button Image to pause image
//            btnPlay.setImageResource(R.drawable.btn_pause);
//
//            // set progress bar value
//            songProgressBar.setProgress(0);
//            songProgressBar.setMax(100);
//
//            // update progress bar
//            updateProgressBar();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable Thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            // TODO fix these
            //long totalDuration = mp.getDuration();
            //long currentDuration = mp.getCurrentPosition();
            long totalDuration = 2L;
            long currentDuration = 1L;

            // Display total duration time
            songTotalDurationLabel.setText("" + utils.millisecondsToTimer(totalDuration));
            // display time completed playing
            songCurrentDurationLabel.setText("" + utils.millisecondsToTimer(currentDuration));

            // update progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            Log.d(TAG, ""+progress);
            songProgressBar.setProgress(progress);

            // run this thread after 100 milliseconds
            if (progress > 100) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        long pos = getMediaController().getPlaybackState().getBufferedPosition();
        //int totalDuration = mp.getDuration();
        //int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to position
        //mp.seekTo(currentPosition);
        getMediaController().getTransportControls().seekTo(pos);

        // update timer progress again
        updateProgressBar();
    }

//    @Override
//    public void onCompletion(MediaPlayer arg0) {
//        // check if repeat is On or OFF
//        if (isRepeat) {
//            // repeat is on play same song again
//            playSong(currentSongIndex);
//        } else if (isShuffle){
//            // shuffle is on - play random song
//            Random rand = new Random();
//            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
//            playSong(currentSongIndex);
//        } else {
//            // n repeat or shuffle, - play next song
//            if (currentSongIndex < (songsList.size() - 1)) {
//                playSong(currentSongIndex + 1);
//                currentSongIndex++;
//            } else {
//                // play first song
//                playSong(currentSongIndex = 0);
//            }
//        }
//    }

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
        mediaBrowser.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private MediaControllerCompat getController() {
        return MediaControllerCompat.getMediaController(this);
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {

                @Override
                public void onConnected() {
                    super.onConnected();
                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                    MediaControllerCompat mediaController =
                            new MediaControllerCompat(MusicPlayerActivity.this,
                                    token);

                    mediaController.registerCallback(controllerCallback);

                    MediaControllerCompat.setMediaController(MusicPlayerActivity.this, mediaController);

                    // buildTransportControls

                }

                @Override
                public void onConnectionSuspended() {
                    super.onConnectionSuspended();
                    Log.d(TAG, "onConnectionSuspended");
                }

                @Override
                public void onConnectionFailed() {
                    super.onConnectionFailed();
                    Log.e(TAG, "onConnectionFailed");
                }
            };

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onSessionDestroyed() {
                    super.onSessionDestroyed();
                    mediaBrowser.disconnect();
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    super.onQueueChanged(queue);
                }
            };
}
