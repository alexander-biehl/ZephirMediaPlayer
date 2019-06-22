package com.curiositas.apps.zephirmediaplayer.utilities;

import android.os.Environment;

public class StorageUtilities {

    private boolean externalStorageAvailable;

    /**
     * Check to see if the external SD card is mounted
     */
    private void checkStorageState() {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            externalStorageAvailable = true;
        } else {
            externalStorageAvailable = false;
        }
    }

    /**
     * Public function to check whether external storage is available.
     *
     * @return true if the sd card is mounted, false otherwise
     */
    public boolean isExternalStorageAvailable() {
        checkStorageState();

        return externalStorageAvailable;
    }
}
