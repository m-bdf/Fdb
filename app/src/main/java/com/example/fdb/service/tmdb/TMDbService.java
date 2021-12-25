package com.example.fdb.service.tmdb;

import lombok.Value;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TMDbService {

    @Value
    class RedirectTo {
        public String redirect_to;
    }

    @POST("4/auth/request_token")
    Call<RequestToken> requestToken(@Body RedirectTo redirectTo);

    @Value
    class RequestToken {
        public String request_token;
    }

    @POST("4/auth/access_token")
    Call<AccessToken> accessToken(@Body RequestToken requestToken);

    @Value
    class AccessToken {
        public String account_id;
        public String access_token;
    }

    @POST("3/authentication/session/convert/4")
    Call<SessionId> sessionId(@Body AccessToken accessToken);

    @Value
    class SessionId {
        public String session_id;
    }

    @GET("3/account")
    Call<Account> account(@Query("session_id") String sessionId);

    @Value
    class Account {
        public String name;
        public String username;
    }
}