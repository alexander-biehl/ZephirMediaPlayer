package com.alexanderbiehl.apps.zephirmediaplayer.dataloaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreLoader {

    private static final String TAG = MediaStoreLoader.class.getSimpleName();

    private static final String[] PROJECTION = {
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER
    };
    private static final String BASE_SELECTION = String.format("%s = ?",
            MediaStore.Audio.AudioColumns.IS_MUSIC);

    private static final String[] SELECTION_ARGS = {
            "1" // TRUE
    };

    public List<MediaItem> getMedia(@NonNull Context ctx) {
        List<MediaItem> media = new ArrayList<>();

        ContentResolver resolver = ctx.getContentResolver();
        try (Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                BASE_SELECTION,
                SELECTION_ARGS,
                null)) {
            if (cursor == null) {
                Log.d(TAG, "Cursor returned null");
            } else if (!cursor.moveToNext()) {
                Log.d(TAG, "No media on device");
            } else {
                // cache the column numbers
                int mediaIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
                int orderColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER);

                do {
                    final long id = cursor.getLong(mediaIdColumn);
                    final String album = cursor.getString(albumColumn);
                    final String artist = cursor.getString(artistColumn);
                    final String title = cursor.getString(titleColumn);
                    final Uri uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                    );
                    final int order = cursor.getInt(orderColumn);

                    try {
                        media.add(
                                new MediaItem.Builder()
                                        .setMediaId(String.valueOf(id))
                                        .setUri(uri)
                                        .setMediaMetadata(
                                                new MediaMetadata.Builder()
                                                        .setTitle(title)
                                                        .setArtist(artist)
                                                        .setAlbumTitle(album)
                                                        .setTrackNumber(order)
                                                        .build()
                                        )
                                        .build()
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "EXCEPTION: " + e);
                        throw new RuntimeException(e);
                    }
                } while (cursor.moveToNext());
            }
        }
        return media;
    }
}
