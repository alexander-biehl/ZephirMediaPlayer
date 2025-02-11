package com.alexanderbiehl.apps.zephirmediaplayer.activities;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.SessionToken;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.ActivityMainBinding;
import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.alexanderbiehl.apps.zephirmediaplayer.utilities.StorageUtilities;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public MediaViewModel mediaViewModel;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MediaBrowser mediaBrowser;
    private ListenableFuture<MediaBrowser> browserFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (StorageUtilities.verifyStoragePermission(this)) {
                initiateBrowserConnection();
            }
        }
    }

    private void initiateBrowserConnection() {
        SessionToken sessionToken =
                new SessionToken(this,
                        new ComponentName(this, Media3Service.class));
        browserFuture =
                new MediaBrowser.Builder(this, sessionToken).buildAsync();
        browserFuture.addListener(() -> {
            if (browserFuture.isDone()) {
                try {
                    mediaBrowser = browserFuture.get();
                    getRoot();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onStop() {
        MediaBrowser.releaseFuture(browserFuture);
        if (this.mediaBrowser != null) {
            this.mediaBrowser.release();
            this.mediaBrowser = null;
        }
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void getRoot() {
        ListenableFuture<LibraryResult<MediaItem>> rootFuture =
                mediaBrowser.getLibraryRoot(null);
        rootFuture.addListener(() -> {
            if (rootFuture.isDone()) {
                try {
                    LibraryResult<MediaItem> result = rootFuture.get();
                    MediaItem root = result.value;
                    displayResult(root);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void displayResult(@Nullable MediaItem item) {
        if (item == null) {
            Log.d(TAG, "displayResult item was null");
        } else {
            Log.d(TAG, "displayResult item was: " + item);
            mediaViewModel.setCurrentMedia(item);
            NavController navController =
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_SplashFragment_to_MediaListFragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StorageUtilities.REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Storage permissions granted");
                initiateBrowserConnection();
            } else {
                Log.d(TAG, "Permissions not granted");
            }
        } else {
            Log.e(TAG, "Request code did not match");
        }
    }
}