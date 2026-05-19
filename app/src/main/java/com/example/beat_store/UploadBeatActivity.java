package com.example.beat_store;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beat_store.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadBeatActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etGenre, etBpm, etKey, etLicense, etPrice;
    private Button btnSelectFile, btnUpload;
    private TextView tvSelectedFile;
    private Uri selectedAudioUri;
    private String selectedFileName;
    private ImageButton btnBack;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedAudioUri = uri;
                    selectedFileName = getFileName(uri);
                    tvSelectedFile.setText("Выбран: " + selectedFileName);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_beat);

        etTitle = findViewById(R.id.etTitle);
        etGenre = findViewById(R.id.etGenre);
        etBpm = findViewById(R.id.etBpm);
        etKey = findViewById(R.id.etKey);
        etLicense = findViewById(R.id.etLicense);
        etPrice = findViewById(R.id.etPrice);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnUpload = findViewById(R.id.btnUpload);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Выбор аудиофайла
        btnSelectFile.setOnClickListener(v -> {
            filePickerLauncher.launch("audio/*");
        });

        // Загрузка
        btnUpload.setOnClickListener(v -> uploadBeat());
    }

    private String getFileName(Uri uri) {
        String name = "unknown";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return name;
    }

    private void uploadBeat() {
        String title = etTitle.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String bpmStr = etBpm.getText().toString().trim();
        String key = etKey.getText().toString().trim();
        String license = etLicense.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (title.isEmpty() || genre.isEmpty() || bpmStr.isEmpty() || key.isEmpty()
                || license.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAudioUri == null) {
            Toast.makeText(this, "Выберите аудиофайл", Toast.LENGTH_SHORT).show();
            return;
        }

        int bpm = Integer.parseInt(bpmStr);
        double price = Double.parseDouble(priceStr);

        String username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Ошибка: не найден пользователь", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Копируем файл во временную папку
            InputStream inputStream = getContentResolver().openInputStream(selectedAudioUri);
            File tempFile = new File(getCacheDir(), selectedFileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            // Создаём MultipartBody.Part для файла
            RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), tempFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", selectedFileName, requestFile);

            // Текстовые поля
            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody genreBody = RequestBody.create(MediaType.parse("text/plain"), genre);
            RequestBody bpmBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bpm));
            RequestBody keyBody = RequestBody.create(MediaType.parse("text/plain"), key);
            RequestBody licenseBody = RequestBody.create(MediaType.parse("text/plain"), license);
            RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
            RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);

            // Отправляем
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            apiService.uploadBeat(filePart, titleBody, genreBody, bpmBody, keyBody,
                    licenseBody, priceBody, usernameBody).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UploadBeatActivity.this, "Бит загружен!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UploadBeatActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(UploadBeatActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка чтения файла: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}