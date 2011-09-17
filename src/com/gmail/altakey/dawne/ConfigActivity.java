package com.gmail.altakey.dawne;

import android.app.ListActivity;
import android.os.Bundle;
import android.content.Context;
import android.widget.ArrayAdapter;

public class ConfigActivity extends ListActivity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		String[] ar = new String[] 
			{
				"表示色", "背景色", "フォントサイズ" 
			};

		ArrayAdapter<String> la = new ArrayAdapter<String>((Context)this, android.R.layout.simple_list_item_1, ar);
		setListAdapter(la);
	}
}
