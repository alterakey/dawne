package com.gmail.altakey.dawne;

import android.preference.*;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.util.Log;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private ListPreference colortheme;
	private ListPreference fontsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);

		this.colortheme = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.COLORTHEME);
        this.fontsize = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.FONTSIZE);
    }

    @Override
    protected void onResume() {
        super.onResume();

		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		this.updateSummary(sharedPreferences, ConfigKey.COLORTHEME);
		this.updateSummary(sharedPreferences, ConfigKey.FONTSIZE);

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
	}
}
