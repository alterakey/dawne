package com.gmail.altakey.dawne;

import java.util.List;
import java.util.Arrays;
import org.mozilla.intl.chardet.*;

import android.util.Log;

public class CharsetDetector
{
	private nsDetector detector;
	private String charset;

	private boolean isAscii;
	private boolean detected;
	private LanguagePolicy policy;

	public CharsetDetector()
	{
	}

	public void begin()
	{
		this.charset = null;
		this.isAscii = true;
		this.detected = false;

		this.policy = new JapanesePolicy();

		this.detector = new nsDetector(this.policy.getDetector());
		this.detector.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset)
			{
				CharsetDetector.this.charset = charset;
				Log.d("dawne.CD", String.format("detected %s", charset));
			}
		});
	}

	public void feed(byte[] buffer, int len)
	{
		if (this.isAscii)
			this.isAscii = this.detector.isAscii(buffer, len);
		
		if (!this.isAscii && !this.detected)
			this.detected = this.detector.DoIt(buffer, len, false);
	}

	public void end()
	{
		this.detector.DataEnd();

		if (this.charset == null)
		{
			this.charset = this.policy.guess(this.detector.getProbableCharsets());
			Log.d("dawne.CD", String.format("guessed as %s", this.charset));
		}
	}

	public String getCharset()
	{
		return this.charset;
	}

	public boolean getDetected()
	{
		return this.detected;
	}

	public boolean getGuessed()
	{
		return (this.charset != null);
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
