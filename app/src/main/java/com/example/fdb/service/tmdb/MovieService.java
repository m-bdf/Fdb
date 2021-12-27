package com.example.fdb.service.tmdb;

import lombok.Data;
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

    @Data
    class Movie {
        private int id;
        private String title;
        private String overview;
        private Boolean favorite;
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

    @GET("3/movie/{movie_id}/account_states")
    Call<Favorite> isFavorite(@Path("movie_id") int movieId);

    @POST("3/account/{account_id}/favorite")
    Call<Void> addToFavorites(@Path("account_id") int accountId, @Body Favorite favorite);
}