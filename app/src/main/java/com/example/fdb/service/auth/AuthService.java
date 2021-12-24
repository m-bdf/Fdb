package com.example.fdb.service.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class AuthService extends Service {

    public static final String ACCOUNT_TYPE = "com.example.fdb";

    @Override
    public IBinder onBind(Intent intent) {
        return new AbstractAccountAuthenticator(this) {

            @Override
            public Bundle editProperties(
                    AccountAuthenticatorResponse response,
                    String accountType) {
                return null;
            }

            @Override
            public Bundle addAccount(
                    AccountAuthenticatorResponse response,
                    String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {

                final Bundle bundle = new Bundle();
                bundle.putParcelable(AccountManager.KEY_INTENT,
                        new Intent(AuthService.this, AuthActivity.class));
                return bundle;
            }

            @Override
            public Bundle confirmCredentials(
                    AccountAuthenticatorResponse response,
                    Account account, Bundle options) {
                return null;
            }

            @Override
            public Bundle getAuthToken(
                    AccountAuthenticatorResponse response,
                    Account account, String authTokenType, Bundle options) {
                return null;
            }

            @Override
            public String getAuthTokenLabel(String authTokenType) {
                return null;
            }

            @Override
            public Bundle updateCredentials(
                    AccountAuthenticatorResponse response,
                    Account account, String authTokenType, Bundle options) {
                return null;
            }

            @Override
            public Bundle hasFeatures(
                    AccountAuthenticatorResponse response,
                    Account account, String[] features) {
                return null;
            }

        }.getIBinder();
    }
}