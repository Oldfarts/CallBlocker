package com.example.callblocker;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;
import java.util.Set;


public class CallBlockerAccessibilityService extends AccessibilityService {


    private static final String TAG = "CallBlocker";


    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();

        Log.d(TAG,
                "Accessibility service CONNECTED");


        addLog(
                "Accessibility-palvelu käynnistyi"
        );
    }



    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        int type = event.getEventType();


        Log.d(TAG,
                "Accessibility event = " + type);



        /*
         * Kiinnostavat vain näkymän muutokset.
         */

        if(type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                &&
                type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){

            return;
        }



        String packageName = "";

        if(event.getPackageName() != null){

            packageName =
                    event.getPackageName()
                            .toString();

        }



        Log.d(TAG,
                "Package = " + packageName);



        /*
         * Androidin puhelinsovelluksen tarkistus.
         *
         * OnePlus voi käyttää:
         * com.google.android.dialer
         * com.oneplus.dialer
         * com.android.dialer
         */

        boolean isPhoneApp =

                packageName.contains("dialer")
                        ||
                        packageName.contains("phone")
                        ||
                        packageName.contains("telecom");



        if(!isPhoneApp){

            return;

        }



        Log.d(TAG,
                "PHONE UI DETECTED");



        addLog(
                "Puhelinnäkymä havaittu: "
                        + packageName
        );



        SharedPreferences prefs =
                getSharedPreferences(
                        "settings",
                        MODE_PRIVATE
                );



        boolean enabled =
                prefs.getBoolean(
                        "callBlockerEnabled",
                        true
                );


        boolean blockForeign =
                prefs.getBoolean(
                        "blockForeign",
                        false
                );


        boolean blockSpam =
                prefs.getBoolean(
                        "blockSpam",
                        false
                );



        if(!enabled){

            Log.d(TAG,
                    "CallBlocker disabled");

            return;

        }



        /*
         * Tässä vaiheessa EI vielä katkaista puhelua.
         *
         * Ensin varmistetaan että palvelu toimii.
         */


        if(blockForeign || blockSpam){


            Log.d(TAG,
                    "Blocking rules enabled");


            addLog(
                    "Estosäännöt aktiiviset"
            );


            /*
             * Myöhemmin tähän:
             *
             * forceHangup();
             *
             * kun tiedetään OnePlus käyttäytyminen.
             */

        }

    }




    @Override
    public void onInterrupt() {


        Log.d(TAG,
                "Accessibility service interrupted");


        addLog(
                "Accessibility-palvelu keskeytetty"
        );

    }




    private void addLog(String text){


        SharedPreferences prefs =
                getSharedPreferences(
                        "log",
                        MODE_PRIVATE
                );



        Set<String> logs =
                new HashSet<>(
                        prefs.getStringSet(
                                "entries",
                                new HashSet<>()
                        )
                );



        logs.add(
                System.currentTimeMillis()
                        + " : "
                        + text
        );



        prefs.edit()
                .putStringSet(
                        "entries",
                        logs
                )
                .apply();

    }

}