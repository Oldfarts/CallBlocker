package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SpamReportActivity extends AppCompatActivity {

    private ListView listView;
    private Button clearBtn, backBtn;

    private SimpleAdapter adapter;
    // SimpleAdapter käyttää HashMap-listaa kahden rivin esittämiseen
    private ArrayList<Map<String, String>> spamDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam);

        listView = findViewById(R.id.spamList);
        clearBtn = findViewById(R.id.clearSpamBtn);
        backBtn = findViewById(R.id.backBtn);

        spamDataList = new ArrayList<>();

        // ⭐ Määritetään kaksirivinen adapteri oikeaoppisesti
        adapter = new SimpleAdapter(
                this,
                spamDataList,
                android.R.layout.simple_list_item_2,
                new String[]{"line1", "line2"}, // Avaimet HashMapista
                new int[]{android.R.id.text1, android.R.id.text2} // Mihin kenttiin ne sijoitetaan
        );
        listView.setAdapter(adapter);

        loadSpamReports();

        // Tyhjennä spam-raportti
        clearBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("spam", MODE_PRIVATE);
            prefs.edit().remove("entries").apply(); // remove on varmempi kuin tyhjä setti

            spamDataList.clear();
            adapter.notifyDataSetChanged();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadSpamReports() {
        SharedPreferences prefs = getSharedPreferences("spam", MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet("entries", new HashSet<>());

        ArrayList<String> rawLines = new ArrayList<>(stored);
        
        // Lajitellaan aikaleiman mukaan uusimmasta vanhimpaan
        Collections.sort(rawLines, Collections.reverseOrder());

        spamDataList.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

        for (String rawEntry : rawLines) {
            Map<String, String> datum = new HashMap<>(2);
            
            try {
                // Oletetaan että palvelu tallentaa muodossa "aikaleima : teksti"
                String[] parts = rawEntry.split(" : ", 2);
                if (parts.length == 2) {
                    long timestamp = Long.parseLong(parts[0]);
                    String dateString = sdf.format(new Date(timestamp));

                    datum.put("line1", dateString);      // Yläriville pvm ja klo
                    datum.put("line2", parts[1]);        // Alariville itse viesti
                } else {
                    datum.put("line1", "Spam-raportti");
                    datum.put("line2", rawEntry);
                }
            } catch (Exception e) {
                datum.put("line1", "Spam-raportti");
                datum.put("line2", rawEntry);
            }
            
            spamDataList.add(datum);
        }

        adapter.notifyDataSetChanged();
    }
}