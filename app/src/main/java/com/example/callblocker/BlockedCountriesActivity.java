package com.example.callblocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockedCountriesActivity extends AppCompatActivity {

    private EditText input;
    private Button addBtn, removeBtn, backBtn;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    private static final String PREF_KEY = "blockedCountriesJson";

    // Kaikki maatunnukset paitsi Suomi (+358)
    private static final String[] ALL_COUNTRY_CODES = {
            "+1", "+7", "+20", "+27", "+30", "+31", "+32", "+33", "+34", "+36", "+39",
            "+40", "+41", "+43", "+44", "+45", "+46", "+47", "+48", "+49",
            "+51", "+52", "+53", "+54", "+55", "+56", "+57", "+58",
            "+60", "+61", "+62", "+63", "+64", "+65", "+66",
            "+81", "+82", "+84", "+86",
            "+90", "+91", "+92", "+93", "+94", "+95", "+98",
            "+212", "+213", "+216", "+218",
            "+220", "+221", "+222", "+223", "+224", "+225", "+226", "+227", "+228", "+229",
            "+230", "+231", "+232", "+233", "+234", "+235", "+236", "+237", "+238", "+239",
            "+240", "+241", "+242", "+243", "+244", "+245", "+246", "+247", "+248", "+249",
            "+250", "+251", "+252", "+253", "+254", "+255", "+256", "+257", "+258", "+260",
            "+261", "+262", "+263", "+264", "+265", "+266", "+267", "+268", "+269",
            "+290", "+291", "+297", "+298", "+299",
            "+350", "+351", "+352", "+353", "+354", "+355", "+356", "+357",
            "+370", "+371", "+372", "+373", "+374", "+375", "+376", "+377", "+378", "+379",
            "+380", "+381", "+382", "+383", "+385", "+386", "+387", "+389",
            "+420", "+421", "+423",
            "+500", "+501", "+502", "+503", "+504", "+505", "+506", "+507", "+508", "+509",
            "+590", "+591", "+592", "+593", "+594", "+595", "+596", "+597", "+598", "+599",
            "+670", "+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679",
            "+680", "+681", "+682", "+683", "+685", "+686", "+687", "+688", "+689",
            "+690", "+691", "+692",
            "+850", "+852", "+853", "+855", "+856",
            "+870", "+878",
            "+880", "+886",
            "+960", "+961", "+962", "+963", "+964", "+965", "+966", "+967", "+968", "+970",
            "+971", "+972", "+973", "+974", "+975", "+976", "+977",
            "+992", "+993", "+994", "+995", "+996", "+998"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_countries);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        input = findViewById(R.id.blockedCountryInput);
        addBtn = findViewById(R.id.addBlockedCountryBtn);
        removeBtn = findViewById(R.id.removeBlockedCountryBtn);
        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.blockedCountriesList);

        // ⭐ Lataa JSON-lista
        list = loadList(prefs);

        // Jos lista on tyhjä → täytetään maatunnuksilla
        if (list.isEmpty()) {
            list.addAll(Arrays.asList(ALL_COUNTRY_CODES));
            saveList(prefs);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // ⭐ Lisää maatunnus / range
        addBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();

            if (!isValidCountryInput(inputText)) {
                Toast.makeText(this, "Virheellinen maatunnus", Toast.LENGTH_SHORT).show();
                return;
            }

            list.addAll(expandRange(inputText));
            saveList(prefs);

            Toast.makeText(this, "Maatunnus(t) lisätty", Toast.LENGTH_SHORT).show();
        });

        // ⭐ Poista maatunnus / range
        removeBtn.setOnClickListener(v -> {
            String inputText = input.getText().toString().trim();

            if (!isValidCountryInput(inputText)) {
                Toast.makeText(this, "Virheellinen maatunnus", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean removed = false;
            for (String code : expandRange(inputText)) {
                removed |= list.remove(code);
            }

            saveList(prefs);

            Toast.makeText(this,
                    removed ? "Maatunnus(t) poistettu" : "Maatunnusta ei löytynyt",
                    Toast.LENGTH_SHORT).show();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // ⭐ JSON tallennus
    private void saveList(SharedPreferences prefs) {
        JSONArray arr = new JSONArray(list);
        prefs.edit().putString(PREF_KEY, arr.toString()).apply();
        adapter.notifyDataSetChanged();
    }

    // ⭐ JSON lataus
    private ArrayList<String> loadList(SharedPreferences prefs) {
        String json = prefs.getString(PREF_KEY, "[]");
        ArrayList<String> result = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
        } catch (Exception ignored) {}

        return result;
    }

    // ⭐ Validointi
    private boolean isValidCountryInput(String input) {
        if (input == null || input.isEmpty()) return false;

        if (input.contains("-")) {
            String[] parts = input.split("-");
            return parts.length == 2 &&
                    parts[0].trim().startsWith("+") &&
                    parts[1].trim().matches("\\d+");
        }

        return input.startsWith("+") && input.substring(1).matches("\\d+");
    }

    // ⭐ Range-tuki
    private ArrayList<String> expandRange(String input) {
        ArrayList<String> result = new ArrayList<>();

        if (!input.contains("-")) {
            result.add(input);
            return result;
        }

        try {
            String[] parts = input.split("-");
            int startNum = Integer.parseInt(parts[0].trim().substring(1));
            int endNum = Integer.parseInt(parts[1].trim());

            for (int i = startNum; i <= endNum; i++) {
                result.add("+" + i);
            }

        } catch (Exception e) {
            result.add(input);
        }

        return result;
    }
}
