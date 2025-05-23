package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import static androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED;

import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.media3.ui.PlayerView;
import androidx.navigation.fragment.NavHostFragment;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentNowPlayingBinding;
import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.google.common.util.concurrent.ListenableFuture;

public class NowPlayingFragment extends Fragment {

    private static final String TAG = NowPlayingFragment.class.getSimpleName();
    private FragmentNowPlayingBinding binding;
    private PlayerView playerView;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;

    public NowPlayingFragment() {

    }


    @Override
    public void onStart() {
        super.onStart();
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(NowPlayingFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        initializeController();
    }

    @Override
    public void onStop() {
        MediaController.releaseFuture(controllerFuture);
        if (mediaController != null) {
            mediaController.release();
            mediaController = null;
        }
        playerView.setPlayer(null);
        super.onStop();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @OptIn(markerClass = UnstableApi.class)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(NowPlayingFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
        binding.goToQueueFab.setOnClickListener(v ->
                NavHostFragment.findNavController(NowPlayingFragment.this)
                        .navigate(R.id.action_NowPlayingFragment_to_QueueFragment)
        );

        playerView = binding.playerView;
        playerView.setDefaultArtwork(
                AppCompatResources.getDrawable(requireContext(), R.drawable.zephir)
        );
        playerView.setControllerAutoShow(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeController() {
        controllerFuture = new MediaController.Builder(
                requireContext(),
                new SessionToken(
                        requireContext(),
                        new ComponentName(
                                requireActivity(),
                                Media3Service.class
                        )
                )
        ).buildAsync();
        controllerFuture.addListener(() -> {
            if (controllerFuture.isDone()) {
                try {
                    setController(controllerFuture.get());
                } catch (Exception e) {
                    Log.e(TAG, "Exception when getting the controller: " + e);
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    private void setController(@NonNull MediaController controller) {
        mediaController = controller;
        playerView.setPlayer(mediaController);

        mediaController.addListener(new Player.Listener() {
            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                if (events.contains(EVENT_MEDIA_METADATA_CHANGED)) {
                    updateMediaMetadataUI();
                }
//                if (events.contains(EVENT_TIMELINE_CHANGED)) {
//                    updateQueue();
//                }
            }
        });

        updateMediaMetadataUI();
    }

    private void updateMediaMetadataUI() {
        if (mediaController == null || mediaController.getMediaItemCount() == 0) {
            binding.albumTextView.setText(R.string.album_view_default_text);
            binding.songTextView.setText(R.string.song_view_default_text);
            binding.artistTextView.setText(R.string.artist_view_default_test);
            return;
        }

        final MediaMetadata currentMetadata = mediaController.getMediaMetadata();
        final CharSequence title = currentMetadata.title;
        final CharSequence artist = currentMetadata.artist;
        final CharSequence album = currentMetadata.albumTitle;

        // TODO see if we can do some calculations on spacing to that longer
        // strings don't make the UI look wierd

        binding.artistTextView.setText(artist);
        binding.albumTextView.setText(album);
        binding.songTextView.setText(title);
    }
}