package com.example.fix.books.rest;

import com.example.fix.books.model.Practice;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public interface WebService {
    @GET("uc?export=download&id=0BzbUj-AQ2UXHWmhlMEhmTDd3SDA")
    Call<List<Practice>> getElemnts();

    //настраиваем ретрофит
    Retrofit retrofit = new Retrofit.Builder()
            //ссылка
            .baseUrl("https://drive.google.com/")
            //конвертер
            .addConverterFactory(GsonConverterFactory.create())
            //собрать
            .build();
}