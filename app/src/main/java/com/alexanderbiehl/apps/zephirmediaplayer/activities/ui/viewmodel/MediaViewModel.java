package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaViewModel extends ViewModel {

    private final MutableLiveData<MediaItem> currentMedia;
    private final MutableLiveData<List<MediaItem>> currentQueue;

    public MediaViewModel() {
        this.currentMedia = new MutableLiveData<>();
        this.currentQueue = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<MediaItem>> getQueue() {
        return this.currentQueue;
    }

    public void setQueue(final List<MediaItem> queue) {
        this.currentQueue.setValue(queue);
    }

    public LiveData<MediaItem> getCurrentMedia() {
        return this.currentMedia;
    }

    public void setCurrentMedia(final MediaItem item) {
        this.currentMedia.setValue(item);
    }

    public synchronized void addToQueue(final MediaItem item) {
        List<MediaItem> updated = new ArrayList<>();
        List<MediaItem> current = currentQueue.getValue();
        if (current != null) {
            updated.addAll(current);
        }
        updated.add(item);
        this.currentQueue.setValue(updated);
    }

    public synchronized void addToQueue(final List<MediaItem> items) {
        List<MediaItem> updated = new ArrayList<>();
        List<MediaItem> current = currentQueue.getValue();
        if (current != null) {
            updated.addAll(current);
        }
        updated.addAll(items);
        this.currentQueue.setValue(updated);
    }

    public synchronized void removeFromQueue(final MediaItem item) {
        List<MediaItem> updated = new ArrayList<>();
        List<MediaItem> current = currentQueue.getValue();
        if (current != null) {
            updated.addAll(current);
            for (int i = 0; i < updated.size(); i++) {
                if (Objects.equals(updated.get(i).mediaId, item.mediaId)) {
                    updated.remove(i);
                    break;
                }
            }
        }
        this.currentQueue.setValue(updated);
    }

    public synchronized void removeRangeFromQueue(int start, int end) {
        if (end < start || start < 0) {
            return;
        }
        List<MediaItem> updated = new ArrayList<>();
        List<MediaItem> current = this.currentQueue.getValue();
        if (current != null) {
            updated.addAll(current);
            if (start < updated.size() && end <= updated.size() - 1) {
                // reverse iterate down the list, removing items
                for (int i = end; i >= start; i--) {
                    updated.remove(i);
                }
            }
        }
        this.currentQueue.setValue(updated);
    }
}
