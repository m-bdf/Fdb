package com.example.fdb.ui.movies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fdb.R;
import com.example.fdb.databinding.FragmentMovieBinding;
import com.example.fdb.service.tmdb.TMDbService.Movie;
import com.example.fdb.service.tmdb.TMDbService.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

@NoArgsConstructor
@AllArgsConstructor
public class MoviesFragment extends Fragment {

    private Function<Integer, Call<Page<Movie>>> fetch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView)
                inflater.inflate(R.layout.fragment_movies, container, false);
        view.setAdapter(new Adapter());
        return view;
    }

    @Getter
    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private class ViewHolder extends RecyclerView.ViewHolder {
            private final FragmentMovieBinding binding;

            private ViewHolder(FragmentMovieBinding newBinding) {
                super(newBinding.getRoot());
                binding = newBinding;
            }
        }

        private final List<Movie> values = new ArrayList<>();
        private int page, itemCount = 1;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentMovieBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position < values.size()) {
                final Movie movie = values.get(position);
                holder.binding.title.setText(movie.getTitle());
                holder.binding.overview.setText(movie.getOverview());
            } else {
                fetch.apply(page += 1).enqueue(onSuccess(response -> {
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
}