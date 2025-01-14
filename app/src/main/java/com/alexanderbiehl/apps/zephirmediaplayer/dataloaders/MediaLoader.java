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

public class MediaLoader {

    private static final String TAG = MediaLoader.class.getSimpleName();

    private static final String[] PROJECTION = {
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST
    };
    private static final String BASE_SELECTION = String.format("%s = ?",
            MediaStore.Audio.AudioColumns.IS_MUSIC);

    private static final String[] SELECTION_ARGS = {
            "1" // TRUE
    };

    public static List<MediaItem> getMedia(@NonNull Context ctx) {
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

                do {
                    final long id = cursor.getLong(mediaIdColumn);
//                    final String mediaId = cursor.getString(mediaIdColumn);
//                    final long _id = Long.getLong(mediaId);
                    final String album = cursor.getString(albumColumn);
                    final String artist = cursor.getString(artistColumn);
                    final String title = cursor.getString(titleColumn);
                    final Uri uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                    );

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
                                                        .build()
                                        )
                                        .build()
                                //                            new MediaMetadataCompat.Builder()
                                //                                    .putLong(BaseColumns._ID, _id)
                                //                                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                                //                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                                //                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                                //                                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                                //                                    .build()
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
