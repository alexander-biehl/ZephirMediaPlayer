package com.curiositas.apps.zephirmediaplayer.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.curiositas.apps.zephirmediaplayer.R;
import com.curiositas.apps.zephirmediaplayer.activities.ui.main.fragments.SongListFragment;

public class SongListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SongListFragment.newInstance())
                    .commitNow();
        }
    }
}