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

        Set<String> allowedNumbers =
                new HashSet<>(prefs.getStringSet("allowedNumbers", new HashSet<>()));
        Set<String> blockedCountries =
                new HashSet<>(prefs.getStringSet("blockedCountries", new HashSet<>()));

        String number = "";
        if (callDetails.getHandle() != null) {
            number = callDetails.getHandle().getSchemeSpecificPart();
        }

        boolean shouldBlock = false;

        // Estetty tuntematon numero
        if (number == null || number.trim().isEmpty()) {
            shouldBlock = true;
            addLog("Estetty tuntematon numero");
        }

        // Estetty epäilyttävä puhelu (heuristiikka)
        if (blockSpam && isSpam(callDetails)) {
            shouldBlock = true;
            addSpamReport(number);
            addLog("Estetty epäilyttävä puhelu: " + number);
        }

        // Sallitut numerot ohittavat estot
        if (allowedNumbers.contains(number)) {
            shouldBlock = false;
        }

        // Estetty ulkomainen numero
        if (blockForeign && !isFinnishNumber(number)) {
            shouldBlock = true;
            addLog("Estetty ulkomainen numero: " + number);
        }

        // Estetty mustan listan numero
        for (String code : blockedCountries) {
            if (number != null && number.startsWith(code)) {
                shouldBlock = true;
                addLog("Estetty mustan listan numero: " + number);
                break;
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

        int status = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            status = details.getCallerNumberVerificationStatus();
        }

        boolean unverified = (status == 0 || status == 2);

        String number = "";
        if (details.getHandle() != null) {
            number = details.getHandle().getSchemeSpecificPart();
        }

        boolean foreign = !isFinnishNumber(number);

        return unverified && foreign;
    }

    private boolean isFinnishNumber(String number) {
        if (number == null || number.trim().isEmpty()) return false;

        if (number.startsWith("+358")) return true;

        String[] prefixes = {
                "040", "041", "042", "043", "044", "045", "046", "049",
                "050", "051", "052", "053", "054", "055", "056", "059"
        };

        for (String p : prefixes) {
            if (number.startsWith(p)) return true;
        }

        return false;
    }

    private void addLog(String entry) {
        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        Set<String> logs =
                new HashSet<>(prefs.getStringSet("entries", new HashSet<>()));
        logs.add(entry);
        prefs.edit().putStringSet("entries", logs).apply();
    }

    private void addSpamReport(String number) {
        SharedPreferences prefs = getSharedPreferences("spam", MODE_PRIVATE);
        Set<String> spam =
                new HashSet<>(prefs.getStringSet("entries", new HashSet<>()));
        spam.add(number);
        prefs.edit().putStringSet("entries", spam).apply();
    }
}
