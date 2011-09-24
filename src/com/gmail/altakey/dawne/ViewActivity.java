package com.gmail.altakey.dawne;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.ScrollView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ViewActivity extends MainActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		TextLoader.create(this.textView, intent.getData()).load();
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean scrollerEnabled = pref.getBoolean(ConfigKey.SCROLL_BY_VOLUME_KEYS, false);

		if (scrollerEnabled)
		{
			int divisor = Integer.parseInt(pref.getString(ConfigKey.SCROLL_LINES, "2"));

			int action = event.getAction();
			int keyCode = event.getKeyCode();

			switch (keyCode)
			{
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (action == KeyEvent.ACTION_UP)
					TextPager.create(this.textView, (ScrollView)this.rootView, divisor).up();
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (action == KeyEvent.ACTION_DOWN)
					TextPager.create(this.textView, (ScrollView)this.rootView, divisor).down();
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}
}
