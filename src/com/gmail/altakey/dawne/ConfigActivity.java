package com.gmail.altakey.dawne;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private ListPreference colortheme;
	private ListPreference fontsize;
	private ListPreference scrolllines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);

		this.colortheme = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.COLORTHEME);
        this.fontsize = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.FONTSIZE);
        this.scrolllines = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.SCROLL_LINES);
    }

    @Override
    protected void onResume() {
        super.onResume();

		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		this.updateSummary(sharedPreferences, ConfigKey.COLORTHEME);
		this.updateSummary(sharedPreferences, ConfigKey.FONTSIZE);
		this.updateSummary(sharedPreferences, ConfigKey.SCROLL_LINES);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.updateSummary(sharedPreferences, key);
    }

	private void updateSummary(SharedPreferences sharedPreferences, String key)
	{
        if (key.equals(ConfigKey.COLORTHEME))
			this.colortheme.setSummary(this.colortheme.getEntry());

        if (key.equals(ConfigKey.FONTSIZE))
			this.fontsize.setSummary(sharedPreferences.getString(key, "") + " sp");

        if (key.equals(ConfigKey.SCROLL_LINES))
			this.scrolllines.setSummary(this.scrolllines.getEntry());
	}
}
