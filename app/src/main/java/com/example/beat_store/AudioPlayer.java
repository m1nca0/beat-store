package com.example.beat_store;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;
public class AudioPlayer {


    private static AudioPlayer instance;


    private MediaPlayer mediaPlayer;


    private int currentPosition = -1;


    private String currentUrl = null;


    private PlayerCallback callback;

    public interface PlayerCallback {
        void onPlayStarted(String trackName);
        void onPlayPaused();
        void onPlayResumed();
        void onPlayCompleted();
        void onPlayError(String error);
    }


    private AudioPlayer() {}

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public void setCallback(PlayerCallback callback) {
        this.callback = callback;
    }

    public void togglePlay(String audioUrl, String trackName, int position) {

        if (currentPosition == position && mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                if (callback != null) callback.onPlayPaused();
            } else {

                mediaPlayer.start();
                if (callback != null) callback.onPlayResumed();
            }
            return;
        }


        release();


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        currentPosition = position;
        currentUrl = audioUrl;

        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (callback != null) callback.onPlayStarted(trackName);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (callback != null) callback.onPlayCompleted();
                release();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (callback != null) callback.onPlayError("Ошибка воспроизведения");
                release();
                return true;
            });

        } catch (Exception e) {
            if (callback != null) callback.onPlayError(e.getMessage());
            release();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPosition = -1;
        currentUrl = null;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isPaused() {
        return mediaPlayer != null && !mediaPlayer.isPlaying() && currentPosition != -1;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
    public int getCurrentPlaybackPosition() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || isPaused())) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || isPaused())) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int positionMs) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(positionMs);
        }
    }
}