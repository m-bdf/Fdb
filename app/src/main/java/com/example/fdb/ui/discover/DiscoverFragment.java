package com.example.fdb.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fdb.Application;
import com.example.fdb.databinding.FragmentDiscoverBinding;

public class DiscoverFragment extends Fragment {

    private DiscoverViewModel discoverViewModel;
    private FragmentDiscoverBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        discoverViewModel =
                new ViewModelProvider(this).get(DiscoverViewModel.class);

        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        binding.movieResults.setFetcher(Application.service::discover);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}