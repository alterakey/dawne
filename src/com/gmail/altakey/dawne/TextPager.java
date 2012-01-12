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

import android.widget.ScrollView;
import android.widget.TextView;

public class TextPager
{
	private ScrollView scroller;
	private int divisor;

	public TextPager(TextView textView, ScrollView scroller, int divisor)
	{
		this.scroller = scroller;
		this.divisor = divisor;
	}

	public static TextPager create(TextView textView, ScrollView scroller, int divisor)
	{
		return new TextPager(textView, scroller, divisor);
	}

	public void up()
	{
		this.scroller.smoothScrollBy(0, -this.scroller.getHeight() / divisor);
	}

	public void down()
	{
		this.scroller.smoothScrollBy(0, this.scroller.getHeight() / divisor);
	}
}
