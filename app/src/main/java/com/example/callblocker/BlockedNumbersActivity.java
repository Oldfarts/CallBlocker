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

    // 🔥 UUSI: Pidetään muistissa listalta valitun neron indeksi (-1 tarkoittaa, että mitään ei ole valittu)
    private int selectedPosition = -1;

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

        // 🔥 UUSI: Kosketuslista (Listan klikkauskuuntelija)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Otetaan valitun rivin teksti talteen
            String selectedItem = list.get(position);

            // Syötetään se suoraan tekstikenttään käyttäjälle näkyviin
            input.setText(selectedItem);

            // Tallennetaan klikatun rivin indeksi muistiin poistoa varten
            selectedPosition = position;
        });

        // Lisää kielletty numero tai sarja
        addBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

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

            saveToPrefs();

            input.setText("");
            selectedPosition = -1; // Nollataan valinta
            Toast.makeText(this, "Lisätty: " + processedNumber, Toast.LENGTH_SHORT).show();
        });

        // 🔥 PÄIVITETTY: Poista-painikkeen älykäs logiikka
        removeBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            boolean removed = false;

            // Vaihtoehto A: Jos käyttäjä on koskettanut listalta jotain riviä
            if (selectedPosition != -1 && selectedPosition < list.size()) {
                list.remove(selectedPosition);
                removed = true;
            }
            // Vaihtoehto B: Jos käyttäjä vain kirjoitti numeron käsin kenttään koskettamatta listaa
            else if (!inputText.isEmpty()) {
                String processedNumber = convertToPattern(inputText);
                removed = list.remove(processedNumber);
            }

            if (removed) {
                adapter.notifyDataSetChanged();
                saveToPrefs();
                input.setText("");
                selectedPosition = -1; // Nollataan valinta poiston jälkeen
                Toast.makeText(this, "Poistettu listalta", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Mitään ei poistettu. Valitse numero listalta tai kirjoita se kenttään.", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // Apumetodi tallennuksen selkeyttämiseksi
    private void saveToPrefs() {
        getSharedPreferences("settings", MODE_PRIVATE).edit()
                .putStringSet("blockedNumbers", new HashSet<>(list))
                .apply();
    }

    private String convertToPattern(String input) {
        if (!input.contains("-")) {
            return input;
        }
        try {
            String[] parts = input.split("-");
            String start = parts[0].trim();
            String endSuffix = parts[1].trim();
            int suffixLength = endSuffix.length();

            if (suffixLength >= start.length()) return input;

            String prefix = start.substring(0, start.length() - suffixLength);
            StringBuilder wildcards = new StringBuilder();
            for (int i = 0; i < suffixLength; i++) {
                wildcards.append("?");
            }
            return prefix + wildcards.toString();
        } catch (Exception e) {
            return input;
        }
    }
}