package com.example.beat_store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvRole;
    private TextView tvEmail;
    private TextView tvArtistName;
    private TextView tvBalance;
    private TextView tvRegDate;
    private LinearLayout layoutArtistName;
    private View dividerArtist;
    private Button btnLogout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Находим элементы
        tvUsername = findViewById(R.id.tvUsername);
        tvRole = findViewById(R.id.tvRole);
        tvEmail = findViewById(R.id.tvEmail);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvBalance = findViewById(R.id.tvBalance);
        tvRegDate = findViewById(R.id.tvRegDate);
        layoutArtistName = findViewById(R.id.layoutArtistName);
        dividerArtist = findViewById(R.id.dividerArtist);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        // Получаем данные из Intent (передаются из MainActivity или LoginActivity)
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");
        String email = intent.getStringExtra("email");
        String artistName = intent.getStringExtra("artist_name");
        double balance = intent.getDoubleExtra("balance", 0.0);
        String regDate = intent.getStringExtra("reg_date");

        // Заполняем поля
        tvUsername.setText(username != null ? username : "Гость");
        tvEmail.setText(email != null ? email : "—");

        if ("customer".equals(role)) {
            tvRole.setText("Покупатель");
            // Показываем поле с именем артиста
            if (artistName != null && !artistName.isEmpty()) {
                tvArtistName.setText(artistName);
                layoutArtistName.setVisibility(View.VISIBLE);
                dividerArtist.setVisibility(View.VISIBLE);
            }
        } else if ("producer".equals(role)) {
            tvRole.setText("Продюсер");
            layoutArtistName.setVisibility(View.GONE);
            dividerArtist.setVisibility(View.GONE);
        } else {
            tvRole.setText("—");
        }

        tvBalance.setText(String.format("$%.2f", balance));
        tvRegDate.setText(regDate != null ? regDate : "—");

        // Кнопка "Назад" (стрелка)
        btnBack.setOnClickListener(v -> finish());

        // Кнопка "Выйти"
        btnLogout.setOnClickListener(v -> {
            // Возвращаемся на экран входа
            Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });

        // Нижний навбар
        BottomNavigationView bottomNav = findViewById(R.id.bnb);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Переход на главную
                Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
                mainIntent.putExtra("username", username);
                mainIntent.putExtra("role", role);
                mainIntent.putExtra("email", email);
                mainIntent.putExtra("balance", balance);
                mainIntent.putExtra("reg_date", regDate);
                if (artistName != null) {
                    mainIntent.putExtra("artist_name", artistName);
                }
                startActivity(mainIntent);
                finish();
                return true;

            } else if (itemId == R.id.nav_profile) {
                // Уже на профиле — ничего не делаем
                return true;
            }
            return false;
        });
    }
}