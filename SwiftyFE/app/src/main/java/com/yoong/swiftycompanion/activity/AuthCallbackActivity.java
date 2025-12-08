package com.yoong.swiftycompanion.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yoong.swiftycompanion.ConstantSwifty;
import com.yoong.swiftycompanion.R;
import com.yoong.swiftycompanion.model.Skill;
import com.yoong.swiftycompanion.model.UserDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;

public class AuthCallbackActivity extends AppCompatActivity {
    String token;
    String refreshToken;
    private final OkHttpClient httpClient = new OkHttpClient();
    private WebView myWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_callback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View tokenView =findViewById(R.id.textView2);
        ((TextView)tokenView).setText(R.string.loading);
        Uri uri = getIntent().getData();
        if (uri != null && uri.getQueryParameter("token") != null) {
            token = uri.getQueryParameter("token");
            refreshToken = uri.getQueryParameter("refresh_token");

            try {
                ConstantSwifty.MAP.put("access_token", token);
                ConstantSwifty.MAP.put("refresh_token", refreshToken);
            } catch (Exception e) {
                startActivity(new Intent(this, MainActivity.class));
            }
            if (ConstantSwifty.MAP.get("access_token") == null) {
                Toast.makeText(this, "Token not found in URL", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
            } else
                fetchMyInfoJson(token, null);
        }
        else{
//            Toast.makeText(this, "search intent :" + getIntent().getStringExtra("search"), Toast.LENGTH_LONG).show();
            if (ConstantSwifty.MAP.get("access_token") == null) {
                Toast.makeText(this, "No access token, please login first.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
                return;
            }
            fetchMyInfoJson(ConstantSwifty.MAP.get("access_token"), getIntent().getStringExtra("search"));
        }

    }

    public void HomeButtonHandler(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }


    public void LogoutButtonHandler(View v) {
        ConstantSwifty.MAP.clear();
        clearWebViewData();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void clearWebViewData() {
        // Clear history & cache
        myWebView.clearHistory();
        myWebView.clearCache(true);
        myWebView.clearFormData();

        // Clear cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();

        // Destroy WebView completely
        myWebView.removeAllViews();
        myWebView.destroy();
    }

    public void fetchMyInfoJson(String token, String intra) {
        String url = intra==null ? ConstantSwifty.ME_URL : ConstantSwifty.GET42USER_URL+intra;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AuthCallbackActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                Log.d("AuthCallbackActivity", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String msg = "HTTP error: " + response.code();
                    runOnUiThread(() -> {
                        Toast.makeText(AuthCallbackActivity.this, msg, Toast.LENGTH_LONG).show();
                        TextView tokenView = findViewById(R.id.textView2);
                        if (tokenView != null)
                            tokenView.setText("error: " + msg);

                    });
                    return;
                }

                String body = response.body().string();
                Log.d("AuthCallbackActivity", "onResponse: " + body);
                try {
                    Gson gson = new Gson();
                    UserDTO user = gson.fromJson(body, UserDTO.class);
                    Log.d("AuthCallbackActivity", "User info: " + user);

                    runOnUiThread(() -> {
                        TextView tokenView = findViewById(R.id.textView2);
                        ImageView imageView = findViewById(R.id.profilePic);
                        ScrollView scrollView = findViewById(R.id.projectsView);
                        if (user != null && tokenView != null) {
                            tokenView.setText(getUserInfo(user).append(getUserProjects(user)).toString());


                            Glide.with(AuthCallbackActivity.this)
                                    .load(user.image().link())
                                    .into(imageView);
                        } else {
                            Toast.makeText(AuthCallbackActivity.this, "No 'userinfo' in response", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> Toast.makeText(AuthCallbackActivity.this, "Parse error: " + ex.getMessage(), Toast.LENGTH_LONG).show());
                    Log.d("AuthCallbackActivity", "Parse error: " + ex.getMessage());
                }
            }
        });
    }

    StringBuilder getUserInfo(UserDTO user) {
        StringBuilder info = new StringBuilder();
        info.append("Username: ").append(user.login())
                .append("\n")
                .append("Email: ").append(user.email())
                .append("\n")
                .append("Level: ").append(user.cursus_users()[1].level())
                .append("\n")
                .append("Grade: ").append(user.cursus_users()[1].grade())
                .append("\n\n")
                .append("Skills:");
        for (Skill skill : user.cursus_users()[1].skills())
            info.append("\n").append(skill.name()).append(" - ").append(skill.level());
        return info;
    }

    StringBuilder getUserProjects(UserDTO user) {
        StringBuilder projectsInfo = new StringBuilder();
        projectsInfo.append("\n\nProjects:");
        for (var projectUser : user.projects_users())
            projectsInfo.append("\n").append(projectUser.project().name())
                    .append(" - ").append(projectUser.status())
                    .append("  ").append(projectUser.final_mark() != null ? projectUser.final_mark() + "%": "N/A");
        return projectsInfo;
    }

}
