package com.example.beat_store.network;

import com.example.beat_store.model.Beat;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
import com.example.beat_store.model.AuthRequest;
import com.example.beat_store.model.AuthResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @GET("beats")
    Call<List<Beat>> getBeats();
    // Вход
    @POST("login")
    Call<AuthResponse> login(@Body AuthRequest request);

    // Регистрация
    @POST("register")
    Call<AuthResponse> register(@Body AuthRequest request);
}
