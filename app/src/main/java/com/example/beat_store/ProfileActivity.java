package com.example.beat_store;

import android.app.AlertDialog;
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
import com.example.beat_store.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements AudioPlayer.PlayerCallback {

    private TextView tvUsername, tvRole, tvEmail, tvArtistName, tvBalance, tvRegDate, tvBeatsTitle;
    private LinearLayout layoutArtistName;
    private View dividerArtist;

    private RecyclerView recyclerViewProducerBeats;
    private BeatAdapter producerBeatsAdapter;
    private List<Beat> producerBeatList;

    private String currentUsername;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AudioPlayer.getInstance().setCallback(this);

        currentUsername = getIntent().getStringExtra("username");
        currentRole = getIntent().getStringExtra("role");

        initViews();
        setupAdapter();
        loadProfileData();
        loadBeats();
        setupBottomNavigation();
        TextView tvMyBeatsTitle = findViewById(R.id.tvMyBeatsTitle);
        Button btnUploadBeat = findViewById(R.id.btnUploadBeat);
        RecyclerView recyclerViewProducerBeats = findViewById(R.id.recyclerViewProducerBeats);

        if ("producer".equals(currentRole)) {
            if (tvMyBeatsTitle != null) tvMyBeatsTitle.setVisibility(View.VISIBLE);
            if (btnUploadBeat != null) btnUploadBeat.setVisibility(View.VISIBLE);
            if (recyclerViewProducerBeats != null) recyclerViewProducerBeats.setVisibility(View.VISIBLE);
        } else if ("customer".equals(currentRole)) {
            if (tvMyBeatsTitle != null) {
                tvMyBeatsTitle.setText("Купленные биты");
                tvMyBeatsTitle.setVisibility(View.VISIBLE);
            }
            if (btnUploadBeat != null) btnUploadBeat.setVisibility(View.GONE);
            if (recyclerViewProducerBeats != null) recyclerViewProducerBeats.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvRole = findViewById(R.id.tvRole);
        tvEmail = findViewById(R.id.tvEmail);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvBalance = findViewById(R.id.tvBalance);
        tvRegDate = findViewById(R.id.tvRegDate);
        layoutArtistName = findViewById(R.id.layoutArtistName);
        layoutArtistName = findViewById(R.id.layoutArtistName);
        dividerArtist = findViewById(R.id.dividerArtist);
        recyclerViewProducerBeats = findViewById(R.id.recyclerViewProducerBeats);
        tvBeatsTitle = findViewById(R.id.tvMyBeatsTitle);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button btnTopup = findViewById(R.id.btnTopUp);
        btnTopup.setOnClickListener(v -> showTopupDialog());


        if ("customer".equals(currentRole)) {
            tvBeatsTitle.setText("Купленные биты");
        } else {
            tvBeatsTitle.setText("Мои загруженные биты");
        }
    }

    private void setupAdapter() {
        producerBeatList = new ArrayList<>();
        recyclerViewProducerBeats.setLayoutManager(new LinearLayoutManager(this));

        producerBeatsAdapter = new BeatAdapter(producerBeatList,

                (beat, position) -> {
                    if ("producer".equals(currentRole)) {
                        Intent intent = new Intent(ProfileActivity.this, UploadBeatActivity.class);
                        intent.putExtra("edit_mode", true);
                        intent.putExtra("beat_id", beat.getId());
                        intent.putExtra("beat_title", beat.getTitle());
                        intent.putExtra("beat_genre", beat.getGenre());
                        intent.putExtra("beat_bpm", beat.getBpm());
                        intent.putExtra("beat_key", beat.getKey());
                        intent.putExtra("beat_license", beat.getLicensetype());
                        intent.putExtra("beat_price", beat.getPrice());
                        intent.putExtra("beat_audio", beat.getAudiofilepath());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Вы уже купили этот бит", Toast.LENGTH_SHORT).show();
                    }
                },

                (beat, position) -> {
                    Intent intent = new Intent(ProfileActivity.this, beat_card_activity.class);
                    intent.putExtra("beat_title", beat.getTitle());
                    intent.putExtra("beat_producer", beat.getUsernameproducer());
                    intent.putExtra("beat_genre", beat.getGenre());
                    intent.putExtra("beat_bpm", beat.getBpm());
                    intent.putExtra("beat_key", beat.getKey());
                    intent.putExtra("beat_price", beat.getPrice());
                    intent.putExtra("beat_audio", beat.getAudiofilepath());
                    startActivity(intent);
                },

                (beat, position) -> {
                    String audioUrl = "http://10.0.2.2:8080" + beat.getAudiofilepath();
                    AudioPlayer.getInstance().togglePlay(audioUrl, beat.getTitle(), position);
                });

        producerBeatsAdapter.setCurrentUser(currentUsername, currentRole);
        recyclerViewProducerBeats.setAdapter(producerBeatsAdapter);
    }

    private void loadProfileData() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getProfile(currentUsername, currentRole).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> profile = response.body();

                    tvUsername.setText(String.valueOf(profile.get("username")));
                    tvEmail.setText(String.valueOf(profile.get("email")));
                    tvRole.setText("Роль: " + currentRole);
                    tvRegDate.setText("Регистрация: " + String.valueOf(profile.get("reg_date")));

                    double balance = Double.parseDouble(String.valueOf(profile.get("balance")));
                    tvBalance.setText(String.format("$%.2f", balance));

                    if ("customer".equals(currentRole) && profile.containsKey("artist_name")) {
                        layoutArtistName.setVisibility(View.VISIBLE);
                        dividerArtist.setVisibility(View.VISIBLE);
                        tvArtistName.setText(String.valueOf(profile.get("artist_name")));
                    } else {
                        layoutArtistName.setVisibility(View.GONE);
                        dividerArtist.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBeats() {
        ApiService apiService = RetrofitClient.getApiService();


        Call<List<Beat>> call = "customer".equals(currentRole) ? apiService.getMyBeats(currentUsername) : apiService.getBeatsByProducer(currentUsername);

        call.enqueue(new Callback<List<Beat>>() {
            @Override
            public void onResponse(Call<List<Beat>> call, Response<List<Beat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    producerBeatList.clear();
                    producerBeatList.addAll(response.body());
                    producerBeatsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProfileActivity.this, "Ошибка получения списка битов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Beat>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети при загрузке битов", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTopupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_topup, null);
        TextInputEditText etAmount = view.findViewById(R.id.etAmount);

        builder.setView(view).setTitle("Пополнение баланса").setPositiveButton("Пополнить", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) return;

            double amount = Double.parseDouble(amountStr);
            Map<String, Object> request = new HashMap<>();
            request.put("username", currentUsername);
            request.put("amount", amount);
            request.put("role", currentRole);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.topUpBalance(request).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Баланс успешно пополнен!", Toast.LENGTH_SHORT).show();
                        loadProfileData();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Ошибка пополнения", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                }
            });
        }).setNegativeButton("Отмена", null).create().show();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bnb);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        Button btnUploadBeat = findViewById(R.id.btnUploadBeat);

        if (btnUploadBeat != null) {
            if ("producer".equals(currentRole)) {
                btnUploadBeat.setVisibility(View.VISIBLE);
            } else {
                btnUploadBeat.setVisibility(View.GONE);
            }

            btnUploadBeat.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UploadBeatActivity.class);
                intent.putExtra("username", currentUsername);
                intent.putExtra("role", currentRole);
                startActivity(intent);
                finish();
            });
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("username", currentUsername);
                intent.putExtra("role", currentRole);
                startActivity(intent);
                finish();
                return true;
            }


            return itemId == R.id.nav_profile;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioPlayer.getInstance().setCallback(this);
        loadBeats();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance().setCallback(null);
    }


    @Override
    public void onPlayStarted(String trackName) {
        Toast.makeText(this, "▶ " + trackName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayPaused() {
        Toast.makeText(this, "⏸ Пауза", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayResumed() {
        Toast.makeText(this, "▶ Продолжаем", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayCompleted() {
        Toast.makeText(this, "⏹ Трек завершен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}