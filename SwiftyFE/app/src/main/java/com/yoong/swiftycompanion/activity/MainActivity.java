// java
package com.yoong.swiftycompanion.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
    Button searchButton;
    TextView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // assign to the class field (avoid shadowing)
        authButton = findViewById(R.id.auth_button);
        searchButton = findViewById(R.id.searchButton);
        View searchView = findViewById(R.id.searchText);
        View tokenView = findViewById(R.id.tokenView);
        if (ConstantSwifty.MAP.isEmpty())
            tokenView.setVisibility(View.INVISIBLE);
        else {
            tokenView.setVisibility(View.VISIBLE);
            ((TextView) tokenView).setText(ConstantSwifty.MAP.toString());
        }
        if (authButton == null)
            Log.w("MainActivity", "auth_button not found in layout");
        else{
            authButton.setVisibility(ConstantSwifty.MAP.isEmpty()?View.VISIBLE:View.INVISIBLE);
            searchButton.setVisibility(ConstantSwifty.MAP.isEmpty()?View.INVISIBLE:View.VISIBLE);
            searchView.setVisibility(ConstantSwifty.MAP.isEmpty()?View.INVISIBLE:View.VISIBLE);
        }
        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.w("MainActivity", "root view `R.id.main` is null");
        }
    }

    public void SearchButtonHandler(View v) {
        Intent intent = new Intent(this, AuthCallbackActivity.class);
        String intraName = ((TextView)findViewById(R.id.searchText)).getText().toString();
        boolean valid = true;
        for (int i =0; i < intraName.length(); i++) {
            char c = intraName.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                valid = false;
                Toast.makeText(this, "only 0 - 9, a - z and A- Z are allowed", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        intent.putExtra("search", intraName);
        startActivity(intent);
    }

    public void AuthButtonHandler(View v) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        intent.launchUrl(MainActivity.this, Uri.parse(ConstantSwifty.OAUTH_LOGIN));
    }
}
