package com.example.beat_store;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.ImageButton;

public class beat_card_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_card);

        ImageButton btnBack = findViewById(R.id.imageButton);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        String title = getIntent().getStringExtra("beat_title");
        String producer = getIntent().getStringExtra("beat_producer");
        String genre = getIntent().getStringExtra("beat_genre");
        int bpm = getIntent().getIntExtra("beat_bpm", 0);
        String key = getIntent().getStringExtra("beat_key");
        double price = getIntent().getDoubleExtra("beat_price", 0.0);
        String license = getIntent().getStringExtra("beat_license");

        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_prod = findViewById(R.id.tv_prod);
        TextView tv_genre = findViewById(R.id.tv_genre);
        TextView tv_bpm = findViewById(R.id.tv_bpm);
        TextView tv_key = findViewById(R.id.tv_key);
        TextView tv_license = findViewById(R.id.tv_license);
        TextView tv_cost = findViewById(R.id.tv_cost);

        tv_title.setText(title);
        tv_prod.setText(producer);
        tv_genre.setText(genre);
        tv_bpm.setText("" + bpm);
        tv_key.setText(key);
        tv_license.setText(license);
        tv_cost.setText("" + price);
    }
}