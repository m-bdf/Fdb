package com.example.fdb;

import com.example.fdb.service.tmdb.AccountService;
import com.example.fdb.service.tmdb.MovieService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Application extends android.app.Application {

    public static class Account {
        public int id;
        public String name;
        public String sessionId;
    }

    public static final Account account = new Account();

    private static final OkHttpClient client =
            new OkHttpClient().newBuilder().addInterceptor(chain ->
                    chain.proceed(chain.request().newBuilder().url(chain.request().url().newBuilder()
                            .addQueryParameter("api_key", "286280ea458bc7fc39c4d7193dc43d01")
                            .addQueryParameter("session_id", account.sessionId)
                            .build()).build()
                    )).build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/")
            .client(client).build();

    public static final AccountService accountService = retrofit.create(AccountService.class);
    public static final MovieService movieService = retrofit.create(MovieService.class);
}