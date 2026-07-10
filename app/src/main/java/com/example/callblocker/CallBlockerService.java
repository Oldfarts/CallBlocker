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

        // 🔥 REFAKTROITU 1. Kielletty numero (Tukee nyt +358401234??? -hakua lennosta)
        String cleanedIncoming = number.replace(" ", ""); // Siivotaan saapuvan numeron välilyönnit varmuudeksi

        for (String blockedPattern : blockedNumbers) {
            String pattern = blockedPattern.replace(" ", ""); // Siivotaan listalla olevan kuvion välilyönnit

            if (pattern.contains("?")) {
                // Otetaan talteen vain osa ennen ensimmäistä kysymysmerkkiä (esim. "+358401234")
                String prefix = pattern.split("\\?")[0];

                // Jos saapuva numero alkaa tällä alkuosalla -> ESTETÄÄN
                if (cleanedIncoming.startsWith(prefix)) {
                    block = true;
                    logReason = number + " estetty: kielletty numerosarja (" + blockedPattern + ")";
                    break; // Löytyi osuma, lopetetaan loop
                }
            } else {
                // Jos kuvio ei sisällä kysymysmerkkejä, käytetään alkuperäistä NumberUtils-tarkistusta tai suoraan equalsia
                if (cleanedIncoming.equals(pattern)) {
                    block = true;
                    logReason = number + " estetty: kielletty numero";
                    break; // Löytyi osuma, lopetetaan loop
                }
            }
        }

        // 2. Ulkomaiset numerot
        if (!block && blockForeign && !NumberUtils.isFinnishNumber(number)) {
            block = true;
            logReason = number + " estetty: ulkomainen numero";
        }
        // 3. Spam (tulevaisuuden laajennus)
        else if (!block && blockSpam) {
            // Tähän tulee myöhemmin tietokantatarkistus.
            // Seuraa samaa logiikkaa: block = true ja logReason = ...
        }

        // Rakennetaan vastaus
        CallResponse.Builder response = new CallResponse.Builder();

        if (block) {
            Log.d(TAG, "CALL BLOCKED. Reason: " + logReason);
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