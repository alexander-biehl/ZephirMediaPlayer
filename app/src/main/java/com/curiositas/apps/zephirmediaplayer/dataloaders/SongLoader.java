package com.curiositas.apps.zephirmediaplayer.dataloaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.ArrayList;
import java.util.Date;

public class SongLoader {

    private SongLoader() {}

    private static final String TAG = SongLoader.class.getSimpleName();

    private static final String BASE_SELECTION = String.format("%s=1 AND %s !=''",
            MediaStore.Audio.AudioColumns.IS_MUSIC,
            MediaStore.Audio.AudioColumns.TITLE);
    private static final String[] BASE_PROJECTION = {
            BaseColumns._ID,// 0
            MediaStore.Audio.AudioColumns.TITLE,// 1
            MediaStore.Audio.AudioColumns.TRACK,// 2
            MediaStore.Audio.AudioColumns.YEAR,// 3
            MediaStore.Audio.AudioColumns.DURATION,// 4
            MediaStore.Audio.AudioColumns.DATA,// 5
            MediaStore.Audio.AudioColumns.DATE_ADDED,// 6
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 7
            MediaStore.Audio.AudioColumns.ALBUM_ID,// 8
            MediaStore.Audio.AudioColumns.ALBUM,// 9
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 10
            MediaStore.Audio.AudioColumns.ARTIST,// 11
    };

    public static ArrayList<Song> getAllSongs(Context context) {
        ArrayList<Song> songList = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        // define the location that we want to retrieve the media from
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = resolver.query(mediaUri, BASE_PROJECTION, BASE_SELECTION, null, null);
        if (cursor == null) {
            Log.d(TAG, "The song cursor returned null");
        } else if (!cursor.moveToFirst()) {
            // no media on device
            Log.d(TAG, "No media on device");
        } else {
            // cache the column numbers so we don't have to re-query for them
            int idColumn = cursor.getColumnIndex(BaseColumns._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK);
            int yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED);
            int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                final long id = cursor.getLong(idColumn);
                final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                final String title = cursor.getString(titleColumn);
                final String artist = cursor.getString(artistColumn);
                final String album = cursor.getString(albumColumn);
                final int trackNumber = cursor.getInt(trackColumn);
                final int year = cursor.getInt(yearColumn);
                final long duration = cursor.getLong(durationColumn);
                final String data = cursor.getString(dataColumn);
                final long dateAdded = cursor.getLong(dateAddedColumn);
                final long dateModified = cursor.getLong(dateModifiedColumn);
                final long albumId = cursor.getLong(albumIdColumn);
                final long artistId = cursor.getLong(artistIdColumn);

                songList.add(
                        new Song(id,
                                title,
                                trackNumber % 1000,
                                trackNumber / 1000,
                                duration,
                                data,
                                new Date(dateAdded),
                                new Date(dateModified),
                                albumId,
                                album,
                                artistId,
                                artist)
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songList;
    }
}
