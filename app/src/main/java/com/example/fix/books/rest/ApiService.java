package com.example.fix.books.rest;

import com.example.fix.books.model.Books;
import com.example.fix.books.model.Practice;
import com.example.fix.books.model.server.ServerRequest;
import com.example.fix.books.model.server.ServerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;


public interface ApiService {
//    @GET("uc?export=download&id=0B56uYA405vcsZU9XdkthNzFvSUE")
//    Observable<List<Books>> loadBooks();
    @GET("uc?export=download&id=0BzbUj-AQ2UXHeGZiYWNIYjhTelE")
    Observable<List<Books>> loadBooks();

    @POST("android/login_registration/")
    Observable<ServerResponse> post(@Body ServerRequest request);
}
