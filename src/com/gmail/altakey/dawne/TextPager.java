package com.gmail.altakey.dawne;

import android.widget.TextView;
import android.widget.ScrollView;

public class TextPager
{
	private TextView textView;
	private ScrollView scroller;

	public TextPager(TextView textView, ScrollView scroller)
	{
		this.textView = textView;
		this.scroller = scroller;
	}

	public static TextPager create(TextView textView, ScrollView scroller)
	{
		return new TextPager(textView, scroller);
	}

	public void up()
	{
		this.scroller.smoothScrollBy(0, -this.scroller.getHeight() / 2);
	}

	public void down()
	{
		this.scroller.smoothScrollBy(0, this.scroller.getHeight() / 2);
	}
}
