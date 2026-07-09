package com.example.callblocker;

import android.app.role.RoleManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CallBlocker";

    private Switch switchBlockForeign;
    private Switch switchBlockSpam;
    private Switch switchCallScreening;

    private TextView titleCallScreening;
    private TextView titleForeign;
    private TextView titleSpam;

    private Button saveBtn;
    private Button logBtn;
    private Button spamBtn;

    private Button blockedNumbersBtn;
    private Button allowedCountriesBtn;
    private Button allowedNumbersBtn;

    private SharedPreferences prefs;
    private RoleManager roleManager;
    private ActivityResultLauncher<Intent> roleLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        initViews();

        roleManager = getSystemService(RoleManager.class);

        roleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    boolean active = isCallScreeningActive();
                    Log.d(TAG, "Role result = " + active);

                    switchCallScreening.setChecked(active);
                    updateSwitchColor(switchCallScreening, titleCallScreening);
                }
        );

        loadSettings();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean active = isCallScreeningActive();
        Log.d(TAG, "onResume: Role held = " + active);

        // Päivitetään kytkin vastaamaan todellista tilannetta ilman pelkoa sivuvaikutuksista
        switchCallScreening.setChecked(active);
        updateSwitchColor(switchCallScreening, titleCallScreening);
    }

    private void initViews() {
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
    }

    private void loadSettings() {
        switchBlockForeign.setChecked(prefs.getBoolean("blockForeign", false));
        switchBlockSpam.setChecked(prefs.getBoolean("blockSpam", false));

        boolean active = isCallScreeningActive();
        Log.d(TAG, "Role held = " + active);
        switchCallScreening.setChecked(active);

        updateSwitchColor(switchBlockForeign, titleForeign);
        updateSwitchColor(switchBlockSpam, titleSpam);
        updateSwitchColor(switchCallScreening, titleCallScreening);
    }

    private void setupListeners() {
        // 🔥 KORJATTU: Käytetään OnClickListeneria, jotta koodilla tehdyt setChecked-kutsut eivät riko logiikkaa
        switchCallScreening.setOnClickListener(v -> {
            boolean checked = switchCallScreening.isChecked();

            if (checked) {
                requestCallScreeningRole();
            } else {
                Toast.makeText(
                        this,
                        "Poista palvelu Androidin asetuksista (Oletussovellukset)",
                        Toast.LENGTH_LONG
                ).show();

                // Palautetaan kytkin takaisin päälle, koska roolia ei voi poistaa sovelluksen sisältä
                switchCallScreening.setChecked(true);
            }
            updateSwitchColor(switchCallScreening, titleCallScreening);
        });

        switchBlockForeign.setOnCheckedChangeListener(
                (b, c) -> updateSwitchColor(switchBlockForeign, titleForeign)
        );

        switchBlockSpam.setOnCheckedChangeListener(
                (b, c) -> updateSwitchColor(switchBlockSpam, titleSpam)
        );

        saveBtn.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean("blockForeign", switchBlockForeign.isChecked())
                    .putBoolean("blockSpam", switchBlockSpam.isChecked())
                    .apply();

            Toast.makeText(this, "Asetukset tallennettu", Toast.LENGTH_SHORT).show();
        });

        blockedNumbersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, BlockedNumbersActivity.class)));

        allowedCountriesBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AllowedCountriesActivity.class)));

        allowedNumbersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AllowedNumbersActivity.class)));

        logBtn.setOnClickListener(v ->
                startActivity(new Intent(this, LogActivity.class)));

        spamBtn.setOnClickListener(v ->
                startActivity(new Intent(this, SpamReportActivity.class)));
    }

    private void requestCallScreeningRole() {
        if (roleManager == null) return;

        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
            roleLauncher.launch(intent);
        } else {
            Toast.makeText(
                    this,
                    "Puhelunestopalvelu ei ole saatavilla",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private boolean isCallScreeningActive() {
        return roleManager != null && roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
    }

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