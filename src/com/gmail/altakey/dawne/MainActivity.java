package com.gmail.altakey.dawne;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.SharedPreferences;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class MainActivity extends Activity
{
	protected View rootView;
	protected TextView textView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		this.rootView = findViewById(R.id.view);
		this.textView = (TextView)findViewById(R.id.textview);

		this.restyle();
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
		this.restyle();
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
