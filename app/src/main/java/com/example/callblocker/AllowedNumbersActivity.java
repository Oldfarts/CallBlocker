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

public class AllowedNumbersActivity extends AppCompatActivity {

    private EditText input;
    private Button addBtn, removeBtn, backBtn;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    // Pidetään muistissa listalta valitun rivin indeksi (-1 = ei valintaa)
    private int selectedPosition = -1;

    private static final int MAX_ALLOWED_NUMBERS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowed_numbers);

        input = findViewById(R.id.allowedNumberInput);
        addBtn = findViewById(R.id.addAllowedNumberBtn);
        removeBtn = findViewById(R.id.removeAllowedNumberBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.allowedNumbersList);

        // Ladataan olemassa olevat sallitut numerot
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet("allowedNumbers", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // Kosketuslista: Kun riviä klikataan, siirretään se kenttään ja otetaan indeksi talteen
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = list.get(position);
            input.setText(selectedItem);
            selectedPosition = position;
        });

        // Lisää-painikkeen logiikka
        addBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

            String processedNumber = convertToPattern(inputText);

            if (list.contains(processedNumber)) {
                Toast.makeText(this, "Numero on jo sallittujen listalla", Toast.LENGTH_SHORT).show();
                return;
            }

            if (list.size() >= MAX_ALLOWED_NUMBERS) {
                Toast.makeText(this, "Liikaa numeroita (max " + MAX_ALLOWED_NUMBERS + ")", Toast.LENGTH_LONG).show();
                return;
            }

            list.add(processedNumber);
            adapter.notifyDataSetChanged();

            saveToPrefs(); // Tallennetaan korjatulla metodilla

            input.setText("");
            selectedPosition = -1;
            Toast.makeText(this, "Lisätty sallittuihin: " + processedNumber, Toast.LENGTH_SHORT).show();
        });

        // Poista-painikkeen logiikka (Kosketuslistatuella)
        removeBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            boolean removed = false;

            // Vaihtoehto A: Käyttäjä klikkasi riviä suoraan listasta
            if (selectedPosition != -1 && selectedPosition < list.size()) {
                list.remove(selectedPosition);
                removed = true;
            }
            // Vaihtoehto B: Käyttäjä kirjoitti numeron käsin kenttään koskematta listaan
            else if (!inputText.isEmpty()) {
                String processedNumber = convertToPattern(inputText);
                removed = list.remove(processedNumber);
            }

            if (removed) {
                saveToPrefs(); // Tallennetaan muutokset levylle
                adapter.notifyDataSetChanged(); // Päivitetään UI-lista heti
                input.setText("");
                selectedPosition = -1; // Välttämätön nollaus, ettei vanha indeksi jää kummittelemaan
                Toast.makeText(this, "Poistettu sallituista", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Valitse numero listalta tai kirjoita se kenttään poistaaksesi", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // 🔥 KORJATTU TALLENNUSMETODI: Luo aina uuden HashSet-olion levylle kirjoitusta varten
    private void saveToPrefs() {
        Set<String> newSet = new HashSet<>(list);
        getSharedPreferences("settings", MODE_PRIVATE).edit()
                .putStringSet("allowedNumbers", newSet)
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