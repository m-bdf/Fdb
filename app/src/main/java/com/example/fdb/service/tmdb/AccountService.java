package com.example.fdb.service.tmdb;

import lombok.Value;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AccountService {

    @GET("3/authentication/token/new")
    Call<RequestToken> requestToken();

    @Value
    class RequestToken {
        String request_token;
    }

    @POST("3/authentication/session/new")
    Call<SessionId> sessionId(@Body RequestToken requestToken);

    @Value
    class SessionId {
        String session_id;
    }

    @GET("3/account")
    Call<Account> account();

    @Value
    class Account {
        int id;
        String name;
        String username;
    }
}