package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import static androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION;
import static androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED;
import static androidx.media3.common.Player.EVENT_TIMELINE_CHANGED;

import android.content.ComponentName;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.media3.ui.PlayerView;
import androidx.navigation.fragment.NavHostFragment;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters.NowPlayingListRecyclerAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentNowPlayingBinding;
import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NowPlayingFragment extends Fragment {

    private static final String TAG = NowPlayingFragment.class.getSimpleName();

    private FragmentNowPlayingBinding binding;
    private PlayerView playerView;
    private MediaViewModel viewModel;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private List<MediaItem> currentQueue;
    private NowPlayingListRecyclerAdapter listAdapter;

    public NowPlayingFragment() {
        currentQueue = new ArrayList<>();
    }


    @Override
    public void onStart() {
        super.onStart();
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
        // String mediaId = getArguments().getString(MEDIA_KEY);
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // listAdapter = new NowPlayingListRecyclerAdapter(currentQueue);
        // listAdapter.setOnClickHandler(new MediaClickHandler());
        // binding.nowPlayingList.setAdapter(listAdapter);

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(NowPlayingFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        playerView = binding.playerView;

        // maybe we don't need the model at all, can just reference the data
        // held by the controller

        this.viewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
//        this.viewModel.getCurrentMedia().observe(requireActivity(), mediaItem -> {
//            // TODO handle currently playing item change
//
//        });
        // this.viewModel.getQueue().observe(requireActivity(), this::updateQueue);
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
            public void onEvents(Player player, Player.Events events) {
                // Player.Listener.super.onEvents(player, events);
//                if (events.contains(EVENT_MEDIA_METADATA_CHANGED)) {
//
//                }
                if (events.contains(EVENT_TIMELINE_CHANGED)) {
                    updateQueue();
                }
                if (events.contains(EVENT_MEDIA_ITEM_TRANSITION)) {
                    // listAdapter.notifyDataSetChanged();
                }
            }
        });
        updateQueue();
    }

    private void updateQueue() {
        this.currentQueue.clear();
        // this.currentQueue.addAll(mediaQueue);
        for (int i = 0; i < mediaController.getMediaItemCount(); i++) {
            this.currentQueue.add(mediaController.getMediaItemAt(i));
        }
        // this.listAdapter.notifyDataSetChanged();
    }

    private class MediaClickHandler implements NowPlayingListRecyclerAdapter.OnClickHandler {
        @Override
        public void onClick(int position, MediaItem item) {

        }
    }
}