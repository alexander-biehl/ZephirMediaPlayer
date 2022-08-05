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

public class MediaLoader {

    private static final String TAG = MediaLoader.class.getSimpleName();

    public static final String[] BASE_PROJECTION = {
            BaseColumns._ID,
            MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
            MediaMetadataCompat.METADATA_KEY_ALBUM,
            MediaMetadataCompat.METADATA_KEY_ARTIST,
            MediaMetadataCompat.METADATA_KEY_DURATION,
            MediaMetadataCompat.METADATA_KEY_GENRE,
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
            MediaMetadataCompat.METADATA_KEY_TITLE,
    };
    private static final String BASE_SELECTION = String.format("%s=1 AND %s !=''",
            MediaStore.Audio.AudioColumns.IS_MUSIC,
            MediaStore.Audio.AudioColumns.TITLE);

    public static List<MediaMetadataCompat> getMedia(@NonNull Context ctx) {
        List<MediaMetadataCompat> media = new ArrayList<>();

        ContentResolver resolver = ctx.getContentResolver();
        try (Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                BASE_PROJECTION,
                BASE_SELECTION,
                null,
                null)) {
            if (cursor == null) {
                Log.d(TAG, "Cursor returned null");
            } else if (!cursor.moveToNext()) {
                Log.d(TAG, "No media on device");
            } else {
                // cache the column numbers
                int idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                int mediaIdColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_ALBUM);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_ARTIST);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_DURATION);
                int genreColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_GENRE);
                int albumArtColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaMetadataCompat.METADATA_KEY_TITLE);

                do {
                    final long _id = cursor.getLong(idColumn);
                    final String mediaId = cursor.getString(mediaIdColumn);
                    final String album = cursor.getString(albumColumn);
                    final String artist = cursor.getString(artistColumn);
                    final long duration = cursor.getLong(durationColumn);
                    final String genre = cursor.getString(genreColumn);
                    final String albumArtUri = cursor.getString(albumArtColumn);
                    final String title = cursor.getString(titleColumn);

                    media.add(
                            new MediaMetadataCompat.Builder()
                                    .putLong(BaseColumns._ID, _id)
                                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                                    .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getAlbumArtUri(albumArtUri))
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
