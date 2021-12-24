package com.example.fdb;

import android.accounts.Account;

import com.example.fdb.service.tmdb.TMDbService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Application extends android.app.Application {

    public static Account selectedAccount;
    public static String selectedToken =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyODYyODBlYTQ1OGJjN2ZjMzljNGQ3MTkzZGM0M2QwMSIsInN1YiI6IjYxYzI0MDhjOTA0ZjZkMDA5MzI4YmViOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.4qWhZknl7W2QKv2qtN8yLkXlXDIhiNlg3QGogXZ3eq8";

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .addInterceptor(chain -> chain.proceed(
                    chain.request().newBuilder().header("Authorization", "Bearer " + selectedToken).build()
            ))/*.authenticator((route, response) -> {
                try {
                    AccountManager accountManager = AccountManager.get(this);
                    accountManager.invalidateAuthToken(AuthService.ACCOUNT_TYPE, selectedToken);
                    selectedToken = accountManager.blockingGetAuthToken(
                            selectedAccount, AuthService.ACCOUNT_TYPE, true);
                    return response.request();
                } catch (Exception e) {
                    return null;
                }
            })*/.build();

    public static final TMDbService service = new Retrofit.Builder().client(client)
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TMDbService.class);
}