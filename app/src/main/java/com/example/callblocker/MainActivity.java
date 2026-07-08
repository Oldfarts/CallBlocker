package com.example.callblocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.role.RoleManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Switch switchBlockForeign, switchBlockSpam;
    private EditText allowedInput, blockedInput;
    private Button saveBtn, roleBtn, logBtn, spamBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        switchBlockForeign = findViewById(R.id.switchBlockForeign);
        switchBlockSpam = findViewById(R.id.switchBlockSpam);
        allowedInput = findViewById(R.id.allowedInput);
        blockedInput = findViewById(R.id.blockedInput);
        saveBtn = findViewById(R.id.saveBtn);
        roleBtn = findViewById(R.id.roleBtn);
        logBtn = findViewById(R.id.logBtn);
        spamBtn = findViewById(R.id.spamBtn);

        switchBlockForeign.setChecked(prefs.getBoolean("blockForeign", false));
        switchBlockSpam.setChecked(prefs.getBoolean("blockSpam", false));

        saveBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("blockForeign", switchBlockForeign.isChecked());
            editor.putBoolean("blockSpam", switchBlockSpam.isChecked());

            editor.putStringSet("allowedNumbers", parse(allowedInput.getText().toString()));
            editor.putStringSet("blockedCountries", parse(blockedInput.getText().toString()));

            editor.apply();

            Toast.makeText(MainActivity.this, "Asetukset tallennettu", Toast.LENGTH_SHORT).show();
        });

        roleBtn.setOnClickListener(v -> {
            RoleManager rm = getSystemService(RoleManager.class);
            Intent intent = rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
            startActivity(intent);
        });

        logBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LogActivity.class)));

        spamBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SpamReportActivity.class)));
    }

    private Set<String> parse(String text) {
        Set<String> set = new HashSet<>();
        if (text == null) return set;

        for (String s : text.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) set.add(s);
        }
        return set;
    }
}
