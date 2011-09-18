package com.gmail.altakey.dawne;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

public class ViewActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Intent intent = getIntent();
		TextView textView = (TextView)findViewById(R.id.textview);
		TextViewStyler.create(textView).style();
		TextLoader.create(textView, intent.getData()).load();
    }
}
