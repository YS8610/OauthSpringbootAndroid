package com.yoong.swiftycompanion.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.yoong.swiftycompanion.ConstantSwifty;
import com.yoong.swiftycompanion.R;

public class MainActivity extends AppCompatActivity {
    Button authButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // check on user is authenticated then change the button text
        Button authButton = findViewById(R.id.auth_button);
    }

    public void AuthButtonHandler(View v) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        intent.launchUrl(MainActivity.this, Uri.parse(ConstantSwifty.OAUTH_LOGIN));
    }

}