package com.example.callblocker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SpamReportActivity extends AppCompatActivity {

    private ListView listView;
    private Button clearBtn, backBtn;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam);

        SharedPreferences prefs = getSharedPreferences("spam", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        listView = findViewById(R.id.spamList);
        clearBtn = findViewById(R.id.clearSpamBtn);
        backBtn = findViewById(R.id.backBtn);

        Set<String> stored = prefs.getStringSet("entries", new HashSet<>());
        list = new ArrayList<>(stored);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        clearBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Tyhjennä spam-raportti")
                    .setMessage("Haluatko varmasti tyhjentää spam-raportin?")
                    .setPositiveButton("Kyllä", (dialog, which) -> {
                        list.clear();
                        adapter.notifyDataSetChanged();
                        editor.putStringSet("entries", new HashSet<>()).apply();
                    })
                    .setNegativeButton("Peruuta", null)
                    .show();
        });

        backBtn.setOnClickListener(v -> finish());
    }
}
