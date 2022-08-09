package com.curiositas.apps.zephirmediaplayer.service;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;

import com.curiositas.apps.zephirmediaplayer.BuildConfig;
import com.curiositas.apps.zephirmediaplayer.repositories.MediaRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class MusicLibrary {

    private static final String ROOT = "__root__";

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final Object musicLock = new Object();
    private static final Map<String, Integer> albumResourceMap = new HashMap<>();
    private static final Map<String, Integer> musicResourceMap = new HashMap<>();
    private static final List<Callback> listenerCallbacks = new ArrayList<>();
    private boolean isReady;
    private final MediaRepository.Callback repoCallback = () -> loadIfReady();

    private static MusicLibrary INSTANCE;
    private static MediaRepository mediaRepository;

    private MusicLibrary(Application application) {
        isReady = false;
        mediaRepository = new MediaRepository(application);
        mediaRepository.subscribe(repoCallback);
        loadIfReady();
    }

    public void loadIfReady() {
        if (mediaRepository.isReady()) {
            synchronized (musicLock) {
                for (MediaMetadataCompat metadata : mediaRepository.getMedia()) {
                    music.put(metadata.getDescription().getMediaId(), metadata);
                    //musicResourceMap.put(metadata.)
                }
                isReady = true;
                notifyListeners();
            }
        }
    }

    public static synchronized MusicLibrary getInstance(Application app) {
        if (INSTANCE == null) {
            INSTANCE = new MusicLibrary(app);
        }
        return INSTANCE;
    }

    public void subscribe(Callback callback) {
        if (!listenerCallbacks.contains(callback)) {
            listenerCallbacks.add(callback);
        }
    }

    public void unsubscribe(Callback callback) {
        if (listenerCallbacks.contains(callback)) {
            listenerCallbacks.remove(callback);
        }
    }

    private void notifyListeners() {
        for (final Callback callback : listenerCallbacks) {
            callback.onReady(true);
        }
    }

    public static String getRoot() {
        return ROOT;
    }

    public boolean getIsReady() {
        return isReady;
    }

    public static String getSongUri(String mediaId) {
        return String.format("android.resource://%s/%o",
                BuildConfig.APPLICATION_ID,
                getMusicRes(mediaId));
    }

    private static String getAlbumArtUri(String albumArtResName) {
        return String.format("android.resource://%s/drawable/%s",
                BuildConfig.APPLICATION_ID,
                albumArtResName);
    }

    private static int getMusicRes(String mediaId) {
        return musicResourceMap.containsKey(mediaId) ? musicResourceMap.get(mediaId) : 0;
    }

    private static int getAlbumRes(String mediaId) {
        return albumResourceMap.containsKey(mediaId) ? albumResourceMap.get(mediaId) : 0;
    }

    public static Bitmap getAlbumBitmap(@NonNull Context context,String mediaId) {
        return BitmapFactory.decodeResource(
                context.getResources(),
                getAlbumRes(mediaId)
        );
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        synchronized (musicLock) {
            for (MediaMetadataCompat metadata : music.values()) {
                result.add(new MediaBrowserCompat.MediaItem(metadata.getDescription(),
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
        }
        return result;
    }

    public static String getPreviousSong(String currentMediaId) {
        String nextMediaId;
        synchronized (musicLock) {
            nextMediaId = music.lowerKey(currentMediaId);
        }
        if (nextMediaId == null) {
            synchronized (musicLock) {
                nextMediaId = music.firstKey();
            }
        }
        return nextMediaId;
    }

    public static String getNextSong(String currentMediaId) {
        String nextMediaId;
        synchronized (musicLock) {
            nextMediaId = music.higherKey(currentMediaId);
        }
        if (nextMediaId == null) {
            synchronized (musicLock) {
                nextMediaId = music.firstKey();
            }
        }
        return nextMediaId;
    }

    public static MediaMetadataCompat getMetadata(@NonNull Context ctx, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap;
        synchronized (musicLock) {
            metadataWithoutBitmap = music.get(mediaId);
        }
        Bitmap albumArt = getAlbumBitmap(ctx, mediaId);
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key: new String[]{MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
        MediaMetadataCompat.METADATA_KEY_ALBUM,
        MediaMetadataCompat.METADATA_KEY_ARTIST,
        MediaMetadataCompat.METADATA_KEY_GENRE,
        MediaMetadataCompat.METADATA_KEY_TITLE}) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        );
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    private static void createMediaMetadata(String mediaId,
                                            String title,
                                            String artist,
                                            String album,
                                            String genre,
                                            long duration,
                                            int musicResId,
                                            int albumArtResId,
                                            String albumArtResName) {
        music.put(mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration * 1000)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getAlbumArtUri(albumArtResName))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, getAlbumArtUri(albumArtResName))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build());
        albumResourceMap.put(mediaId, albumArtResId);
        musicResourceMap.put(mediaId, musicResId);
    }

    public void release() {
        mediaRepository.unsubscribe(repoCallback);
    }

    public interface Callback {
        void onReady(boolean isReady);
    }
}
