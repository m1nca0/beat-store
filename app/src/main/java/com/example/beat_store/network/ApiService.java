package com.example.beat_store.network;

import com.example.beat_store.model.Beat;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
import com.example.beat_store.model.AuthRequest;
import com.example.beat_store.model.AuthResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.Map;
import retrofit2.http.Path;
import retrofit2.http.Query;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.PUT;

public interface ApiService {


    @GET("profile/{username}")
    Call<Map<String, Object>> getProfile(@Path("username") String username, @Query("role") String role);
    @GET("beats")
    Call<List<Beat>> getBeats();
    // Вход
    @POST("login")
    Call<AuthResponse> login(@Body AuthRequest request);

    // Регистрация
    @POST("register")
    Call<AuthResponse> register(@Body AuthRequest request);
    @GET("beats/by-producer/{producerUsername}")
    Call<List<Beat>> getBeatsByProducer(@Path("producerUsername") String producerUsername);
    // Покупка бита
    @POST("beats/buy")
    Call<Map<String, Object>> buyBeat(@Body Map<String, Object> request);

    // Купленные биты покупателя
    @GET("beats/my-beats/{username}")
    Call<List<Beat>> getMyBeats(@Path("username") String username);

    @Multipart
    @POST("beats/upload")
    Call<Map<String, Object>> uploadBeat(
            @Part MultipartBody.Part file,
            @Part("title") RequestBody title,
            @Part("genre") RequestBody genre,
            @Part("bpm") RequestBody bpm,
            @Part("key") RequestBody key,
            @Part("licenseType") RequestBody licenseType,
            @Part("price") RequestBody price,
            @Part("usernameproducer") RequestBody usernameProducer
    );
    @GET("beats/search")
    Call<List<Beat>> searchBeats(
            @Query("query") String query,
            @Query("field") String field
    );

    @Multipart
    @POST("beats/{id}/update")
    Call<Map<String, Object>> updateBeat(
            @Path("id") Long id,
            @Part MultipartBody.Part file,
            @Part("title") RequestBody title,
            @Part("genre") RequestBody genre,
            @Part("bpm") RequestBody bpm,
            @Part("key") RequestBody key,
            @Part("licenseType") RequestBody licenseType,
            @Part("price") RequestBody price
    );

    @POST("profile/top-up")
    Call<Map<String, Object>> topUpBalance(@Body Map<String, Object> request);
}
