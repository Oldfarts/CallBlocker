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

public class BlockedCountriesActivity extends AppCompatActivity {

    private EditText input;
    private Button addBtn, removeBtn, backBtn;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_countries);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        input = findViewById(R.id.blockedCountryInput);
        addBtn = findViewById(R.id.addBlockedCountryBtn);
        removeBtn = findViewById(R.id.removeBlockedCountryBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.blockedCountriesList);

        // Lataa tallennetut maatunnukset
        Set<String> stored = prefs.getStringSet("blockedCountries", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // Lisää maatunnus
        addBtn.setOnClickListener(v -> {
            String code = input.getText().toString().trim();
            if (!code.isEmpty()) {
                list.add(code);
                adapter.notifyDataSetChanged();
                editor.putStringSet("blockedCountries", new HashSet<>(list)).apply();
                Toast.makeText(this, "Maatunnus lisätty", Toast.LENGTH_SHORT).show();
            }
        });

        // Poista maatunnus
        removeBtn.setOnClickListener(v -> {
            String code = input.getText().toString().trim();
            if (list.remove(code)) {
                adapter.notifyDataSetChanged();
                editor.putStringSet("blockedCountries", new HashSet<>(list)).apply();
                Toast.makeText(this, "Maatunnus poistettu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Maatunnusta ei löytynyt", Toast.LENGTH_SHORT).show();
            }
        });

        // Palaa päävalikkoon
        backBtn.setOnClickListener(v -> finish());
    }
}
