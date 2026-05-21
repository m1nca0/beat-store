package com.example.beat_store;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beat_store.adapter.BeatAdapter;
import com.example.beat_store.model.Beat;
import com.example.beat_store.network.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AudioPlayer.PlayerCallback {

    private RecyclerView recyclerView;
    private BeatAdapter adapter;
    private List<Beat> beatList;
    private SearchView searchView;
    private Spinner spinnerFilter;

    private String currentSearchField = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudioPlayer.getInstance().setCallback(this);

        BottomNavigationView bottomNav = findViewById(R.id.bnb);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_profile) {
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

        searchView = findViewById(R.id.searchView);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        String[] filterOptionsWithIcons = {"🔤 Название", "👤 Продюсер", "🎵 Жанр", "⏱ BPM", "🎹 Тональность", "📄 Лицензия"};
        String[] filterFields = {"title", "usernameproducer", "genre", "bpm", "key", "licensetype"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterOptionsWithIcons
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSearchField = filterFields[position];
                String query = searchView.getQuery().toString();
                if (!query.isEmpty()) {
                    performSearch(query);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentSearchField = "title";
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        beatList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BeatAdapter(beatList,
                new BeatAdapter.OnBuyClickListener() {
                    @Override
                    public void onBuyClick(Beat beat, int position) {
                        String username = getIntent().getStringExtra("username");
                        String role = getIntent().getStringExtra("role");

                        if (username == null) {
                            Toast.makeText(MainActivity.this, "Сначала войдите", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!"customer".equals(role)) {
                            Toast.makeText(MainActivity.this, "Только покупатели могут покупать биты", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> request = new HashMap<>();
                        request.put("beatId", beat.getId());
                        request.put("buyerUsername", username);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:8080/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        ApiService apiService = retrofit.create(ApiService.class);

                        apiService.buyBeat(request).enqueue(new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String message = String.valueOf(response.body().get("message"));
                                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
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
                },
                new BeatAdapter.OnPlayClickListener() {
                    @Override
                    public void onPlayClick(Beat beat, int position) {
                        String audioUrl = "http://10.0.2.2:8080" + beat.getAudioFile();
                        AudioPlayer.getInstance().togglePlay(audioUrl, beat.getTitle(), position);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        performSearch("");
    }


    /**
     * Выполняет поиск битов.
     * Если query пустой — загружает все биты.
     * Иначе — ищет по выбранному в Spinner полю.
     */
    private void performSearch(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Beat>> call;
        if (query == null || query.isEmpty()) {
            call = apiService.getBeats();
        } else {
            call = apiService.searchBeats(query, currentSearchField);
        }

        call.enqueue(new Callback<List<Beat>>() {
            @Override
            public void onResponse(Call<List<Beat>> call, Response<List<Beat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    beatList.clear();
                    beatList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (response.body().isEmpty() && !query.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Ошибка сервера: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Beat>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Ошибка соединения",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
        Toast.makeText(this, "⏹ Трек завершён", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance().setCallback(null);
    }
}