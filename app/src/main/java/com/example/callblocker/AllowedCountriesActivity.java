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

    private static final String PREF_KEY = "allowedCountries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowed_countries);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        input = findViewById(R.id.allowedCountryInput);
        addBtn = findViewById(R.id.addAllowedCountryBtn);
        removeBtn = findViewById(R.id.removeAllowedCountryBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.allowedCountriesList);

        // Lataa sallitut maatunnukset
        Set<String> stored = prefs.getStringSet(PREF_KEY, new HashSet<>());
        list = new ArrayList<>(stored);

        // ⭐ Lisää +358 automaattisesti, jos puuttuu
        if (!list.contains("+358")) {
            list.add("+358");
            editor.putStringSet(PREF_KEY, new HashSet<>(list)).apply();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // ⭐ Lisää sallittu maatunnus
        addBtn.setOnClickListener(v -> {
            String code = input.getText().toString().trim();

            if (!isValidCountryCode(code)) {
                Toast.makeText(this, "Virheellinen maatunnus", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!list.contains(code)) {
                list.add(code);
                editor.putStringSet(PREF_KEY, new HashSet<>(list)).apply();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Maatunnus sallittu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Maatunnus on jo sallittu", Toast.LENGTH_SHORT).show();
            }
        });

        // ⭐ Poista sallittu maatunnus
        removeBtn.setOnClickListener(v -> {
            String code = input.getText().toString().trim();

            if (code.equals("+358")) {
                Toast.makeText(this, "Suomen maatunnusta ei voi poistaa", Toast.LENGTH_SHORT).show();
                return;
            }

            if (list.remove(code)) {
                editor.putStringSet(PREF_KEY, new HashSet<>(list)).apply();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Maatunnus poistettu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Maatunnusta ei löytynyt", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // ⭐ Validointi: sallii vain muodot +358, +44, +1 jne.
    private boolean isValidCountryCode(String code) {
        return code.startsWith("+") && code.substring(1).matches("\\d+");
    }
}
