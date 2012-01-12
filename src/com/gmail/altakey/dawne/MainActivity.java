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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.Window;
import android.content.SharedPreferences;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class MainActivity extends Activity
{
	protected View rootView;
	protected TextView textView;
	private boolean titleHidden;

	private String currentCharsetpreference;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		this.setupWindow();

        setContentView(R.layout.main);

		this.rootView = findViewById(R.id.view);
		this.textView = (TextView)findViewById(R.id.textview);

		this.restyle();
    }

	protected void setupWindow()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean hideTitle = !pref.getBoolean(ConfigKey.SHOW_TITLE, true);
		String charsetpreference = pref.getString(ConfigKey.CHARSET_PREFERENCE, "all");
		
		if (hideTitle)
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.titleHidden = hideTitle;
		this.currentCharsetpreference = charsetpreference;
	} 

	private void restyle()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		String colortheme = pref.getString(ConfigKey.COLORTHEME, "black");
		int foreground = 0xffffffff;
		int background = 0xff000000;

		float fontsize = Float.parseFloat(pref.getString(ConfigKey.FONTSIZE, "14"));

		if (colortheme.equals("white"))
		{
			foreground = 0xff000000;
			background = 0xffffffff;
		}

		TextStyler.create(this.rootView, this.textView, background, foreground, fontsize).style();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean hideTitle = !pref.getBoolean(ConfigKey.SHOW_TITLE, true);
		String charsetpreference = pref.getString(ConfigKey.CHARSET_PREFERENCE, "all");

		if (this.titleHidden != hideTitle || this.currentCharsetpreference != charsetpreference)
		{
			this.restart();
			return;
		}
		
		this.restyle();
	}

	private void restart()
	{
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.menu_main_config:
			startActivity(new Intent(this, ConfigActivity.class));
		}
		return true;
	}
}
