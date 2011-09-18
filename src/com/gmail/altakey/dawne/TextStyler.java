package com.gmail.altakey.dawne;

import android.widget.TextView;
import android.util.TypedValue;

public class TextStyler
{
	private TextView textView;

	public TextStyler(TextView textView) 
	{
		this.textView = textView;
	}

	public static TextStyler create(TextView textView)
	{
		return new TextStyler(textView);
	}

	public void style() 
	{
		this.textView.setTextColor(0xffffffff);
		this.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
	}
}
