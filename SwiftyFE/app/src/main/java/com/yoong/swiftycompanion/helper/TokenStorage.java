package com.yoong.swiftycompanion.helper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class TokenStorage {

    private final SharedPreferences prefs;

    public TokenStorage(Context context) throws Exception {

        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        prefs = EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void saveToken(String token) {
        prefs.edit().putString("jwt_token", token).apply();
    }

    public String getToken() {
        return prefs.getString("jwt_token", null);
    }

    public void clearToken() {
        prefs.edit().remove("jwt_token").apply();
    }
}
