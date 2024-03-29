package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.adapters.SongListAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main.SongListViewModel;


public class SongListFragment extends Fragment {

    private SongListViewModel mViewModel;
    private RecyclerView recyclerView;

    public static SongListFragment newInstance() {
        return new SongListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        this.recyclerView = view.findViewById(R.id.song_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.recyclerView.addItemDecoration(
                new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL)
        );

        final SongListAdapter adapter = new SongListAdapter(view.getContext());
        this.recyclerView.setAdapter(adapter);

        mViewModel = new ViewModelProvider(this).get(SongListViewModel.class);
        mViewModel.getSongs().observe(getActivity(), songs -> {
            adapter.setSongs(songs);
        });

        return view;
    }
}