package com.example.beat_store;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BeatAdapter adapter;
    private List<Beat> beatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ======== НАВБАР ========
        BottomNavigationView bottomNav = findViewById(R.id.bnb);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Уже на главной
                return true;

            } else if (itemId == R.id.nav_profile) {
                // Загружаем профиль с бэкенда
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);

                String username = getIntent().getStringExtra("username");
                String role = getIntent().getStringExtra("role");

                if (username == null || role == null) {
                    Toast.makeText(this, "Сначала войдите", Toast.LENGTH_SHORT).show();
                    return true;
                }

                apiService.getProfile(username, role).enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Object> profile = response.body();

                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("username", String.valueOf(profile.get("username")));
                            intent.putExtra("role", role);
                            intent.putExtra("email", String.valueOf(profile.get("email")));
                            intent.putExtra("balance", Double.parseDouble(String.valueOf(profile.get("balance"))));
                            intent.putExtra("reg_date", String.valueOf(profile.get("reg_date")));

                            if (profile.containsKey("artist_name")) {
                                intent.putExtra("artist_name", String.valueOf(profile.get("artist_name")));
                            }

                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
            return false;
        });
        // ======== КОНЕЦ НАВБАРА ========

        // ======== RECYCLERVIEW ========
        beatList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BeatAdapter(beatList,
                new BeatAdapter.OnBuyClickListener() {
                    @Override
                    public void onBuyClick(Beat beat, int position) {
                        Toast.makeText(MainActivity.this,
                                "Покупка: " + beat.getTitle(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new BeatAdapter.OnBeatClickListener() {
                    @Override
                    public void onBeatClick(Beat beat, int position) {
                        Intent intent = new Intent(MainActivity.this, beat_card_activity.class);

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

        recyclerView.setAdapter(adapter);
        loadBeats();
    }

    private void loadBeats() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getBeats().enqueue(new Callback<List<Beat>>() {
            @Override
            public void onResponse(Call<List<Beat>> call, Response<List<Beat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Beat> beatsFromServer = response.body();
                    beatList.clear();
                    beatList.addAll(beatsFromServer);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Ошибка сервера: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Beat>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Ошибка соединения: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}