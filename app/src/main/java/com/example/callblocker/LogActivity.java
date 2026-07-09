package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LogActivity extends AppCompatActivity {

    private ListView logList;
    private Button clearLogBtn;
    private Button backBtn;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayedLogs; // Käyttäjälle näytettävät kauniit rivit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logList = findViewById(R.id.logList);
        clearLogBtn = findViewById(R.id.clearLogBtn);
        backBtn = findViewById(R.id.backBtn);

        displayedLogs = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayedLogs);
        logList.setAdapter(adapter);

        loadLogs();

        clearLogBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
            prefs.edit().remove("entries").apply();

            displayedLogs.clear();
            adapter.notifyDataSetChanged();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadLogs() {
        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet("entries", new HashSet<>());

        // Luodaan kopio järjestämistä varten
        ArrayList<String> rawLogs = new ArrayList<>(stored);
        
        // Järjestetään raa'at aikaleimat uusimmasta vanhimpaan
        Collections.sort(rawLogs, Collections.reverseOrder());

        displayedLogs.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

        for (String rawEntry : rawLogs) {
            try {
                // Jaetaan rivi aikaleimaan ja tekstiin (erottimena " : ")
                String[] parts = rawEntry.split(" : ", 2);
                if (parts.length == 2) {
                    long timestamp = Long.parseLong(parts[0]);
                    String dateString = sdf.format(new Date(timestamp));
                    
                    // Muotoillaan ihmiselle luettava rivi
                    displayedLogs.add("[" + dateString + "] " + parts[1]);
                } else {
                    // Jos rivi oli viallinen, näytetään se sellaisenaan
                    displayedLogs.add(rawEntry);
                }
            } catch (Exception e) {
                displayedLogs.add(rawEntry);
            }
        }

        adapter.notifyDataSetChanged();
    }
}