package com.example.beat_store;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beat_store.model.AuthRequest;
import com.example.beat_store.model.AuthResponse;
import com.example.beat_store.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private RadioButton rbCustomer;
    private RadioButton rbProducer;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Находим все элементы
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbProducer = findViewById(R.id.rbProducer);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Кнопка "Вход"
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = rbCustomer.isChecked() ? "customer" : "producer";
            AuthRequest request = new AuthRequest(username, password, role);
            login(request);
        });

        // Кнопка "Регистрация"
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = rbCustomer.isChecked() ? "customer" : "producer";
            AuthRequest request = new AuthRequest(username, password, role);
            register(request);
        });
    }

    private void login(AuthRequest request) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Toast.makeText(LoginActivity.this,
                            "Вход выполнен! " + authResponse.getMessage(),
                            Toast.LENGTH_SHORT).show();

                    // Переход на главную
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", request.getUsername());
                    intent.putExtra("role", request.getRole());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Ошибка входа: " + response.code(),
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

    private void register(AuthRequest request) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Toast.makeText(LoginActivity.this,
                            "Регистрация успешна! " + authResponse.getMessage(),
                            Toast.LENGTH_SHORT).show();

                    // После регистрации — сразу на главную
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", request.getUsername());
                    intent.putExtra("role", request.getRole());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Ошибка регистрации: " + response.code(),
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