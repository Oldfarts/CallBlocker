package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class SpamReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam);

        TextView tv = findViewById(R.id.spamText);

        SharedPreferences prefs = getSharedPreferences("spam", MODE_PRIVATE);
        Set<String> spam = prefs.getStringSet("entries", null);

        if (spam == null || spam.isEmpty()) {
            tv.setText("Ei häirikkösoittajia.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : spam) sb.append(s).append("\n");
            tv.setText(sb.toString());
        }
    }
}
