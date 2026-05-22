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
import com.example.beat_store.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private Button btnTabLogin;
    private Button btnTabRegister;
    private LinearLayout layoutLogin;
    private LinearLayout layoutRegister;
    private TextInputEditText etLoginUsername;
    private TextInputEditText etLoginPassword;
    private RadioGroup rgRoleLogin;
    private TextInputEditText etRegUsername;
    private TextInputEditText etRegEmail;
    private TextInputEditText etRegPassword;
    private TextInputEditText etRegArtistName;
    private TextInputLayout tilRegArtistName;
    private RadioGroup rgRoleReg;
    private Button btnLogin;
    private Button btnRegister;
    private boolean isLoginTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabRegister = findViewById(R.id.btnTabRegister);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        rgRoleLogin = findViewById(R.id.rgRoleLogin);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegArtistName = findViewById(R.id.etRegArtistName);
        tilRegArtistName = findViewById(R.id.tilRegArtistName);
        rgRoleReg = findViewById(R.id.rgRoleReg);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnTabLogin.setOnClickListener(v -> switchToLogin());
        btnTabRegister.setOnClickListener(v -> switchToRegister());
        rgRoleReg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCustomerReg) {
                tilRegArtistName.setVisibility(View.VISIBLE);
            } else {
                tilRegArtistName.setVisibility(View.GONE);
            }
        });
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
        btnRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String artistName = etRegArtistName.getText().toString().trim();
            String role = rgRoleReg.getCheckedRadioButtonId() == R.id.rbCustomerReg ? "customer" : "producer";

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

    private void switchToLogin() {
        isLoginTab = true;
        btnTabLogin.setTextColor(getColor(android.R.color.white));
        btnTabLogin.setBackgroundTintList(getColorStateList(android.R.color.holo_purple));
        btnTabRegister.setTextColor(getColor(android.R.color.darker_gray));
        btnTabRegister.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        layoutLogin.setVisibility(View.VISIBLE);
        layoutRegister.setVisibility(View.GONE);
    }

    private void switchToRegister() {
        isLoginTab = false;
        btnTabRegister.setTextColor(getColor(android.R.color.white));
        btnTabRegister.setBackgroundTintList(getColorStateList(android.R.color.holo_purple));
        btnTabLogin.setTextColor(getColor(android.R.color.darker_gray));
        btnTabLogin.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        layoutLogin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.VISIBLE);
        if (rgRoleReg.getCheckedRadioButtonId() == R.id.rbCustomerReg) {
            tilRegArtistName.setVisibility(View.VISIBLE);
        } else {
            tilRegArtistName.setVisibility(View.GONE);
        }
    }

    private void sendRequest(AuthRequest request, String endpoint) {
        ApiService apiService = RetrofitClient.getApiService();

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