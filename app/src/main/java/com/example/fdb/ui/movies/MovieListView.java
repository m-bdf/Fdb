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

import com.example.fdb.databinding.ViewMovieDetailsBinding;
import com.example.fdb.databinding.ViewMovieEntryBinding;
import com.example.fdb.service.tmdb.TMDbService.Movie;
import com.example.fdb.service.tmdb.TMDbService.Page;

import java.util.ArrayList;
import java.util.Arrays;
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
        movieList.setLayoutManager(new LinearLayoutManager(context));

        addView(movieList);
        addView(movieDetails.getRoot());
        open();
    }

    public void setFetcher(Function<Integer, Call<Page<Movie>>> fetcher) {
        movieList.setAdapter(new MovieListAdapter(fetcher));
    }

    @RequiredArgsConstructor
    private class MovieListAdapter extends RecyclerView.Adapter<MovieEntryViewHolder> {

        private final Function<Integer, Call<Page<Movie>>> fetcher;
        private final List<Movie> values = new ArrayList<>();
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
            if (position < values.size()) {
                holder.binding.setMovie(values.get(position));
            } else if (null != fetcher) {
                fetcher.apply(page += 1).enqueue(onSuccess(response -> {
                    values.addAll(Arrays.asList(response.getResults()));
                    itemCount = response.getTotal_results();
                    onBindViewHolder(holder, position);
                }));
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

    private class MovieEntryViewHolder extends RecyclerView.ViewHolder {
        private final ViewMovieEntryBinding binding;

        private MovieEntryViewHolder(ViewMovieEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(view -> {
                movieDetails.setMovie(binding.getMovie());
                close();
            });
        }
    }
}