package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters.MyQueueRecyclerViewAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.common.OnClickHandler;
import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class QueueFragment extends Fragment {

    private static final String TAG = QueueFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private final List<MediaItem> currentQueue;
    private int mColumnCount = 1;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private MyQueueRecyclerViewAdapter queueAdapter;
    private MediaViewModel mediaViewModel;

    // TODO's
    /*
     * add mediacontroller so we can display all the items in the current queue
     * add context menu so we can remove items from queue
     * */

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QueueFragment() {
        currentQueue = new ArrayList<>();
    }

    public static QueueFragment newInstance(int columnCount) {
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
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
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(QueueFragment.this)
                        .navigate(R.id.action_QueueFragment_to_NowPlayingFragment);
            }
        });

        this.mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.queue_fragment_title);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            queueAdapter = new MyQueueRecyclerViewAdapter(currentQueue, new QueueViewClickHandler());
            recyclerView.setAdapter(queueAdapter);
            registerForContextMenu(recyclerView);
        }
        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
//        if (v instanceof RecyclerView) {
//            MediaItem item = ((MyQueueRecyclerViewAdapter) ((RecyclerView) v).getAdapter()).getContextMenuItem();
//        }
        inflater.inflate(R.menu.menu_now_playing_queue, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_from_queue:
                Log.d(TAG, "Remove from Queue clicked");
                MediaItem toRemove = queueAdapter.getContextMenuItem();
                for (int i = 0; i < mediaController.getMediaItemCount(); i++) {
                    if (toRemove == mediaController.getMediaItemAt(i)) {
                        mediaController.removeMediaItem(i);
                        updateQueue();
                        break;
                    }
                }
                // remove from the viewModel queue as well
                mediaViewModel.removeFromQueue(toRemove);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        updateQueue();
    }

    private void updateQueue() {
        this.currentQueue.clear();
        List<MediaItem> savedQueue = this.mediaViewModel.getQueue().getValue();
        // if we are resuming and the mediaViewModel has elements, restore them to the controller
        if (mediaController.getMediaItemCount() == 0 && savedQueue != null && !savedQueue.isEmpty()) {
            mediaController.addMediaItems(savedQueue);
        }
        for (int i = 0; i < mediaController.getMediaItemCount(); i++) {
            currentQueue.add(mediaController.getMediaItemAt(i));
        }
        queueAdapter.notifyDataSetChanged();
    }

    private class QueueViewClickHandler implements OnClickHandler {

        @Override
        public void onClick(int position, MediaItem item) {
            // need to remove all items earlier than the selected one in the queue
            // and then start playing
            boolean wasPlaying = false;
            if (mediaController.getCurrentMediaItemIndex() < position && mediaController.isPlaying()) {
                mediaController.stop();
                wasPlaying = true;
            }
            mediaController.removeMediaItems(0, position);
            updateQueue();
            if (wasPlaying) {
                mediaController.prepare();
                mediaController.play();
            }
        }
    }
}