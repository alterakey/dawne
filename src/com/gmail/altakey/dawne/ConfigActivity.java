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
	private static final String KEY_COLORTHEME = "colortheme";
	private static final String KEY_FONTSIZE = "fontsize";

	private ListPreference colortheme;
	private ListPreference fontsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);

		this.colortheme = (ListPreference)getPreferenceScreen().findPreference(KEY_COLORTHEME);
        this.fontsize = (ListPreference)getPreferenceScreen().findPreference(KEY_FONTSIZE);
    }

    @Override
    protected void onResume() {
        super.onResume();

		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		this.updateSummary(sharedPreferences, KEY_COLORTHEME);
		this.updateSummary(sharedPreferences, KEY_FONTSIZE);

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
        if (key.equals(KEY_COLORTHEME))
			this.colortheme.setSummary(this.colortheme.getEntry());

        if (key.equals(KEY_FONTSIZE))
			this.fontsize.setSummary(sharedPreferences.getString(key, "") + " sp");
	}
}
