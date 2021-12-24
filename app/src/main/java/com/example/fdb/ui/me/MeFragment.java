package com.example.fdb.ui.me;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.fdb.Application;
import com.example.fdb.databinding.FragmentMeBinding;
import com.example.fdb.service.auth.AuthService;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;
    private FragmentMeBinding binding;

    private final ActivityResultLauncher<Intent> switchAccount =
            registerForActivityResult(new StartActivityForResult(), result -> {
                final Intent data = result.getData();

                if (Activity.RESULT_OK == result.getResultCode() && null != data) {
                    Application.selectedAccount = new Account(
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
                    );
                    System.out.println("Selected account: " + Application.selectedAccount);
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meViewModel =
                new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMe;
        meViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        binding.button.setOnClickListener(view -> switchAccount.launch(
                AccountManager.newChooseAccountIntent(
                        Application.selectedAccount,
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
}