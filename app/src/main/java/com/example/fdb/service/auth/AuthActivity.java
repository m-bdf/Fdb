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

    private TMDbService.RequestToken requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.service.requestToken(new TMDbService.RedirectTo("fdb://auth"))
                .enqueue(onSuccess(newRequestToken -> {
                    requestToken = newRequestToken;
                    final Uri uri = Uri.parse("https://www.themoviedb.org/auth/access").buildUpon()
                            .appendQueryParameter("request_token", requestToken.getRequest_token()).build();
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        Application.service.accessToken(requestToken).enqueue(onSuccess(accessToken
                -> Application.service.sessionId(accessToken).enqueue(onSuccess(sessionId
                -> Application.service.account(sessionId.getSession_id()).enqueue(onSuccess(details
                -> addAccount(details, accessToken.getAccess_token())))))));
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

    private void addAccount(TMDbService.Account details, String authToken) {
        final AccountManager accountManager = AccountManager.get(this);
        final Account account = new Account(details.getUsername(), AuthService.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, account.type, authToken);

        final Intent data = new Intent();
        data.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        data.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        setResult(RESULT_OK, data);
        finish();
    }
}