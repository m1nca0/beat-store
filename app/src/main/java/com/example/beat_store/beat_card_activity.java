package com.example.beat_store;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class beat_card_activity extends AppCompatActivity implements AudioPlayer.PlayerCallback {

    private SeekBar seekBar;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private ImageButton btnPlay;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBarRunnable;

    private String audioFile;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_card);


        AudioPlayer.getInstance().setCallback(this);


        ImageButton btnBack = findViewById(R.id.imageButton);
        btnBack.setOnClickListener(v -> finish());


        title = getIntent().getStringExtra("beat_title");
        String producer = getIntent().getStringExtra("beat_producer");
        String genre = getIntent().getStringExtra("beat_genre");
        int bpm = getIntent().getIntExtra("beat_bpm", 0);
        String key = getIntent().getStringExtra("beat_key");
        double price = getIntent().getDoubleExtra("beat_price", 0.0);
        String license = getIntent().getStringExtra("beat_license");
        audioFile = getIntent().getStringExtra("beat_audiofile");


        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_prod = findViewById(R.id.tv_prod);
        TextView tv_genre = findViewById(R.id.tv_genre);
        TextView tv_bpm = findViewById(R.id.tv_bpm);
        TextView tv_key = findViewById(R.id.tv_key);
        TextView tv_license = findViewById(R.id.tv_license);
        TextView tv_cost = findViewById(R.id.tv_cost);
        btnPlay = findViewById(R.id.btnPlay);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);


        tv_title.setText(title != null ? title : "—");
        tv_prod.setText(producer != null ? producer : "—");
        tv_genre.setText(genre != null ? genre : "—");
        tv_bpm.setText(String.valueOf(bpm));
        tv_key.setText(key != null ? key : "—");
        tv_license.setText(license != null ? license : "—");
        tv_cost.setText(String.format("$%.2f", price));


        btnPlay.setOnClickListener(v -> {
            String audioUrl = "http://10.0.2.2:8080" + audioFile;
            AudioPlayer.getInstance().togglePlay(audioUrl, title, -1);
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    AudioPlayer.getInstance().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                stopSeekBarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startSeekBarUpdate();
            }
        });

        startSeekBarUpdate();
    }

    private void startSeekBarUpdate() {
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }

        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                AudioPlayer player = AudioPlayer.getInstance();

                int duration = player.getDuration();
                int currentPos = player.getCurrentPlaybackPosition();

                if (duration > 0) {
                    seekBar.setMax(duration);
                    seekBar.setProgress(currentPos);

                    tvCurrentTime.setText(formatTime(currentPos));
                    tvTotalTime.setText(formatTime(duration));
                }


                if (player.isPlaying() || player.isPaused()) {
                    handler.postDelayed(this, 200);
                }
            }
        };

        handler.post(updateSeekBarRunnable);
    }

    private void stopSeekBarUpdate() {
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }


    @Override
    public void onPlayStarted(String trackName) {
        Toast.makeText(this, "▶ " + trackName, Toast.LENGTH_SHORT).show();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        startSeekBarUpdate();
    }

    @Override
    public void onPlayPaused() {
        Toast.makeText(this, "⏸ Пауза", Toast.LENGTH_SHORT).show();
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
        stopSeekBarUpdate();
    }

    @Override
    public void onPlayResumed() {
        Toast.makeText(this, "▶ Продолжаем", Toast.LENGTH_SHORT).show();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        startSeekBarUpdate();
    }

    @Override
    public void onPlayCompleted() {
        Toast.makeText(this, "⏹ Трек завершён", Toast.LENGTH_SHORT).show();
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
        seekBar.setProgress(0);
        tvCurrentTime.setText("0:00");
        stopSeekBarUpdate();
    }

    @Override
    public void onPlayError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance().setCallback(null);
        stopSeekBarUpdate();
    }
}