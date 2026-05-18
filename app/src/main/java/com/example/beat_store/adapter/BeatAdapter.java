package com.example.beat_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beat_store.R;
import com.example.beat_store.model.Beat;

import java.util.List;

public class BeatAdapter extends RecyclerView.Adapter<BeatAdapter.BeatViewHolder> {

    private List<Beat> beatList;

    private OnBuyClickListener buyClickListener;

    public interface OnBuyClickListener {
        void onBuyClick(Beat beat, int position);
    }
    public interface OnBeatClickListener {
        void onBeatClick(Beat beat, int position);
    }
    private OnBeatClickListener beatClickListener;
    public BeatAdapter(List<Beat> beatList) {
        this.beatList = beatList;
    }
    public BeatAdapter(List<Beat> beatList, OnBuyClickListener listener) {
        this.beatList = beatList;
        this.buyClickListener = listener;
    }
    public BeatAdapter(List<Beat> beatList, OnBuyClickListener buyListener, OnBeatClickListener beatListener) {
        this.beatList = beatList;
        this.buyClickListener = buyListener;
        this.beatClickListener = beatListener;
    }
    @NonNull
    @Override
    public BeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_beat, parent, false);
        return new BeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeatViewHolder holder, int position) {
        Beat beat = beatList.get(position);

        holder.tvTitle.setText(beat.getTitle());
        holder.tvProducerName.setText(beat.getUserNameProducer());
        holder.tvGenreBpm.setText(beat.getGenre() + " • " + beat.getBpm() + " BPM");
        holder.tvPrice.setText(String.format("$%.2f", beat.getPrice()));


        holder.btnBuy.setOnClickListener(v -> {
            if (buyClickListener != null) {
                buyClickListener.onBuyClick(beat, position);
            }
        });
        holder.ivCover.setOnClickListener(v -> {
            if (beatClickListener != null) {
                beatClickListener.onBeatClick(beat, position);
            }
        });
        holder.btnPlayPreview.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return beatList != null ? beatList.size() : 0;
    }

    public void updateData(List<Beat> newBeatList) {
        this.beatList = newBeatList;
    }

    static class BeatViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvTitle;
        TextView tvProducerName;
        TextView tvGenreBpm;
        TextView tvPrice;
        Button btnBuy;
        ImageButton btnPlayPreview;

        BeatViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.ivBeatCover);
            tvTitle = itemView.findViewById(R.id.tvBeatTitle);
            tvProducerName = itemView.findViewById(R.id.tvProducerName);
            tvGenreBpm = itemView.findViewById(R.id.tvGenreBpm);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnBuy = itemView.findViewById(R.id.btnBuy);
            btnPlayPreview = itemView.findViewById(R.id.btnPlayPreview);
        }
    }
}