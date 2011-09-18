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
