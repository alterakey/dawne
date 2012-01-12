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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.ScrollView;

public class ViewActivity extends MainActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String charsetpreference = pref.getString(ConfigKey.CHARSET_PREFERENCE, "japanese");

		Intent intent = getIntent();
		TextLoader.create(this.textView, intent.getData(), charsetpreference).load();
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
				if (action == KeyEvent.ACTION_DOWN)
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
