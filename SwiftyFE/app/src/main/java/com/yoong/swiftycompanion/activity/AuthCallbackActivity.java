package com.yoong.swiftycompanion.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
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
import com.yoong.swiftycompanion.helper.TokenStorage;
import com.yoong.swiftycompanion.model.Skill;
import com.yoong.swiftycompanion.model.UserDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;

public class AuthCallbackActivity extends AppCompatActivity {
    String token;
    private final OkHttpClient httpClient = new OkHttpClient();

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

        TextView tokenView = findViewById(R.id.textView2);
        tokenView.setText(R.string.loading);
        Uri uri = getIntent().getData();
        if (uri != null && uri.getQueryParameter("token") != null) {
            token = uri.getQueryParameter("token");
            try {
                TokenStorage storage = new TokenStorage(this);
                storage.saveToken(token);
            } catch (Exception e) {
                e.printStackTrace();
                startActivity(new Intent(this, MainActivity.class));
            }
            fetchMyInfoJson(token);
        }

    }

    public void HomeButtonHandler(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void fetchMyInfoJson(String token) {
        Request request = new Request.Builder()
                .url(ConstantSwifty.ME_URL)
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
                    runOnUiThread(() -> Toast.makeText(AuthCallbackActivity.this, msg, Toast.LENGTH_LONG).show());
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
                    .append(" - ").append(projectUser.status());
        return projectsInfo;
    }

}
