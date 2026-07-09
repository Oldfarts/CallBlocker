package com.example.callblocker;

import android.content.SharedPreferences;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class CallBlockerService extends CallScreeningService {

    private static final String TAG = "CallBlocker";

    @Override
    public void onScreenCall(Call.Details callDetails) {
        Log.d(TAG, "CallScreeningService STARTED");

        if (callDetails == null || callDetails.getHandle() == null) {
            // Varmistetaan ettei sovellus kaadu, jos puhelun tiedot ovat tyhjät
            respondToCall(callDetails, new CallResponse.Builder().setDisallowCall(false).build());
            return;
        }

        String number = callDetails.getHandle().getSchemeSpecificPart();
        Log.d(TAG, "Incoming number = " + number);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean blockForeign = prefs.getBoolean("blockForeign", false);
        boolean blockSpam = prefs.getBoolean("blockSpam", false);

        // Luodaan uusi setti, jotta viittaukset eivät sekoita Androidin välimuistia
        Set<String> blockedNumbers = new HashSet<>(prefs.getStringSet("blockedNumbers", new HashSet<>()));

        boolean block = false;
        String logReason = "";

        // 1. Kielletty numero
        if (NumberUtils.containsNumber(blockedNumbers, number)) {
            block = true;
            logReason = number + " estetty: kielletty numero";
        }
        // 2. Ulkomaiset numerot
        else if (blockForeign && !NumberUtils.isFinnishNumber(number)) {
            block = true;
            logReason = number + " estetty: ulkomainen numero";
        }
        // 3. Spam (tulevaisuuden laajennus)
        else if (blockSpam) {
            // Tähän tulee myöhemmin tietokantatarkistus.
            // Jos tunnistetaan spamiksi, block = true ja logReason = ...
        }

        // Rakennetaan vastaus
        CallResponse.Builder response = new CallResponse.Builder();

        if (block) {
            Log.d(TAG, "CALL BLOCKED");
            response.setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false) // Näkyy puhelimen omassa lokissa estettynä
                    .setSkipNotification(true);

            // Tehdään lokikirjaus taustalla, ettei se viivästytä puhelun käsittelyä
            final String finalLogReason = logReason;
            new Thread(() -> addLog(finalLogReason)).start();

        } else {
            Log.d(TAG, "CALL ALLOWED");
            response.setDisallowCall(false);
        }

        respondToCall(callDetails, response.build());
    }

    private synchronized void addLog(String text) {
        if (text == null || text.isEmpty()) return;

        SharedPreferences logPrefs = getSharedPreferences("log", MODE_PRIVATE);
        Set<String> logs = new HashSet<>(logPrefs.getStringSet("entries", new HashSet<>()));

        // Lisätään uusi lokirivi
        logs.add(System.currentTimeMillis() + " : " + text);

        // commit() taustasäikeessä varmistaa, että tiedot kirjoitetaan heti levylle
        logPrefs.edit().putStringSet("entries", logs).commit();
    }
}