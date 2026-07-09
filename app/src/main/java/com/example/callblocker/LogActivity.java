package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LogActivity extends AppCompatActivity {

    private ListView listView;
    private Button clearBtn, backBtn;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        listView = findViewById(R.id.logList);
        clearBtn = findViewById(R.id.clearLogBtn);
        backBtn = findViewById(R.id.backBtn);

        // Lataa lokit
        Set<String> stored = prefs.getStringSet("entries", new HashSet<>());
        list = new ArrayList<>(stored);

        // ⭐ Lajittele uusimmasta vanhimpaan
        Collections.sort(list, Collections.reverseOrder());

        // ⭐ Kaksirivinen UI
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, list);
        listView.setAdapter(adapter);

        // Tyhjennys
        clearBtn.setOnClickListener(v -> {
            editor.putStringSet("entries", new HashSet<>()).apply();
            list.clear();
            adapter.notifyDataSetChanged();
        });

        backBtn.setOnClickListener(v -> finish());
    }
}
