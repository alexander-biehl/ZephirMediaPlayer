package com.curiositas.apps.zephirmediaplayer;

import android.os.Environment;

import com.curiositas.apps.zephirmediaplayer.utilities.StorageUtilities;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongManager {

    //SDCard path
    private String MEDIA_PATH;
    private ArrayList<HashMap<String, String>> songList = new ArrayList<HashMap<String,String>>();
    private StorageUtilities storageUtilities;

    // Constructor
    public SongManager() {

//        storageUtilities = new StorageUtilities();
//        if (storageUtilities.isExternalStorageAvailable()) {
//            MEDIA_PATH = new String(Environment.getExternalStorageDirectory().getParent());
//        } else {
//            MEDIA_PATH = new String(Environment.getDataDirectory().getPath());
//        }
        MEDIA_PATH = new String(Environment.getDownloadCacheDirectory().getPath());
    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in an ArrayList
     */
    public ArrayList<HashMap<String,String>> getPlayList() {
        File home = new File(MEDIA_PATH);

        File[] paths = home.listFiles(new FileExtensionFilter());
        if (paths != null && paths.length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String,String> song = new HashMap<String,String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // add each to SongList
                songList.add(song);
            }
        }
        // return songs list array
        return songList;
    }

    /**
     * Class to filter files which have .mp3 extension
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return true;
            //return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
