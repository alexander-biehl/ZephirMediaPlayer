package com.curiositas.apps.zephirmediaplayer.dataloaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.ArrayList;

public class SongLoader {

    private static final String TAG = SongLoader.class.getSimpleName();

    public static ArrayList<Song> getAllSongs(Context context) {
        ArrayList<Song> songList = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        // define the location that we want to retrieve the media from
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // define which columns we want
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM
        };
        Cursor cursor = resolver.query(mediaUri, projection, null, null, null);
        if (cursor == null) {
            Log.d(TAG, "The song cursor returned null");
        } else if (!cursor.moveToFirst()) {
            // no media on device
            Log.d(TAG, "No media on device");
        } else {
            // cache the column numbers so we don't have to re-query for them
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            do {
                long id = cursor.getLong(idColumn);
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String album = cursor.getString(albumColumn);

                songList.add(new Song(uri, title, artist, album));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songList;
    }
}
