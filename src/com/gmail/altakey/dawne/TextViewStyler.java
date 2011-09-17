package com.gmail.altakey.dawne;

import android.widget.TextView;
import android.util.TypedValue;

public class TextViewStyler
{
	private TextView textView;

	public TextViewStyler(TextView textView) 
	{
		this.textView = textView;
	}

	public static TextViewStyler create(TextView textView)
	{
		return new TextViewStyler(textView);
	}

	public void style() 
	{
		this.textView.setTextColor(0xffffffff);
		this.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
	}
}
