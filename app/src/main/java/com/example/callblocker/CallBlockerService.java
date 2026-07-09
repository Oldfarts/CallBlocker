package com.example.callblocker;

import android.content.SharedPreferences;
import android.telecom.CallScreeningService;
import android.telecom.Call;
import android.telecom.CallScreeningService.CallResponse;

import java.util.HashSet;
import java.util.Set;

public class CallBlockerService extends CallScreeningService {

    @Override
    public void onScreenCall(Call.Details callDetails) {

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        boolean blockForeign = prefs.getBoolean("blockForeign", false);
        boolean blockSpam = prefs.getBoolean("blockSpam", false);

        // Sallitut numerot (poikkeukset)
        Set<String> allowedNumbers = prefs.getStringSet("allowedNumbers", new HashSet<>());

        // Sallitut maatunnukset (poikkeukset)
        Set<String> allowedCountries = prefs.getStringSet("allowedCountries", new HashSet<>());

        // Kielletyt numerot
        Set<String> blockedNumbers = prefs.getStringSet("blockedNumbers", new HashSet<>());

        String number = "";
        if (callDetails.getHandle() != null) {
            number = callDetails.getHandle().getSchemeSpecificPart();
        }

        boolean shouldBlock = false;

        // Tuntematon numero
        if (number == null || number.trim().isEmpty()) {
            shouldBlock = true;
            addLog(number, "Tuntematon numero");
        }

        // Spam-soittaja
        if (blockSpam && isSpam(callDetails)) {
            shouldBlock = true;
            addSpamReport(number);
            addLog(number, "Häirikkösoittaja (spam-heuristiikka)");
        }

        // Sallittu numero
        if (allowedNumbers.contains(number)) {
            shouldBlock = false;
            addLog(number, "Sallittu numero (poikkeus)");
        }

        // Kielletty numero
        if (blockedNumbers.contains(number)) {
            shouldBlock = true;
            addLog(number, "Numero on kiellettyjen listalla");
        }

        // Ulkomainen numero
        if (blockForeign && !isFinnishNumber(number)) {

            boolean allowedCountry = false;

            for (String code : allowedCountries) {
                if (number.startsWith(code)) {
                    allowedCountry = true;
                    break;
                }
            }

            if (!allowedCountry) {
                shouldBlock = true;
                addLog(number, "Ulkomainen numero — maatunnus ei sallittu");
            }
        }

        CallResponse.Builder builder = new CallResponse.Builder();

        if (shouldBlock) {
            builder.setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(true);
        } else {
            builder.setDisallowCall(false);
        }

        respondToCall(callDetails, builder.build());
    }

    private boolean isSpam(Call.Details details) {

        String number = "";
        if (details.getHandle() != null) {
            number = details.getHandle().getSchemeSpecificPart();
        }

        if (number == null || number.isEmpty()) return true;

        boolean foreign = !isFinnishNumber(number);

        boolean shortCallHeuristic =
                details.getCallDirection() == Call.Details.DIRECTION_INCOMING;

        return foreign && shortCallHeuristic;
    }

    private boolean isFinnishNumber(String number) {
        if (number == null || number.trim().isEmpty()) return false;

        // Suomi sallitaan aina
        if (number.startsWith("+358")) return true;

        // Kotimaiset GSM-prefixit
        String[] prefixes = {
                "040", "041", "042", "043", "044", "045", "046", "049",
                "050", "051", "052", "053", "054", "055", "056", "059"
        };

        for (String p : prefixes) {
            if (number.startsWith(p)) return true;
        }

        return false;
    }

    private void addLog(String number, String reason) {
        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        Set<String> logs = prefs.getStringSet("entries", new HashSet<>());

        String entry = "Numero: " + number + " — Syy: " + reason;

        logs.add(entry);
        prefs.edit().putStringSet("entries", logs).apply();
    }

    private void addSpamReport(String number) {
        SharedPreferences prefs = getSharedPreferences("spam", MODE_PRIVATE);
        Set<String> spam = prefs.getStringSet("entries", new HashSet<>());
        spam.add(number);
        prefs.edit().putStringSet("entries", spam).apply();
    }
}
