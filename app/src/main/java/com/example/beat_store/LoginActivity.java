package com.example.beat_store;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beat_store.model.AuthRequest;
import com.example.beat_store.model.AuthResponse;
import com.example.beat_store.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etArtistName;
    private TextInputLayout tilArtistName;
    private RadioButton rbCustomer;
    private RadioButton rbProducer;
    private RadioGroup rgRole;
    private Button btnLogin;
    private Button btnRegister;

    // Флаг: true = режим регистрации (поля открыты), false = режим входа
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Находим все элементы
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etArtistName = findViewById(R.id.etArtistName);
        tilArtistName = findViewById(R.id.tilArtistName);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbProducer = findViewById(R.id.rbProducer);
        rgRole = findViewById(R.id.rgRole);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // При выборе "Покупатель" показываем поле artist_name
        // При выборе "Продюсер" — скрываем
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (isRegisterMode) {
                if (checkedId == R.id.rbCustomer) {
                    tilArtistName.setVisibility(android.view.View.VISIBLE);
                } else {
                    tilArtistName.setVisibility(android.view.View.GONE);
                }
            }
        });

        // Кнопка "Вход"
        btnLogin.setOnClickListener(v -> {
            isRegisterMode = false;
            tilArtistName.setVisibility(android.view.View.GONE);
            performLogin();
        });

        // Кнопка "Регистрация"
        btnRegister.setOnClickListener(v -> {
            isRegisterMode = true;
            // Показываем поле artist_name только для покупателя
            if (rbCustomer.isChecked()) {
                tilArtistName.setVisibility(android.view.View.VISIBLE);
            } else {
                tilArtistName.setVisibility(android.view.View.GONE);
            }
            performRegister();
        });
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = rbCustomer.isChecked() ? "customer" : "producer";
        AuthRequest request = new AuthRequest(username, password, role);
        sendRequest(request, "login");
    }

    private void performRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String artistName = etArtistName.getText().toString().trim();
        String role = rbCustomer.isChecked() ? "customer" : "producer";

        // Проверка обязательных полей
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Если покупатель — проверим artistName
        if ("customer".equals(role) && artistName.isEmpty()) {
            Toast.makeText(this, "Введите имя артиста", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRequest request;
        if ("customer".equals(role)) {
            request = new AuthRequest(username, password, email, role, artistName);
        } else {
            request = new AuthRequest(username, password, email, role, null);
        }

        sendRequest(request, "register");
    }

    private void sendRequest(AuthRequest request, String endpoint) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<AuthResponse> call;
        if ("login".equals(endpoint)) {
            call = apiService.login(request);
        } else {
            call = apiService.register(request);
        }

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Toast.makeText(LoginActivity.this,
                            authResponse.getMessage(),
                            Toast.LENGTH_SHORT).show();

                    // Переход на главную
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", request.getUsername());
                    intent.putExtra("role", request.getRole());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Ошибка: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Ошибка соединения: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}