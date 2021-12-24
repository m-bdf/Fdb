package com.example.fdb.service.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.util.Consumer;

import com.example.fdb.Application;
import com.example.fdb.service.tmdb.TMDbService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class AuthActivity extends Activity {

    private String requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.service.requestToken(new TMDbService.RedirectTo("fdb://auth"))
                .enqueue(onSuccess(newRequestToken -> {
                    requestToken = newRequestToken.request_token;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                            "https://www.themoviedb.org/auth/access?request_token=" + requestToken)));
                }));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        Application.service.accessToken(new TMDbService.RequestToken(requestToken))
                .enqueue(onSuccess(accessToken -> {
                    final AccountManager accountManager = AccountManager.get(this);
                    final Account account =
                            new Account(accessToken.account_id, AuthService.ACCOUNT_TYPE);
                    accountManager.addAccountExplicitly(account, null, null);
                    accountManager.setAuthToken(account, account.type, accessToken.access_token);

                    final Intent data = new Intent();
                    data.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    data.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                    setResult(RESULT_OK, data);
                    finish();
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();

        if (null == intent) {
            finish();
        } else if (null == intent.getAction()) {
            setIntent(null);
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
                Toast.makeText(AuthActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        };
    }
}