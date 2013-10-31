/**
 * Copyright (C) 2011-2012 Takahiro Yoshimura
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gmail.altakey.dawne;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class ConfigActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference colortheme;
    private NumberPickerPreference fontsize;
    private ListPreference scrolllines;
    private ListPreference charsetpreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);

        this.colortheme = (ListPreference) getPreferenceScreen()
                .findPreference(ConfigKey.COLORTHEME);
        this.fontsize = (NumberPickerPreference) getPreferenceScreen()
                .findPreference(ConfigKey.FONTSIZE);
        this.scrolllines = (ListPreference) getPreferenceScreen()
                .findPreference(ConfigKey.SCROLL_LINES);
        this.charsetpreference = (ListPreference) getPreferenceScreen()
                .findPreference(ConfigKey.CHARSET_PREFERENCE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferenceScreen()
                .getSharedPreferences();
        this.updateSummary(sharedPreferences, ConfigKey.COLORTHEME);
        this.updateSummary(sharedPreferences, ConfigKey.FONTSIZE);
        this.updateSummary(sharedPreferences, ConfigKey.SCROLL_LINES);
        this.updateSummary(sharedPreferences, ConfigKey.CHARSET_PREFERENCE);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        this.updateSummary(sharedPreferences, key);
    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        if (key.equals(ConfigKey.COLORTHEME))
            this.colortheme.setSummary(this.colortheme.getEntry());

        if (key.equals(ConfigKey.FONTSIZE))
            this.fontsize.setSummary(sharedPreferences.getString(key, "14")
                    + " sp");

        if (key.equals(ConfigKey.SCROLL_LINES))
            this.scrolllines.setSummary(this.scrolllines.getEntry());

        if (key.equals(ConfigKey.CHARSET_PREFERENCE))
            this.charsetpreference
                    .setSummary(this.charsetpreference.getEntry());
    }
}
