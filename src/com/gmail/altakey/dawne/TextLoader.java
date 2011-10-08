package com.gmail.altakey.dawne;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

import java.io.ByteArrayOutputStream;

import android.net.Uri;
import android.widget.TextView;

public class TextLoader
{
	private TextView textView;
	private Uri uri;

	private String charset;
	
	public TextLoader(TextView view, Uri uri)
	{
		this.textView = view;
		this.uri = uri;
		this.charset = null;
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
		CharsetDetector det = new CharsetDetector();
		
		try
		{
			InputStream in = this.textView.getContext().getContentResolver().openInputStream(this.uri);

			byte[] buffer = new byte[1024];
			ByteArrayOutputStream os = new ByteArrayOutputStream();	

			det.begin();

			int read;
			while ((read = in.read(buffer, 0, buffer.length)) != -1)
			{
				det.feed(buffer, read);
				os.write(buffer, 0, read);
			}

			det.end();

			return new String(os.toByteArray(), det.getCharset());
		}
		catch (java.io.IOException e)
		{
			return "Cannot load URI";
		}
	}
}
