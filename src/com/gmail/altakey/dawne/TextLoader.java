package com.gmail.altakey.dawne;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

import android.net.Uri;
import android.widget.TextView;

public class TextLoader
{
	private TextView textView;
	private Uri uri;
	
	public TextLoader(TextView view, Uri uri)
	{
		this.textView = view;
		this.uri = uri;
	}

	public static TextLoader create(TextView view, Uri uri)
	{
		return new TextLoader(view, uri);
	}
	
	public void load()
	{
		this.textView.setText(this.read());
	}
	
	private String read()
	{
		try
		{
			InputStream in = this.textView.getContext().getContentResolver().openInputStream(this.uri);
			Reader reader = new InputStreamReader(in);

			StringBuilder builder = new StringBuilder();
			CharBuffer buffer = CharBuffer.allocate(8192);
			while (reader.read(buffer) >= 0) {
				builder.append(buffer.flip());
				buffer.clear();
			}

			return builder.toString();
		}
		catch (java.io.IOException e)
		{
			return "Cannot load URI";
		}
	}
}
