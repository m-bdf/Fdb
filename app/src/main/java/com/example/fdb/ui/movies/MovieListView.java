package com.example.fdb.ui.movies;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.example.fdb.Application;
import com.example.fdb.databinding.ViewMovieDetailsBinding;
import com.example.fdb.databinding.ViewMovieEntryBinding;
import com.example.fdb.databinding.ViewMovieEntryBindingImpl;
import com.example.fdb.service.tmdb.MovieService;
import com.example.fdb.service.tmdb.MovieService.Movie;
import com.example.fdb.service.tmdb.MovieService.Page;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public final class MovieListView extends SlidingPaneLayout {

    private final RecyclerView movieList = new RecyclerView(getContext());
    private final ViewMovieDetailsBinding movieDetails =
            ViewMovieDetailsBinding.inflate(LayoutInflater.from(getContext()));

    public MovieListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addView(movieList);
        addView(movieDetails.getRoot());

        movieList.setLayoutManager(new LinearLayoutManager(context));
        movieList.setLayoutParams(new LayoutParams(800, ViewGroup.LayoutParams.MATCH_PARENT));

        movieDetails.getRoot().setLayoutParams(
                new LayoutParams(1000, ViewGroup.LayoutParams.MATCH_PARENT));

        movieDetails.favButton.setOnClickListener(view -> {
            final Movie movie = movieDetails.getMovie();
            Application.movieService.addToFavorites(Application.account.id,
                    new MovieService.Favorite(movie.getId(), !movie.getFavorite())
            ).enqueue(onSuccess(unused -> {
                movie.setFavorite(!movie.getFavorite());
                movieDetails.getEntry().setMovie(movie);
                movieDetails.setMovie(movie);
            }));
        });
    }

    public void setFetcher(Function<Integer, Call<Page<Movie>>> fetcher) {
        movieList.setAdapter(new MovieListAdapter(fetcher));
        open();
    }

    @RequiredArgsConstructor
    private class MovieListAdapter extends RecyclerView.Adapter<MovieEntryViewHolder> {

        private final Function<Integer, Call<Page<Movie>>> fetcher;
        private final List<Movie> movies = new ArrayList<>();
        @Getter
        private int page, itemCount = 1;

        @NonNull
        @Override
        public MovieEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieEntryViewHolder(ViewMovieEntryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieEntryViewHolder holder, int position) {
            if (position < movies.size()) {
                holder.binding.setMovie(movies.get(position));
            } else if (null != fetcher) {
                fetcher.apply(page += 1).enqueue(onSuccess(response -> {
                    if (null == response) return;
                    itemCount = response.getTotal_results();

                    for (final Movie movie : response.getResults())
                        Application.movieService.isFavorite(movie.getId()).enqueue(onSuccess(favorite -> {
                            movie.setFavorite(null != favorite ? favorite.isFavorite() : null);
                            movies.add(movie);
                            notifyItemChanged(movies.size() - 1);
                        }));
                }));
            }
        }
    }

    private class MovieEntryViewHolder extends RecyclerView.ViewHolder {
        private final ViewMovieEntryBinding binding;

        private MovieEntryViewHolder(ViewMovieEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(view -> {
                movieDetails.setEntry((ViewMovieEntryBindingImpl) binding);
                movieDetails.setMovie(binding.getMovie());
                close();
            });

            binding.favButton.setOnClickListener(view -> {
                final Movie movie = binding.getMovie();
                Application.movieService.addToFavorites(Application.account.id,
                        new MovieService.Favorite(movie.getId(), !movie.getFavorite())
                ).enqueue(onSuccess(unused -> {
                    movie.setFavorite(!movie.getFavorite());
                    binding.setMovie(movie);
                    movieDetails.setMovie(movie);
                }));
            });
        }
    }

    @EverythingIsNonNull
    private <T> Callback<T> onSuccess(Consumer<T> consumer) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                consumer.accept(response.body());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}