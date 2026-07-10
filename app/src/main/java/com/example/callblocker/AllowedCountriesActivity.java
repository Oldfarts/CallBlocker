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

public class AllowedCountriesActivity extends AppCompatActivity {

    private EditText input;
    private Button addBtn, removeBtn, backBtn;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    // 🔥 UUSI: Pidetään muistissa listalta valitun rivin indeksi (-1 = ei valintaa)
    private int selectedPosition = -1;

    private static final String PREF_KEY = "allowedCountries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowed_countries);

        input = findViewById(R.id.allowedCountryInput);
        addBtn = findViewById(R.id.addAllowedCountryBtn);
        removeBtn = findViewById(R.id.removeAllowedCountryBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.allowedCountriesList);

        // Lataa sallitut maatunnukset
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet(PREF_KEY, new HashSet<>());
        list = new ArrayList<>(stored);

        // ⭐ Lisää +358 automaattisesti, jos puuttuu
        if (!list.contains("+358")) {
            list.add("+358");
            saveToPrefs(); // Tallennetaan heti uudella korjatulla metodilla
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // 🔥 UUSI: Kosketuslista (Listan klikkauskuuntelija)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = list.get(position);
            input.setText(selectedItem); // Siirretään valittu maatunnus tekstikenttään
            selectedPosition = position; // Otetaan paikka talteen
        });

        // ⭐ Lisää sallittu maatunnus
        addBtn.setOnClickListener(v -> {
            String code = input.getText().toString().trim();

            if (!isValidCountryCode(code)) {
                Toast.makeText(this, "Virheellinen maatunnus", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!list.contains(code)) {
                list.add(code);
                adapter.notifyDataSetChanged();
                saveToPrefs(); // Tallennetaan muutokset levylle
                
                input.setText("");
                selectedPosition = -1; // Nollataan kosketusvalinta
                Toast.makeText(this, "Maatunnus sallittu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Maatunnus on jo sallittu", Toast.LENGTH_SHORT).show();
            }
        });

        // ⭐ PÄIVITETTY: Poista-painikkeen logiikka kosketuslistatuella
        removeBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            String codeToRemove = "";
            
            // Selvitetään mitä tunnusta ollaan poistamassa
            if (selectedPosition != -1 && selectedPosition < list.size()) {
                codeToRemove = list.get(selectedPosition);
            } else if (!inputText.isEmpty()) {
                codeToRemove = inputText;
            }

            // Suojalukitus: Estetään Suomen poistaminen riippumatta siitä, miten poistoa yritettiin
            if (codeToRemove.equals("+358")) {
                Toast.makeText(this, "Suomen maatunnusta ei voi poistaa", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean removed = false;

            // Vaihtoehto A: Poisto suoraan listaklikkauksen indeksillä
            if (selectedPosition != -1 && selectedPosition < list.size()) {
                list.remove(selectedPosition);
                removed = true;
            }
            // Vaihtoehto B: Poisto käsin kirjoitetun tekstin perusteella
            else if (!codeToRemove.isEmpty()) {
                removed = list.remove(codeToRemove);
            }

            if (removed) {
                saveToPrefs(); // Pakotetaan uusi HashSet levylle lennossa
                adapter.notifyDataSetChanged(); // Päivitetään näkymä
                input.setText("");
                selectedPosition = -1; // Muista nollata indeksi poiston jälkeen!
                Toast.makeText(this, "Maatunnus poistettu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Valitse maatunnus listalta tai kirjoita se kenttään", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // 🔥 KORJATTU TALLENNUSMETODI: Luo uuden HashSet-olion varmistaen tallennuksen toimivuuden
    private void saveToPrefs() {
        Set<String> newSet = new HashSet<>(list);
        getSharedPreferences("settings", MODE_PRIVATE).edit()
                .putStringSet(PREF_KEY, newSet)
                .apply();
    }

    // ⭐ Validointi: sallii vain muodot +358, +44, +1 jne.
    private boolean isValidCountryCode(String code) {
        return code.startsWith("+") && code.substring(1).matches("\\d+");
    }
}