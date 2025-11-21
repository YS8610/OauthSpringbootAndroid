package com.yoong.swiftycompanion.helper;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public final class OkHttpProvider {
    private static final OkHttpClient INSTANCE = createClient();

    private OkHttpProvider() { }

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public static OkHttpClient getClient() {
        return INSTANCE;
    }
}
