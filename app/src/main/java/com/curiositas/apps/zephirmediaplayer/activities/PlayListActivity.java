package com.curiositas.apps.zephirmediaplayer.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.curiositas.apps.zephirmediaplayer.R;
import com.curiositas.apps.zephirmediaplayer.SongManager;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayListActivity extends ListActivity {

    //Songs List
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        ArrayList<HashMap<String,String>> songsListData =
                new ArrayList<HashMap<String, String>>();

        SongManager plm = new SongManager();
        // get all songs from sdcard
        this.songsList = plm.getPlayList();

        // loop through playlist
        for (int i = 0; i < songsList.size(); i++) {
            //create new HashMap
            HashMap<String, String> song = songsList.get(i);

            // adding HashList to arrayList
            songsListData.add(song);
        }

        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(this, songsListData,
                R.layout.playlist_item, new String[] {"songTitle"}, new int[] {R.id.songTitle});
        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting listitem index
                int songIndex = position;

                // Starting new intent
                Intent intent = new Intent(getApplicationContext(),
                        MusicPlayerActivity.class);
                // sending songIndex to PlayerActivity
                intent.putExtra("songIndex", songIndex);
                setResult(100, intent);
                // closing PlayListView
                finish();
            }
        });
    }
}
