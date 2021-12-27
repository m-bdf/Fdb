package com.example.fdb.service.tmdb;

import lombok.Value;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {

    @Value
    class Page<T> {
        T[] results;
        int total_results;
    }

    @Value
    class Movie {
        int id;
        String title;
        String overview;
    }

    @GET("3/discover/movie")
    Call<Page<Movie>> discover(@Query("page") int page);

    @GET("3/search/movie")
    Call<Page<Movie>> search(@Query("query") String query, @Query("page") int page);

    @GET("3/account/{account_id}/favorite/movies")
    Call<Page<Movie>> favorites(@Path("account_id") int accountId, @Query("page") int page);

    @Value
    class Favorite {
        int media_id;
        String media_type = "movie";
        boolean favorite;
    }

    @POST("3/account/{account_id}/favorite")
    Call<Object> addToFavorites(@Path("account_id") int accountId, @Body Favorite favorite);
}