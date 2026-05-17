package com.example.beat_store.network;

import com.example.beat_store.model.Beat;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
public interface ApiService {
    @GET("beats")
    Call<List<Beat>> getBeats();
}
