package com.example.fdb.ui.me;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fdb.Application;
import com.example.fdb.databinding.FragmentMeBinding;
import com.example.fdb.service.auth.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;
    private FragmentMeBinding binding;

    private Account account;

    private final ActivityResultLauncher<Intent> switchAccount =
            registerForActivityResult(new StartActivityForResult(), result -> {
                final Intent data = result.getData();

                if (Activity.RESULT_OK == result.getResultCode() && null != data) {
                    account = new Account(
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
                    );

                    Application.account.name = account.name;
                    Application.account.sessionId = AccountManager.get(getContext())
                            .peekAuthToken(account, AuthService.ACCOUNT_TYPE);

                    Application.accountService.account().enqueue(onSuccess(account -> {
                        Application.account.id = account.getId();
                        binding.favorites.setFetcher(page ->
                                Application.movieService.favorites(Application.account.id, page));
                    }));
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meViewModel =
                new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.button.setOnClickListener(view -> switchAccount.launch(
                AccountManager.newChooseAccountIntent(
                        account,
                        null,
                        new String[]{AuthService.ACCOUNT_TYPE},
                        false,
                        null,
                        null,
                        null,
                        null
                )));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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