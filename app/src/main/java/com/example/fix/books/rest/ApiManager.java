package com.example.fix.books.rest;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;

import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//наш ретрофит ввиде синглтон,одиноокий клас
public class ApiManager {

    private static ApiService apiService;


    static volatile Retrofit retrofit = null;

    //приватный,чтобы обращались к методам
    private ApiManager(){}

    //метод для нашего rx retrofit
    public static Retrofit getRetrofit(){
        if (retrofit == null){
            synchronized (ApiManager.class){
                if (retrofit == null){
                    //для сети
                    OkHttpClient okHttpClient = new OkHttpClient();
                    //начинаем строить наш ретрофит
                    retrofit = new Retrofit.Builder()
                            //ссылка
                            .baseUrl("https://drive.google.com/")
                            //говорим что хотим рх ретрофи
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            //для нашей орм
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                                    .setExclusionStrategies(new ExclusionStrategy() {
                                        @Override
                                        public boolean shouldSkipField(FieldAttributes f) {
                                            return f.getDeclaringClass().equals(RealmObject.class);
                                        }

                                        @Override
                                        public boolean shouldSkipClass(Class<?> clazz) {
                                            return false;
                                        }
                                    }).create()))
                            //клиент шлем и собираем
                            .client(okHttpClient)
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static void initApiService() {
        if (apiService == null) {
            synchronized (ApiManager.class) {
                if (apiService == null) {
                    apiService = getRetrofit().create(ApiService.class);
                }
            }
        }
    }

    public static ApiService getApiService() {
        initApiService();
        return apiService;
    }
}
