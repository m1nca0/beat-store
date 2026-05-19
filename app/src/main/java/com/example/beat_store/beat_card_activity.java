package com.example.beat_store;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class beat_card_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_card);

        // Кнопка назад
        ImageButton btnBack = findViewById(R.id.imageButton);
        btnBack.setOnClickListener(v -> finish());

        // Получаем данные из Intent
        String title = getIntent().getStringExtra("beat_title");
        String producer = getIntent().getStringExtra("beat_producer");
        String genre = getIntent().getStringExtra("beat_genre");
        int bpm = getIntent().getIntExtra("beat_bpm", 0);
        String key = getIntent().getStringExtra("beat_key");
        double price = getIntent().getDoubleExtra("beat_price", 0.0);
        String license = getIntent().getStringExtra("beat_license");

        // Логи для проверки (потом удалишь)
        android.util.Log.d("BEAT_CARD", "title: " + title);
        android.util.Log.d("BEAT_CARD", "producer: " + producer);
        android.util.Log.d("BEAT_CARD", "license: " + license);
        android.util.Log.d("BEAT_CARD", "price: " + price);

        // Находим TextView
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_prod = findViewById(R.id.tv_prod);
        TextView tv_genre = findViewById(R.id.tv_genre);
        TextView tv_bpm = findViewById(R.id.tv_bpm);
        TextView tv_key = findViewById(R.id.tv_key);
        TextView tv_license = findViewById(R.id.tv_license);
        TextView tv_cost = findViewById(R.id.tv_cost);

        // Заполняем данными
        tv_title.setText(title != null ? title : "—");
        tv_prod.setText(producer != null ? producer : "—");
        tv_genre.setText(genre != null ? genre : "—");
        tv_bpm.setText(String.valueOf(bpm));
        tv_key.setText(key != null ? key : "—");
        tv_license.setText(license != null ? license : "—");

        // Форматируем цену
        tv_cost.setText(String.format("$%.2f", price));
    }
}