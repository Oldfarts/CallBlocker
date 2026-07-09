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

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        input = findViewById(R.id.blockedNumberInput);
        addBtn = findViewById(R.id.addBlockedNumberBtn);
        removeBtn = findViewById(R.id.removeBlockedNumberBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.blockedNumbersList);

        Set<String> stored = prefs.getStringSet("blockedNumbers", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // Lisää kielletty numero / range
        addBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

            ArrayList<String> expanded = expandRange(inputText);

            if (list.size() + expanded.size() > MAX_BLOCKED_NUMBERS) {
                Toast.makeText(this,
                        "Liikaa numeroita (max " + MAX_BLOCKED_NUMBERS + ")",
                        Toast.LENGTH_LONG).show();
                return;
            }

            list.addAll(expanded);
            adapter.notifyDataSetChanged();
            editor.putStringSet("blockedNumbers", new HashSet<>(list)).apply();

            Toast.makeText(this, "Numero(t) lisätty kiellettyihin", Toast.LENGTH_SHORT).show();
        });

        // Poista kielletty numero / range
        removeBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

            ArrayList<String> expanded = expandRange(inputText);
            boolean removedAny = false;

            for (String num : expanded) {
                if (list.remove(num)) {
                    removedAny = true;
                }
            }

            adapter.notifyDataSetChanged();
            editor.putStringSet("blockedNumbers", new HashSet<>(list)).apply();

            if (removedAny) {
                Toast.makeText(this, "Numero(t) poistettu kielletyistä", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Numeroa ei löytynyt listasta", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // Tukee sekä yksittäistä numeroa että rangea: +358400740000-999
    private ArrayList<String> expandRange(String input) {
        ArrayList<String> result = new ArrayList<>();

        if (!input.contains("-")) {
            result.add(input);
            return result;
        }

        try {
            String[] parts = input.split("-");
            String start = parts[0].trim();
            String endSuffix = parts[1].trim();

            // Esim: start = +358400740000, endSuffix = 999
            String prefix = start.substring(0, start.length() - endSuffix.length());
            int startNum = Integer.parseInt(start.substring(prefix.length()));
            int endNum = Integer.parseInt(endSuffix);

            if (endNum < startNum) {
                // Jos loppu < alku → käsitellään yksittäisenä
                result.add(input);
                return result;
            }

            for (int i = startNum; i <= endNum; i++) {
                result.add(prefix + i);
            }

        } catch (Exception e) {
            // Virhe syötteessä → käsitellään yksittäisenä numerona
            result.add(input);
        }

        return result;
    }
}
