package com.myapps.expired.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.myapps.expired.R;

public class MenuActivity extends AppCompatActivity {

    private Button addExpiration;
    private Button watchNotifications;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        addExpiration = findViewById(R.id.addButton);
        watchNotifications = findViewById(R.id.watch_button);
        final Context context = this;
        addExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnterExpDateActivity.class);
                startActivity(intent);
            }
        });
        preferences = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        if (preferences.getString("store", null) == null)
         {
             Intent intent = new Intent(context, RegistrationActivity.class);
             startActivity(intent);
         }
        watchNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Massages.class);
                startActivity(intent);
            }
        });
    }
}
