package com.example.fdb.service.tmdb;

import lombok.Value;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TMDbService {

    @Value
    class RedirectTo {
        public String redirect_to;
    }

    @POST("4/auth/request_token")
    Call<RequestToken> requestToken(@Body RedirectTo body);

    @Value
    class RequestToken {
        public String request_token;
    }

    @POST("4/auth/access_token")
    Call<AccessToken> accessToken(@Body RequestToken body);

    @Value
    class AccessToken {
        public String account_id;
        public String access_token;
    }
}