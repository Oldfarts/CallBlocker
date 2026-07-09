package com.example.callblocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.role.RoleManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Switch switchBlockForeign, switchBlockSpam, switchCallScreening;

    private Button saveBtn, logBtn, spamBtn;

    // Valikkonapit
    private Button blockedNumbersBtn, allowedCountriesBtn, allowedNumbersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Asetuskytkimet
        switchBlockForeign = findViewById(R.id.switchBlockForeign);
        switchBlockSpam = findViewById(R.id.switchBlockSpam);
        switchCallScreening = findViewById(R.id.switchCallScreening);

        // Napit
        saveBtn = findViewById(R.id.saveBtn);
        logBtn = findViewById(R.id.logBtn);
        spamBtn = findViewById(R.id.spamBtn);

        blockedNumbersBtn = findViewById(R.id.blockedNumbersBtn);
        allowedCountriesBtn = findViewById(R.id.allowedCountriesBtn);
        allowedNumbersBtn = findViewById(R.id.allowedNumbersBtn);

        // Lataa asetukset
        switchBlockForeign.setChecked(prefs.getBoolean("blockForeign", false));
        switchBlockSpam.setChecked(prefs.getBoolean("blockSpam", false));

        // Puhelunestopalvelun tila
        RoleManager rm = getSystemService(RoleManager.class);
        boolean isActive = rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
        switchCallScreening.setChecked(isActive);

        // Puhelunestopalvelu päälle/pois
        switchCallScreening.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // Pyydä puhelunestopalvelun roolia
                Intent intent = rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
                startActivity(intent);
            } else {
                // Roolia ei voi poistaa ohjelmallisesti useimmissa Android-versioissa
                Toast.makeText(
                        MainActivity.this,
                        "Puhelunestopalvelua ei voi poistaa sovelluksesta.\n" +
                                "Poista se Androidin asetuksista:\n" +
                                "Asetukset → Sovellukset → Oletussovellukset → Puhelunestopalvelu",
                        Toast.LENGTH_LONG
                ).show();

                // Päivitä kytkin oikeaan tilaan
                boolean active = rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
                switchCallScreening.setChecked(active);
            }
        });

        // Tallenna asetukset
        saveBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("blockForeign", switchBlockForeign.isChecked());
            editor.putBoolean("blockSpam", switchBlockSpam.isChecked());

            editor.apply();

            Toast.makeText(MainActivity.this, "Asetukset tallennettu", Toast.LENGTH_SHORT).show();
        });

        // Valikot
        blockedNumbersBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BlockedNumbersActivity.class)));

        allowedCountriesBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AllowedCountriesActivity.class)));

        allowedNumbersBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AllowedNumbersActivity.class)));

        // Lokit
        logBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LogActivity.class)));

        spamBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SpamReportActivity.class)));
    }
}
