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

    private static final int MAX_ALLOWED_NUMBERS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowed_numbers);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        input = findViewById(R.id.allowedNumberInput);
        addBtn = findViewById(R.id.addAllowedNumberBtn);
        removeBtn = findViewById(R.id.removeAllowedNumberBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.allowedNumbersList);

        Set<String> stored = prefs.getStringSet("allowedNumbers", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // Lisää sallittu numero / range
        addBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) return;

            ArrayList<String> expanded = expandRange(inputText);

            if (list.size() + expanded.size() > MAX_ALLOWED_NUMBERS) {
                Toast.makeText(this,
                        "Liikaa numeroita (max " + MAX_ALLOWED_NUMBERS + ")",
                        Toast.LENGTH_LONG).show();
                return;
            }

            list.addAll(expanded);
            adapter.notifyDataSetChanged();
            editor.putStringSet("allowedNumbers", new HashSet<>(list)).apply();

            Toast.makeText(this, "Numero(t) lisätty sallittuihin", Toast.LENGTH_SHORT).show();
        });

        // Poista sallittu numero / range
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
            editor.putStringSet("allowedNumbers", new HashSet<>(list)).apply();

            if (removedAny) {
                Toast.makeText(this, "Numero(t) poistettu sallituista", Toast.LENGTH_SHORT).show();
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
                result.add(input);
                return result;
            }

            for (int i = startNum; i <= endNum; i++) {
                result.add(prefix + i);
            }

        } catch (Exception e) {
            result.add(input);
        }

        return result;
    }
}
