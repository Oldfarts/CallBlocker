package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlockedNumbersActivity extends AppCompatActivity {

    private EditText input;
    private Button addBtn, removeBtn, backBtn;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    private static final int MAX_BLOCKED_NUMBERS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_numbers);

        input = findViewById(R.id.blockedNumberInput);
        addBtn = findViewById(R.id.addBlockedNumberBtn);
        removeBtn = findViewById(R.id.removeBlockedNumberBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.blockedNumbersList);

        // Ladataan olemassa olevat asetukset
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet("blockedNumbers", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // Lisää kielletty numero tai sarja
        addBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

            // Muunnetaan syöte älykkäästi (esim. range -> kysymysmerkeiksi)
            String processedNumber = convertToPattern(inputText);

            if (list.contains(processedNumber)) {
                Toast.makeText(this, "Numero tai sarja on jo listalla", Toast.LENGTH_SHORT).show();
                return;
            }

            if (list.size() >= MAX_BLOCKED_NUMBERS) {
                Toast.makeText(this, "Liikaa numeroita (max " + MAX_BLOCKED_NUMBERS + ")", Toast.LENGTH_LONG).show();
                return;
            }

            list.add(processedNumber);
            adapter.notifyDataSetChanged();
            
            // 🔥 TALLENNUS: prefs.edit() kutsutaan suoraan tässä lennossa tietoturvan vuoksi
            getSharedPreferences("settings", MODE_PRIVATE).edit()
                    .putStringSet("blockedNumbers", new HashSet<>(list))
                    .apply();

            input.setText(""); // Tyhjennetään kenttä syötön jälkeen
            Toast.makeText(this, "Lisätty kiellettyihin: " + processedNumber, Toast.LENGTH_SHORT).show();
        });

        // Poista kielletty numero tai sarja
        removeBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

            String processedNumber = convertToPattern(inputText);

            if (list.remove(processedNumber)) {
                adapter.notifyDataSetChanged();
                
                getSharedPreferences("settings", MODE_PRIVATE).edit()
                        .putStringSet("blockedNumbers", new HashSet<>(list))
                        .apply();

                input.setText("");
                Toast.makeText(this, "Poistettu kielletyistä", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Numeroa tai sarjaa ei löytynyt listasta", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    /**
     * Älykäs muunnos: Muuttaa esim "+358401234000-999" -> "+358401234???"
     * Jos kyseessä on tavallinen numero tai valmis kysymysmerkkijono, palauttaa sen sellaisenaan.
     */
    private String convertToPattern(String input) {
        if (!input.contains("-")) {
            return input; // Tavallinen numero tai jo valmiiksi kysymysmerkkejä sisältävä jono
        }

        try {
            String[] parts = input.split("-");
            String start = parts[0].trim();
            String endSuffix = parts[1].trim();

            // Lasketaan kuinka monta merkkiä lopusta korvataan kysymysmerkeillä
            int suffixLength = endSuffix.length();
            
            if (suffixLength >= start.length()) {
                return input; // Virheellinen range, palautetaan alkuperäinen
            }

            // Otetaan talteen alkuosa (esim. "+358401234")
            String prefix = start.substring(0, start.length() - suffixLength);
            
            // Rakennetaan kysymysmerkit perään (esim. "???")
            StringBuilder wildcards = new StringBuilder();
            for (int i = 0; i < suffixLength; i++) {
                wildcards.append("?");
            }

            return prefix + wildcards.toString();

        } catch (Exception e) {
            return input; // Virhetilanteessa ei rikota mitään, tallennetaan sellaisenaan
        }
    }
}