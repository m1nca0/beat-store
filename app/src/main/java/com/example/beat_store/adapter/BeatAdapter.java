package com.example.beat_store.adapter;

import android.util.Log;
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
    private OnBeatClickListener beatClickListener;
    private OnPlayClickListener playClickListener;

    private String currentUsername;
    private String currentRole;

    public interface OnBuyClickListener {
        void onBuyClick(Beat beat, int position);
    }

    public interface OnBeatClickListener {
        void onBeatClick(Beat beat, int position);
    }

    public interface OnPlayClickListener {
        void onPlayClick(Beat beat, int position);
    }

    public BeatAdapter(List<Beat> beatList) {
        this.beatList = beatList;
    }

    public BeatAdapter(List<Beat> beatList, OnBuyClickListener listener) {
        this.beatList = beatList;
        this.buyClickListener = listener;
    }

    public BeatAdapter(List<Beat> beatList, OnBuyClickListener buyListener,
                       OnBeatClickListener beatListener, OnPlayClickListener playListener) {
        this.beatList = beatList;
        this.buyClickListener = buyListener;
        this.beatClickListener = beatListener;
        this.playClickListener = playListener;
    }

    public void setCurrentUser(String username, String role) {
        this.currentUsername = username;
        this.currentRole = role;
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
        holder.tvProducerName.setText(beat.getUsernameproducer());
        holder.tvGenreBpm.setText(beat.getGenre() + " • " + beat.getBpm() + " BPM");
        holder.tvPrice.setText(String.format("$%.2f", beat.getPrice()));

        String producerUsername = beat.getUsernameproducer();
        String beatOwner = beat.getOwner();
        String username = currentUsername;
        String role = currentRole;

        if (username != null && "producer".equals(role)
                && producerUsername != null && producerUsername.equals(username)) {
            holder.btnBuy.setText("Изменить");
            holder.btnBuy.setVisibility(View.VISIBLE);
            holder.btnBuy.setEnabled(true);
            holder.btnBuy.setOnClickListener(v -> {
                if (buyClickListener != null) {
                    buyClickListener.onBuyClick(beat, position);
                }
            });
        }
        else if (username != null && "customer".equals(role)
                && beatOwner != null && beatOwner.equals(username)) {
            holder.btnBuy.setText("Куплено ✓");
            holder.btnBuy.setVisibility(View.VISIBLE);
            holder.btnBuy.setEnabled(false);
            holder.btnBuy.setOnClickListener(null);
        }
        else if (username != null && "customer".equals(role)) {
            holder.btnBuy.setText("Купить");
            holder.btnBuy.setVisibility(View.VISIBLE);
            holder.btnBuy.setEnabled(true);
            holder.btnBuy.setOnClickListener(v -> {
                if (buyClickListener != null) {
                    buyClickListener.onBuyClick(beat, position);
                }
            });
        }
        else if (username != null && "producer".equals(role)) {
            holder.btnBuy.setVisibility(View.GONE);
        }
        else {
            holder.btnBuy.setText("Купить");
            holder.btnBuy.setVisibility(View.VISIBLE);
            holder.btnBuy.setEnabled(true);
            holder.btnBuy.setOnClickListener(v -> {
                if (buyClickListener != null) {
                    buyClickListener.onBuyClick(beat, position);
                }
            });
        }

        holder.ivCover.setOnClickListener(v -> {
            if (beatClickListener != null) {
                beatClickListener.onBeatClick(beat, position);
            }
        });

        holder.btnPlayPreview.setOnClickListener(v -> {
            if (playClickListener != null) {
                playClickListener.onPlayClick(beat, position);
            }
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