package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView tv = findViewById(R.id.logText);

        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        Set<String> logs = prefs.getStringSet("entries", null);

        if (logs == null || logs.isEmpty()) {
            tv.setText("Ei estettyjä puheluita.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : logs) sb.append(s).append("\n");
            tv.setText(sb.toString());
        }
    }
}
