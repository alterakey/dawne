package com.gmail.altakey.dawne;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Arrays;

import android.net.Uri;
import android.widget.TextView;

import android.util.Log;
import org.mozilla.intl.chardet.*;

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
		this.textView.setText(this.cleverRead());
	}
	
	private String read()
	{
		try
		{
			InputStream in = this.textView.getContext().getContentResolver().openInputStream(this.uri);
			Reader reader = new InputStreamReader(in);

			StringBuilder builder = new StringBuilder();
			CharBuffer buffer = CharBuffer.allocate(8192);
			while (reader.read(buffer) >= 0)
			{
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

	private String cleverRead()
	{
		try
		{
			InputStream in = this.textView.getContext().getContentResolver().openInputStream(this.uri);

			LanguagePolicy policy = new JapanesePolicy();

			nsDetector det = new nsDetector(policy.getDetector());
			det.Init(new nsICharsetDetectionObserver() {
				public void Notify(String charset)
				{
					TextLoader.this.charset = charset;
					Log.d("dawne.TL.r", String.format("detected %s", charset));
				}
			});

			byte[] buffer = new byte[1024];
			ByteArrayOutputStream os = new ByteArrayOutputStream();	

			boolean isAscii = true;
			boolean done = false;

			int read;
			while ((read = in.read(buffer, 0, buffer.length)) != -1)
			{
				if (isAscii)
					isAscii = det.isAscii(buffer, read);

				if (!isAscii && !done)
					done = det.DoIt(buffer, read, false);

				os.write(buffer, 0, read);
			}
			det.DataEnd();

			if (this.charset == null)
			{
				this.charset = policy.guess(det.getProbableCharsets());
				Log.d("dawne.TL.r", String.format("guessed as %s", this.charset));
			}

			return new String(os.toByteArray(), this.charset);
		}
		catch (java.io.IOException e)
		{
			return "Cannot load URI";
		}
	}

	private interface LanguagePolicy
	{
		int getDetector();
		String guess(String[] probabilities);
	}

	private class BasePolicy implements LanguagePolicy
	{
		public int getDetector()
		{
			return nsPSMDetector.ALL; 
		}

		public String guess(String[] probabilities)
		{
			return "UTF-8";
		}
	}

	private class JapanesePolicy implements LanguagePolicy
	{
		private final String[] preference = {"ISO-2022-JP", "Shift-JIS", "EUC-JP", "UTF-8"};

		public int getDetector()
		{
			return nsPSMDetector.JAPANESE; 
		}

		public String guess(String[] probabilities)
		{
			List<String> probs = Arrays.asList(probabilities);

			for (String pref : this.preference)
			{
				if (probs.contains(pref))
					return pref;
			}

			return "UTF-8";
		}
	}
}
