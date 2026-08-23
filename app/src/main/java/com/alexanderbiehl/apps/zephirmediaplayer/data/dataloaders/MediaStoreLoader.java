package com.alexanderbiehl.apps.zephirmediaplayer.data.dataloaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MediaStoreLoader {

    private static final String TAG = MediaStoreLoader.class.getSimpleName();

    @Inject
    public MediaStoreLoader() {
    }

    private static final String[] PROJECTION = {
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
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
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

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
                    final Long durationMs = cursor.getLong(durationColumn);

                    try {
                        MediaMetadata.Builder builder = new MediaMetadata.Builder()
                                .setTitle(title)
                                .setArtist(artist)
                                .setAlbumTitle(album)
                                .setTrackNumber(order)
                                .setDurationMs(durationMs);
                        // Only point artwork at an actual image. Falling back to the
                        // track's own audio URI here previously made players try to
                        // decode an audio file as artwork.
                        if (albumArtUri != null) {
                            builder.setArtworkUri(albumArtUri);
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

    /**
     * MediaStore.Audio.Albums.ALBUM_ART is a filesystem path, not a numeric id, and
     * scoped storage (API 29+) stops populating it for most apps - so this lookup is
     * only attempted on pre-Q devices. On API 29+ this returns null; resolving artwork
     * there requires ContentResolver#loadThumbnail(trackUri, ...) at display time
     * instead of a storable Uri.
     */
    @Nullable
    public Uri getAlbumArt(@NonNull Context context, String albumID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null;
        }

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
                final String path = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));

                if (path == null) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Album art path for " + albumID + " was null");
                    }
                    return null;
                }
                return Uri.fromFile(new File(path));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception loading album art for albumID " + albumID + ": " + e);
        }
        return null;
    }

//    public Long getTrackDurationMs(@NonNull Context context, String mediaId) {
//        ContentResolver resolver = context.getContentResolver();
//        final String[] selectionArgs = new String[]{
//                mediaId
//        };
//        try (Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[]{MediaStore.Audio.AudioColumns.DURATION},
//                String.format("%s = ?", MediaStore.Audio.AudioColumns._ID),
//                selectionArgs,
//                null)) {
//            if (cursor == null) {
//                Log.d(TAG, "Cursor returned null");
//            } else if (!cursor.moveToNext()) {
//                Log.d(TAG, "No media found for mediaId: " + mediaId);
//            } else {
//                return cursor.getLong(
//                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
//                );
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e);
//            throw new RuntimeException(e);
//        }
//        return 0L;
//    }
}
