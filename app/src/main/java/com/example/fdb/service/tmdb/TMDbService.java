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
        String redirect_to;
    }

    @POST("4/auth/request_token")
    Call<RequestToken> requestToken(@Body RedirectTo redirectTo);

    @Value
    class RequestToken {
        String request_token;
    }

    @POST("4/auth/access_token")
    Call<AccessToken> accessToken(@Body RequestToken requestToken);

    @Value
    class AccessToken {
        String account_id;
        String access_token;
    }

    @POST("3/authentication/session/convert/4")
    Call<SessionId> sessionId(@Body AccessToken accessToken);

    @Value
    class SessionId {
        String session_id;
    }

    @GET("3/account")
    Call<Account> account(@Query("session_id") String sessionId);

    @Value
    class Account {
        String name;
        String username;
    }

    @GET("3/discover/movie")
    Call<Page<Movie>> discover(@Query("page") int page);

    @GET("3/search/movie")
    Call<Page<Movie>> search(@Query("query") String query, @Query("page") int page);

    @Value
    class Page<T> {
        T[] results;
        int total_results;
    }

    @Value
    class Movie {
        String title;
        String overview;
    }
}