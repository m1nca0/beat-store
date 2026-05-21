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

    /**
     * Интерфейс для общения с Activity.
     * Activity должна реализовать этот интерфейс.
     */
    public interface PlayerCallback {
        void onPlayStarted(String trackName);
        void onPlayPaused();
        void onPlayResumed();
        void onPlayCompleted();
        void onPlayError(String error);
    }


    private AudioPlayer() {}

    /**
     * Получить единственный экземпляр плеера.
     */
    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    /**
     * Установить колбэк (Activity подписывается на события плеера).
     */
    public void setCallback(PlayerCallback callback) {
        this.callback = callback;
    }

    /**
     * Главный метод: play или pause.
     *
     * @param audioUrl  полный URL аудиофайла (например http://10.0.2.2:8080/beat-store-media/123.mp3)
     * @param trackName название трека (для Toast)
     * @param position  позиция трека в списке (чтобы отличать треки)
     */
    public void togglePlay(String audioUrl, String trackName, int position) {
        Log.d("AUDIO_PLAYER", "togglePlay: url=" + audioUrl + " pos=" + position + " currentPos=" + currentPosition);
        Log.d("AUDIO_PLAYER", "togglePlay: url=" + audioUrl + " trackName=" + trackName);

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
                Log.e("AUDIO_PLAYER", "Ошибка: what=" + what + " extra=" + extra);
                if (callback != null) callback.onPlayError("Ошибка воспроизведения");
                release();
                return true;
            });

        } catch (Exception e) {
            Log.e("AUDIO_PLAYER", "Ошибка: " + e.getMessage());
            if (callback != null) callback.onPlayError(e.getMessage());
            release();
        }
    }

    /**
     * Полностью остановить и освободить плеер.
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPosition = -1;
        currentUrl = null;
    }

    /**
     * Играет ли плеер сейчас.
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * На паузе ли плеер.
     */
    public boolean isPaused() {
        return mediaPlayer != null && !mediaPlayer.isPlaying() && currentPosition != -1;
    }

    /**
     * Получить позицию текущего трека.
     */
    public int getCurrentPosition() {
        return currentPosition;
    }
    /**
     * Получить текущую позицию воспроизведения (в миллисекундах).
     */
    public int getCurrentPlaybackPosition() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || isPaused())) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Получить длительность трека (в миллисекундах).
     */
    public int getDuration() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || isPaused())) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * Перемотать трек на указанную позицию.
     */
    public void seekTo(int positionMs) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(positionMs);
        }
    }
}