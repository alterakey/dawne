package com.gmail.altakey.dawne;

import android.util.Log;
import android.net.Uri;
import android.widget.TextView;
import java.io.*;
import java.nio.CharBuffer;
import java.net.URI;
import java.net.URISyntaxException;

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
		android.net.Uri androidUri = this.uri;

		try
		{
			File file = new File(new java.net.URI(androidUri.toString()));
			Log.d("", file.toURI().toString());
			Reader reader = new FileReader(file);

			StringBuilder builder = new StringBuilder();
			CharBuffer buffer = CharBuffer.allocate(8192);
			while (reader.read(buffer) >= 0) {
				builder.append(buffer.flip());
				buffer.clear();
			}

			return builder.toString();
		}
		catch (java.net.URISyntaxException e)
		{
			return "content URI is not supported yet";
		}
		catch (java.io.FileNotFoundException e)
		{
			return "URI is not found";
		}
		catch (java.io.IOException e)
		{
			return "Cannot load URI";
		}
	}
}
