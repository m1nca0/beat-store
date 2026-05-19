package com.example.beat_store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

    // Вкладки
    private Button btnTabLogin;
    private Button btnTabRegister;

    // Контейнеры
    private LinearLayout layoutLogin;
    private LinearLayout layoutRegister;

    // Поля ВХОДА
    private TextInputEditText etLoginUsername;
    private TextInputEditText etLoginPassword;
    private RadioGroup rgRoleLogin;

    // Поля РЕГИСТРАЦИИ
    private TextInputEditText etRegUsername;
    private TextInputEditText etRegEmail;
    private TextInputEditText etRegPassword;
    private TextInputEditText etRegArtistName;
    private TextInputLayout tilRegArtistName;
    private RadioGroup rgRoleReg;

    // Кнопки действия
    private Button btnLogin;
    private Button btnRegister;

    // Активная вкладка: true = вход, false = регистрация
    private boolean isLoginTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Вкладки
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabRegister = findViewById(R.id.btnTabRegister);

        // Контейнеры
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);

        // Поля входа
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        rgRoleLogin = findViewById(R.id.rgRoleLogin);

        // Поля регистрации
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegArtistName = findViewById(R.id.etRegArtistName);
        tilRegArtistName = findViewById(R.id.tilRegArtistName);
        rgRoleReg = findViewById(R.id.rgRoleReg);

        // Кнопки действия
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // ====== ПЕРЕКЛЮЧЕНИЕ ВКЛАДОК ======

        btnTabLogin.setOnClickListener(v -> switchToLogin());
        btnTabRegister.setOnClickListener(v -> switchToRegister());

        // ====== ВЫБОР РОЛИ (РЕГИСТРАЦИЯ) ======
        rgRoleReg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCustomerReg) {
                tilRegArtistName.setVisibility(View.VISIBLE);
            } else {
                tilRegArtistName.setVisibility(View.GONE);
            }
        });

        // ====== КНОПКА "ВОЙТИ" ======
        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = rgRoleLogin.getCheckedRadioButtonId() == R.id.rbCustomerLogin
                    ? "customer" : "producer";

            AuthRequest request = new AuthRequest(username, password, role);
            sendRequest(request, "login");
        });

        // ====== КНОПКА "ЗАРЕГИСТРИРОВАТЬСЯ" ======
        btnRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String artistName = etRegArtistName.getText().toString().trim();
            String role = rgRoleReg.getCheckedRadioButtonId() == R.id.rbCustomerReg
                    ? "customer" : "producer";

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

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
        });
    }

    // ====== МЕТОДЫ ПЕРЕКЛЮЧЕНИЯ ВКЛАДОК ======

    private void switchToLogin() {
        isLoginTab = true;

        // Подсветка вкладки
        btnTabLogin.setTextColor(getColor(android.R.color.white));
        btnTabLogin.setBackgroundTintList(getColorStateList(android.R.color.holo_purple));
        btnTabRegister.setTextColor(getColor(android.R.color.darker_gray));
        btnTabRegister.setBackgroundTintList(getColorStateList(android.R.color.transparent));

        // Показываем/скрываем
        layoutLogin.setVisibility(View.VISIBLE);
        layoutRegister.setVisibility(View.GONE);
    }

    private void switchToRegister() {
        isLoginTab = false;

        // Подсветка вкладки
        btnTabRegister.setTextColor(getColor(android.R.color.white));
        btnTabRegister.setBackgroundTintList(getColorStateList(android.R.color.holo_purple));
        btnTabLogin.setTextColor(getColor(android.R.color.darker_gray));
        btnTabLogin.setBackgroundTintList(getColorStateList(android.R.color.transparent));

        // Показываем/скрываем
        layoutLogin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.VISIBLE);

        // Показываем поле artistName, если выбран покупатель
        if (rgRoleReg.getCheckedRadioButtonId() == R.id.rbCustomerReg) {
            tilRegArtistName.setVisibility(View.VISIBLE);
        } else {
            tilRegArtistName.setVisibility(View.GONE);
        }
    }

    // ====== ОТПРАВКА ЗАПРОСА ======

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