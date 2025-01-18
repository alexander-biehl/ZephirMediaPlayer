package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentNowPlayingListItemBinding;

import java.util.List;

public class NowPlayingListRecyclerAdapter extends
        RecyclerView.Adapter<NowPlayingListRecyclerAdapter.ViewHolder>{

    private List<MediaItem> currentQueue;

    public NowPlayingListRecyclerAdapter(List<MediaItem> items) {
        currentQueue = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentNowPlayingListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView itemView;

        public ViewHolder(FragmentNowPlayingListItemBinding binding) {
            super(binding.getRoot());
            itemView = binding.itemName;
            // set onClicklistener
        }
    }
}
