package com.example.flo.anaquiz;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat{

    private long time;
    private int seconds;
    private int minutes;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.my_preferences, rootKey);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Bestzeit klein
        final Preference bestTimeSmall = findPreference("bestTimeSmall");
        time = sharedPreferences.getLong("bestTimeSmall", 0);
        seconds = (int) (time / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        bestTimeSmall.setSummary(getString(R.string.timePlace, minutes, seconds));
        bestTimeSmall.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                bestTimeSmall.setSummary(getString(R.string.timePlace, minutes, seconds));
                return true;
            }
        });

        //Bestzeit mittel
        final Preference bestTimeNormal = findPreference("bestTimeNormal");
        time = sharedPreferences.getLong("bestTimeNormal", 0);
        seconds = (int) (time / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        bestTimeNormal.setSummary(getString(R.string.timePlace, minutes, seconds));
        bestTimeNormal.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                bestTimeNormal.setSummary(getString(R.string.timePlace, minutes, seconds));
                return true;
            }
        });

        //Bestzeit groß
        final Preference bestTimeLarge = findPreference("bestTimeLarge");
        time = sharedPreferences.getLong("bestTimeLarge", 0);
        seconds = (int) (time / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        bestTimeLarge.setSummary(getString(R.string.timePlace, minutes, seconds));
        bestTimeLarge.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                bestTimeLarge.setSummary(getString(R.string.timePlace, minutes, seconds));
                return true;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
