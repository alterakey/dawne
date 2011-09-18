package com.gmail.altakey.dawne;

import android.os.Bundle;
import android.content.Intent;

public class ViewActivity extends MainActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		TextLoader.create(this.textView, intent.getData()).load();
    }
}
