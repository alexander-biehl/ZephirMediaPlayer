package com.curiositas.apps.zephirmediaplayer.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PackageManagerCompat;

public class StorageUtilities {

    private static boolean externalStorageAvailable;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Check to see if the external SD card is mounted
     */
    private static void checkStorageState() {
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
    public static boolean isExternalStorageAvailable() {
        checkStorageState();

        return externalStorageAvailable;
    }

    public static void verifyStoragePermission(Activity activity) {
        // check if we have read permission
        int permission = ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted so we need to ask the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
