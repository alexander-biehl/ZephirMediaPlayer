package com.alexanderbiehl.apps.zephirmediaplayer.dataloaders;

import static androidx.media3.common.MediaMetadata.PICTURE_TYPE_FRONT_COVER;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MediaStoreLoader {

    private static final String TAG = MediaStoreLoader.class.getSimpleName();

    private static final String[] PROJECTION = {
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
            MediaStore.Audio.AudioColumns.ALBUM_ID
    };
    private static final String BASE_SELECTION = String.format("%s = ?",
            MediaStore.Audio.AudioColumns.IS_MUSIC);

    private static final String[] SELECTION_ARGS = {
            "1" // TRUE
    };

    private static final String[] ART_PROJECTION = {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM_ART
    };

    private static final String ART_SELECTION = String.format("%s = ?",
            MediaStore.Audio.Albums._ID);

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
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID);

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
                    final String albumID = cursor.getString(albumIdColumn);
                    Uri albumArtUri = getAlbumArt(ctx, albumID);
                    Bitmap art = null;
                    if (albumArtUri == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            art = getAlbumArt(resolver, uri);
                        }
                    }

                    try {
                        MediaMetadata.Builder builder = new MediaMetadata.Builder()
                                .setTitle(title)
                                .setArtist(artist)
                                .setAlbumTitle(album)
                                .setTrackNumber(order);
                        if (albumArtUri != null) {
                            builder.setArtworkUri(albumArtUri);
                        } else if (art != null) {
                            builder.setArtworkData(convertBitmapToByteArray(art), PICTURE_TYPE_FRONT_COVER);
                        }
                        media.add(
                                new MediaItem.Builder()
                                        .setMediaId(String.valueOf(id))
                                        .setUri(uri)
                                        .setMediaMetadata(
                                                builder.build()
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

    public Uri getAlbumArt(@NonNull Context context, String albumID) {
        ContentResolver resolver = context.getContentResolver();

        final String[] selectionArgs = new String[]{
                albumID
        };

        try (Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                ART_PROJECTION,
                ART_SELECTION,
                selectionArgs,
                null)) {
            if (cursor == null) {
                Log.d(TAG, "Cursor returned null");
            } else if (!cursor.moveToNext()) {
                Log.d(TAG, "No Art found");
            } else {
                final String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));

                if (path == null) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Album art URI for " + albumID + " was null");
                    }
                    return null;
                }
                return ContentUris.withAppendedId(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        Long.parseLong(path)
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
            throw new RuntimeException(e);
        }
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    public Bitmap getAlbumArt(ContentResolver resolver, Uri uri) {
        try {
            return resolver.loadThumbnail(uri, new Size(300, 300), null);
        } catch (IOException e) {
            Log.e(TAG, "Unable to load bitmap with loadThumbnail: " + e);
        }
        return null;
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        buffer.rewind();
        return buffer.array();
    }
}
