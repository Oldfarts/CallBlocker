package com.example.callblocker;

import android.content.SharedPreferences;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telecom.Connection;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class CallBlockerService extends CallScreeningService {

    private static final String TAG = "CallBlocker";

    @Override
    public void onScreenCall(Call.Details callDetails) {
        Log.d(TAG, "CallScreeningService STARTED");

        if (callDetails == null || callDetails.getHandle() == null) {
            respondToCall(callDetails, new CallResponse.Builder().setDisallowCall(false).build());
            return;
        }

        String number = callDetails.getHandle().getSchemeSpecificPart();
        Log.d(TAG, "Incoming number = " + number);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean blockForeign = prefs.getBoolean("blockForeign", false);
        boolean blockSpam = prefs.getBoolean("blockSpam", false);

        Set<String> blockedNumbers = new HashSet<>(prefs.getStringSet("blockedNumbers", new HashSet<>()));

        boolean block = false;
        String logReason = "";

        // 1. Kielletty numero (Tukee +358401234??? -hakua lennosta)
        String cleanedIncoming = number.replace(" ", "");

        for (String blockedPattern : blockedNumbers) {
            String pattern = blockedPattern.replace(" ", "");

            if (pattern.contains("?")) {
                String prefix = pattern.split("\\?")[0];
                if (cleanedIncoming.startsWith(prefix)) {
                    block = true;
                    logReason = number + " estetty: kielletty numerosarja (" + blockedPattern + ")";
                    break;
                }
            } else {
                if (cleanedIncoming.equals(pattern)) {
                    block = true;
                    logReason = number + " estetty: kielletty numero";
                    break;
                }
            }
        }

        // 2. Ulkomaiset numerot
        if (!block && blockForeign && !NumberUtils.isFinnishNumber(number)) {
            block = true;
            logReason = number + " estetty: ulkomainen numero";
        }

        // 3. KORJATTU: Automaattinen häirikkötunnistus raakabittien avulla (Yhteensopiva kaikkien SDK-versioiden kanssa)
        else if (!block && blockSpam) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                // A) Tarkistetaan OnePlussan paikallinen spambitti
                int PROPERTY_ASSUMED_SPAM_VALUE = 0x00020000;
                boolean isAssumedSpam = (callDetails.getCallProperties() & PROPERTY_ASSUMED_SPAM_VALUE) != 0;

                // B) UUSI: Tarkistetaan verkon antama varmennus (jos epäonnistunut -> usein spam)
                int verificationStatus = callDetails.getCallerNumberVerificationStatus();
                boolean verificationFailed = (verificationStatus == Connection.VERIFICATION_STATUS_FAILED);

                if (isAssumedSpam || verificationFailed) {
                    block = true;
                    logReason = number + " estetty: Järjestelmän tunnistama häirikkö (Spam/Verification Failed)";
                }
            }
        }

        // Rakennetaan vastaus järjestelmälle
        CallResponse.Builder response = new CallResponse.Builder();

        if (block) {
            Log.d(TAG, "CALL BLOCKED. Reason: " + logReason);
            response.setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false) // Näkyy puhelimen omassa lokissa estettynä
                    .setSkipNotification(true);

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

        logs.add(System.currentTimeMillis() + " : " + text);
        logPrefs.edit().putStringSet("entries", logs).commit();
    }
}