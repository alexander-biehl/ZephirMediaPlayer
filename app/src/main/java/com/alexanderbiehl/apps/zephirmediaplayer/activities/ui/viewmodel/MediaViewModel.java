package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;

import java.util.ArrayList;
import java.util.List;

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

    public void addToQueue(final MediaItem item) {
        List<MediaItem> current = currentQueue.getValue();
        if (current != null) {
            current.add(current.size(), item);
        } else {
            current = new ArrayList<>();
            current.add(item);
        }
        this.currentQueue.setValue(current);
    }

    public void addToQueue(final List<MediaItem> items) {
        List<MediaItem> current = currentQueue.getValue();
        if (current != null) {
            current.addAll(current.size(), items);
        } else {
            current = new ArrayList<>(items);
        }
        this.currentQueue.setValue(current);
    }

    public void removeFromQueue(final MediaItem item) {
        List<MediaItem> current = currentQueue.getValue();
        if (current != null) {
            for (int i = 0; i < current.size(); i++) {
                if (current.get(i) == item) {
                    current.remove(i);
                    break;
                }
            }
        } else {
            current = new ArrayList<>();
        }
        this.currentQueue.setValue(current);
    }

    public void removeRangeFromQueue(int start, int end) {
        if (end < start || start < 0) {
            return;
        }
        List<MediaItem> current = this.currentQueue.getValue();
        if (current != null) {
            if (start < current.size() && end <= current.size() - 1) {
                // reverse iterate down the list, removing items
                for (int i = end; i > 0 && i >= start; i--) {
                    current.remove(i);
                }
            }
        } else {
            current = new ArrayList<>();
        }
        this.currentQueue.setValue(current);
    }
}
