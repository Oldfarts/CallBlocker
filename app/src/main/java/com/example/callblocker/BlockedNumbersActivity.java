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

        // Lataa tallennetut numerot
        Set<String> stored = prefs.getStringSet("blockedNumbers", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // Lisää numero
        addBtn.setOnClickListener(v -> {
            String num = input.getText().toString().trim();
            if (!num.isEmpty()) {
                list.add(num);
                adapter.notifyDataSetChanged();

                editor.putStringSet("blockedNumbers", new HashSet<>(list)).apply();
                Toast.makeText(this, "Numero lisätty", Toast.LENGTH_SHORT).show();
            }
        });

        // Poista numero
        removeBtn.setOnClickListener(v -> {
            String num = input.getText().toString().trim();
            if (list.remove(num)) {
                adapter.notifyDataSetChanged();
                editor.putStringSet("blockedNumbers", new HashSet<>(list)).apply();
                Toast.makeText(this, "Numero poistettu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Numero ei löytynyt", Toast.LENGTH_SHORT).show();
            }
        });

        // Palaa päävalikkoon
        backBtn.setOnClickListener(v -> finish());
    }
}
