package com.curiositas.apps.zephirmediaplayer.dataloaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.curiositas.apps.zephirmediaplayer.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediaLoader {

    private static final String TAG = MediaLoader.class.getSimpleName();

    private static final String[] PROJECTION = {
        MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.GENRE,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
    };

    public static final String[] BASE_PROJECTION = {
            //BaseColumns._ID,
            MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
            MediaMetadataCompat.METADATA_KEY_ALBUM,
            MediaMetadataCompat.METADATA_KEY_ARTIST,
            MediaMetadataCompat.METADATA_KEY_DURATION,
            MediaMetadataCompat.METADATA_KEY_GENRE,
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
            MediaMetadataCompat.METADATA_KEY_TITLE,
    };
    private static final String BASE_SELECTION = String.format("%s = ? AND %s != ?",
            MediaStore.Audio.AudioColumns.IS_MUSIC,
            MediaStore.Audio.AudioColumns.TITLE);
//    private static final String[] SELECTION_ARGS = {
//            "1",
//            "''"
//    };
//    private static final String BASE_SELECTION = "=1 AND %s!=''";
//    private static final String[] SELECTION_ARGS = {
//            MediaStore.Audio.AudioColumns.IS_MUSIC,
//            MediaStore.Audio.AudioColumns.TITLE
//    };

    public static List<MediaMetadataCompat> getMedia(@NonNull Context ctx) {
        List<MediaMetadataCompat> media = new ArrayList<>();

        ContentResolver resolver = ctx.getContentResolver();
        try (Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                null,
                null,
                null)) {
            if (cursor == null) {
                Log.d(TAG, "Cursor returned null");
            } else if (!cursor.moveToNext()) {
                Log.d(TAG, "No media on device");
            } else {
                // cache the column numbers
                //int idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                int mediaIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION);
                int genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.GENRE);
                //int albumArtColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);

                // temp solution
                Random rand = new Random();
                do {
                    //final long _id = cursor.getLong(idColumn);
                    final long _id = rand.nextLong();
                    final String mediaId = cursor.getString(mediaIdColumn);
                    final String album = cursor.getString(albumColumn);
                    final String artist = cursor.getString(artistColumn);
                    final long duration = cursor.getLong(durationColumn);
                    final String genre = cursor.getString(genreColumn);
                    //final String albumArtUri = cursor.getString(albumArtColumn);
                    final String title = cursor.getString(titleColumn);

                    media.add(
                            new MediaMetadataCompat.Builder()
                                    .putLong(BaseColumns._ID, _id)
                                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                                    .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                                    //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getAlbumArtUri(albumArtUri))
                                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                                    .build()
                    );
                } while (cursor.moveToNext());
            }
        }
        return media;
    }

    private static String getAlbumArtUri(String albumArtResName) {
        return String.format("android.resource://%s/drawable/%s",
                BuildConfig.APPLICATION_ID,
                albumArtResName);
    }
}
