package com.example.fix.books.rest;

import com.example.fix.books.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiPost {
    private static ApiService apiPost;


    static volatile Retrofit retrofit = null;

    private ApiPost() {
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (ApiPost.class) {
                if (retrofit == null) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static void initApiService() {
        if (apiPost == null) {
            synchronized (ApiPost.class) {
                if (apiPost == null) {
                    apiPost = getRetrofit().create(ApiService.class);
                }
            }
        }
    }

    public static ApiService getPostApi() {
        initApiService();
        return apiPost;
    }
}
