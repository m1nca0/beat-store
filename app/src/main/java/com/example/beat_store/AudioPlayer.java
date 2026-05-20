package com.example.beat_store;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

/**
 * Глобальный аудиоплеер (Singleton).
 * Живёт всё время работы приложения.
 * Можно вызывать из любой Activity.
 */
public class AudioPlayer {

    // Единственный экземпляр (Singleton)
    private static AudioPlayer instance;

    // Сам MediaPlayer
    private MediaPlayer mediaPlayer;

    // Какой трек сейчас играет (позиция в списке или -1)
    private int currentPosition = -1;

    // URL текущего трека
    private String currentUrl = null;

    // Колбэк для UI (показать Toast, обновить кнопку)
    private PlayerCallback callback;

    /**
     * Интерфейс для общения с Activity.
     * Activity должна реализовать этот интерфейс.
     */
    public interface PlayerCallback {
        void onPlayStarted(String trackName);   // Началось воспроизведение
        void onPlayPaused();                     // Пауза
        void onPlayResumed();                    // Продолжили
        void onPlayCompleted();                  // Трек закончился
        void onPlayError(String error);          // Ошибка
    }

    // Приватный конструктор (нельзя создать снаружи)
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

        // ===== СЛУЧАЙ 1: Нажали тот же трек =====
        if (currentPosition == position && mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                // Играет → пауза
                mediaPlayer.pause();
                if (callback != null) callback.onPlayPaused();
            } else {
                // На паузе → продолжить
                mediaPlayer.start();
                if (callback != null) callback.onPlayResumed();
            }
            return;
        }

        // ===== СЛУЧАЙ 2: Другой трек → сбросить старый =====
        release();

        // Создаём новый плеер
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