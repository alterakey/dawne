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
