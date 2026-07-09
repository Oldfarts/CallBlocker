package com.example.callblocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.role.RoleManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Switch switchBlockForeign, switchBlockSpam, switchCallScreening;
    private TextView titleCallScreening, titleForeign, titleSpam;

    private Button saveBtn, logBtn, spamBtn;
    private Button blockedNumbersBtn, allowedCountriesBtn, allowedNumbersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // UI-komponentit
        switchBlockForeign = findViewById(R.id.switchBlockForeign);
        switchBlockSpam = findViewById(R.id.switchBlockSpam);
        switchCallScreening = findViewById(R.id.switchCallScreening);

        titleCallScreening = findViewById(R.id.titleCallScreening);
        titleForeign = findViewById(R.id.titleForeign);
        titleSpam = findViewById(R.id.titleSpam);

        saveBtn = findViewById(R.id.saveBtn);
        logBtn = findViewById(R.id.logBtn);
        spamBtn = findViewById(R.id.spamBtn);

        blockedNumbersBtn = findViewById(R.id.blockedNumbersBtn);
        allowedCountriesBtn = findViewById(R.id.allowedCountriesBtn);
        allowedNumbersBtn = findViewById(R.id.allowedNumbersBtn);

        // Lataa asetukset
        switchBlockForeign.setChecked(prefs.getBoolean("blockForeign", false));
        switchBlockSpam.setChecked(prefs.getBoolean("blockSpam", false));

        // Päivitä väritys
        updateSwitchColor(switchBlockForeign, titleForeign);
        updateSwitchColor(switchBlockSpam, titleSpam);

        // Puhelunestopalvelun tila
        RoleManager rm = getSystemService(RoleManager.class);
        boolean isActive = rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
        switchCallScreening.setChecked(isActive);
        updateSwitchColor(switchCallScreening, titleCallScreening);

        // Puhelunestopalvelu päälle/pois
        switchCallScreening.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Intent intent = rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
                startActivity(intent);
                updateSwitchColor(switchCallScreening, titleCallScreening);

            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Puhelunestopalvelua ei voi poistaa sovelluksesta.\n" +
                                "Poista se Androidin asetuksista:\n" +
                                "Asetukset → Sovellukset → Oletussovellukset → Puhelunestopalvelu",
                        Toast.LENGTH_LONG
                ).show();

                boolean active = rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
                switchCallScreening.setChecked(active);
                updateSwitchColor(switchCallScreening, titleCallScreening);
            }
        });

        // Ulkomaiset puhelut
        switchBlockForeign.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSwitchColor(switchBlockForeign, titleForeign));

        // Häirikkösoittajat
        switchBlockSpam.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSwitchColor(switchBlockSpam, titleSpam));

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

    /**
     * Vaihtaa kytkimen ja otsikon värin vihreäksi/punaiseksi.
     */
    private void updateSwitchColor(Switch sw, TextView title) {
        if (sw.isChecked()) {
            sw.setTextColor(getColor(R.color.colorActiveGreen));
            title.setTextColor(getColor(R.color.colorActiveGreen));
        } else {
            sw.setTextColor(getColor(R.color.colorInactiveRed));
            title.setTextColor(getColor(R.color.colorInactiveRed));
        }
    }
}
