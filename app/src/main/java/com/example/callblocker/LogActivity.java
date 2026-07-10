package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        // 🔥 UUSI: Klikkauskuuntelija lokiriveille numeron poimimista varten
        logList.setOnItemClickListener((parent, view, position, id) -> {
            String clickedRow = displayedLogs.get(position);
            String number = extractNumberFromLog(clickedRow);

            if (number == null || number.isEmpty()) {
                Toast.makeText(this, "Riviltä ei tunnistettu puhelinnumeroa", Toast.LENGTH_SHORT).show();
                return;
            }

            // Avataan valintaikkuna numeron käsittelyyn
            showActionDialog(number);
        });

        clearLogBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
            prefs.edit().remove("entries").apply();

            displayedLogs.clear();
            adapter.notifyDataSetChanged();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    /**
     * 🔥 UUSI: Avaa valikon, jossa käyttäjä voi valita lisätäänkö numero sallittuihin vai kiellettyihin
     */
    private void showActionDialog(String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Valitse toiminto numerolle");
        builder.setMessage(number);

        // Vaihtoehto 1: Lisää sallittuihin (valkoinen lista)
        builder.setPositiveButton("Salli numero", (dialog, which) -> {
            addNumberToPreferences("allowedNumbers", number);
            Toast.makeText(this, "Numero lisätty sallittuihin", Toast.LENGTH_SHORT).show();
        });

        // Vaihtoehto 2: Lisää kiellettyihin (musta lista)
        builder.setNegativeButton("Estä numero", (dialog, which) -> {
            addNumberToPreferences("blockedNumbers", number);
            Toast.makeText(this, "Numero lisätty kiellettyihin", Toast.LENGTH_SHORT).show();
        });

        // Vaihtoehto 3: Peruuta
        builder.setNeutralButton("Peruuta", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 🔥 UUSI: Apumetodi, joka tallentaa numeron haluttuun listaan luomalla uuden HashSet-kopion
     */
    private void addNumberToPreferences(String key, String number) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        Set<String> currentList = new HashSet<>(prefs.getStringSet(key, new HashSet<>()));

        currentList.add(number); // Lisätään numero joukkoon

        // Pakotetaan uusi HashSet levylle lennossa
        prefs.edit().putStringSet(key, currentList).apply();
    }

    /**
     * 🔥 UUSI: Etsii ja palauttaa riviltä ensimmäisen "+"-merkillä alkavan sanan (eli puhelinnumeron)
     */
    private String extractNumberFromLog(String row) {
        if (row == null) return null;

        // Jaetaan rivi välilyöntien perusteella osiin
        String[] words = row.split(" ");
        for (String word : words) {
            // Siivotaan mahdolliset kaksoispisteet tai sulut ympäriltä
            String cleanWord = word.replace(":", "").replace("(", "").replace(")", "").trim();

            // Puhelumme tallentuvat aina kansainvälisessä muodossa +358... jne.
            if (cleanWord.startsWith("+")) {
                return cleanWord;
            }
        }
        return null;
    }

    private void loadLogs() {
        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet("entries", new HashSet<>());

        ArrayList<String> rawLogs = new ArrayList<>(stored);
        Collections.sort(rawLogs, Collections.reverseOrder());

        displayedLogs.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

        for (String rawEntry : rawLogs) {
            try {
                String[] parts = rawEntry.split(" : ", 2);
                if (parts.length == 2) {
                    long timestamp = Long.parseLong(parts[0]);
                    String dateString = sdf.format(new Date(timestamp));
                    displayedLogs.add("[" + dateString + "] " + parts[1]);
                } else {
                    displayedLogs.add(rawEntry);
                }
            } catch (Exception e) {
                displayedLogs.add(rawEntry);
            }
        }

        adapter.notifyDataSetChanged();
    }
}