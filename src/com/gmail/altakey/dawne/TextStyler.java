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

import android.view.View;
import android.widget.TextView;
import android.util.TypedValue;

public class TextStyler
{
	private View rootView;
	private TextView textView;
	private int backgroundColor;
	private int foregroundColor;
	private float fontSize;

	public TextStyler(View rootView, TextView textView, int backgroundColor, int foregroundColor, float fontSize) 
	{
		this.rootView = rootView;
		this.textView = textView;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.fontSize = fontSize;
	}

	public static TextStyler create(View rootView, TextView textView, int backgroundColor, int foregroundColor, float fontSize) 
	{
		return new TextStyler(rootView, textView, backgroundColor, foregroundColor, fontSize);
	}

	public void style() 
	{
		this.rootView.setBackgroundColor(this.backgroundColor);
		this.textView.setTextColor(this.foregroundColor);
		this.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.fontSize);
	}
}
