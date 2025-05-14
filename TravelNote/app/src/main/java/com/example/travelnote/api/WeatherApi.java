// WeatherApi.java
package com.example.travelnote.api;

import com.example.travelnote.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("v1/forecast?current_weather=true")
    Call<WeatherResponse> getCurrentWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );
}
