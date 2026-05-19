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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beat_store.adapter.BeatAdapter;
import com.example.beat_store.model.Beat;
import com.example.beat_store.network.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    // Для битов продюсера
    private TextView tvMyBeatsTitle;
    private RecyclerView recyclerViewProducerBeats;
    private BeatAdapter producerBeatsAdapter;
    private List<Beat> producerBeatList;

    private String currentUsername;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Находим элементы профиля
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


        // Элементы для битов продюсера
        tvMyBeatsTitle = findViewById(R.id.tvMyBeatsTitle);
        recyclerViewProducerBeats = findViewById(R.id.recyclerViewProducerBeats);

        Button btnUploadBeat = findViewById(R.id.btnUploadBeat);

        btnUploadBeat.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UploadBeatActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });
        // Получаем данные из Intent
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("username");
        currentRole = intent.getStringExtra("role");
        String email = intent.getStringExtra("email");
        String artistName = intent.getStringExtra("artist_name");
        double balance = intent.getDoubleExtra("balance", 0.0);
        String regDate = intent.getStringExtra("reg_date");

        // Заполняем профиль
        tvUsername.setText(currentUsername != null ? currentUsername : "Гость");
        tvEmail.setText(email != null ? email : "—");

        if ("customer".equals(currentRole)) {
            tvRole.setText("Покупатель");
            if (artistName != null && !artistName.isEmpty()) {
                tvArtistName.setText(artistName);
                layoutArtistName.setVisibility(View.VISIBLE);
                dividerArtist.setVisibility(View.VISIBLE);
            }
            // Покупатель — показываем КУПЛЕННЫЕ биты
            tvMyBeatsTitle.setText("Мои покупки");
            tvMyBeatsTitle.setVisibility(View.VISIBLE);
            recyclerViewProducerBeats.setVisibility(View.VISIBLE);
            setupProducerBeats();  // используем тот же метод
            loadCustomerBeats();
        } else if ("producer".equals(currentRole)) {
            tvRole.setText("Продюсер");
            layoutArtistName.setVisibility(View.GONE);
            dividerArtist.setVisibility(View.GONE);
            btnUploadBeat.setVisibility(View.VISIBLE);
            tvMyBeatsTitle.setVisibility(View.VISIBLE);
            recyclerViewProducerBeats.setVisibility(View.VISIBLE);
            tvMyBeatsTitle.setVisibility(View.VISIBLE);
            recyclerViewProducerBeats.setVisibility(View.VISIBLE);
            setupProducerBeats();
            loadProducerBeats();
        }

        tvBalance.setText(String.format("$%.2f", balance));
        tvRegDate.setText(regDate != null ? regDate : "—");

        // Кнопка "Назад"
        btnBack.setOnClickListener(v -> finish());

        // Кнопка "Выйти"
        btnLogout.setOnClickListener(v -> {
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
                Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
                mainIntent.putExtra("username", currentUsername);
                mainIntent.putExtra("role", currentRole);
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
                return true; // уже здесь
            }
            return false;
        });
    }

    /**
     * Настройка RecyclerView для битов продюсера
     */
    private void setupProducerBeats() {
        producerBeatList = new ArrayList<>();
        recyclerViewProducerBeats.setLayoutManager(new LinearLayoutManager(this));

        // Используем тот же адаптер BeatAdapter
        producerBeatsAdapter = new BeatAdapter(producerBeatList,
                new BeatAdapter.OnBuyClickListener() {
                    @Override
                    public void onBuyClick(Beat beat, int position) {
                        Toast.makeText(ProfileActivity.this,
                                "Это ваш бит: " + beat.getTitle(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new BeatAdapter.OnBeatClickListener() {
                    @Override
                    public void onBeatClick(Beat beat, int position) {
                        // Открываем карточку бита
                        Intent intent = new Intent(ProfileActivity.this, beat_card_activity.class);
                        intent.putExtra("beat_id", beat.getId());
                        intent.putExtra("beat_title", beat.getTitle());
                        intent.putExtra("beat_producer", beat.getUserNameProducer());
                        intent.putExtra("beat_genre", beat.getGenre());
                        intent.putExtra("beat_bpm", beat.getBpm());
                        intent.putExtra("beat_key", beat.getKey());
                        intent.putExtra("beat_price", beat.getPrice());
                        intent.putExtra("beat_license", beat.getLicenseType());
                        intent.putExtra("beat_audio", beat.getAudioFile());
                        startActivity(intent);
                    }
                });

        recyclerViewProducerBeats.setAdapter(producerBeatsAdapter);
    }

    /**
     * Загрузка битов продюсера с бэкенда
     */
    private void loadProducerBeats() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getBeatsByProducer(currentUsername).enqueue(new Callback<List<Beat>>() {
            @Override
            public void onResponse(Call<List<Beat>> call, Response<List<Beat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Beat> beats = response.body();
                    producerBeatList.clear();
                    producerBeatList.addAll(beats);
                    producerBeatsAdapter.notifyDataSetChanged();

                    if (beats.isEmpty()) {
                        Toast.makeText(ProfileActivity.this,
                                "У вас пока нет битов", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Ошибка загрузки битов: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Beat>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Ошибка: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Загрузка купленных битов покупателя
     */
    private void loadCustomerBeats() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getMyBeats(currentUsername).enqueue(new Callback<List<Beat>>() {
            @Override
            public void onResponse(Call<List<Beat>> call, Response<List<Beat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Beat> beats = response.body();
                    producerBeatList.clear();
                    producerBeatList.addAll(beats);
                    producerBeatsAdapter.notifyDataSetChanged();

                    if (beats.isEmpty()) {
                        Toast.makeText(ProfileActivity.this,
                                "У вас пока нет купленных битов", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Ошибка загрузки битов: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Beat>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Ошибка: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if ("producer".equals(currentRole) && producerBeatList != null) {
            loadProducerBeats();
        }
    }
}