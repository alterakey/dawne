/**
 * Copyright (C) 2011-2012 Takahiro Yoshimura
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gmail.altakey.dawne;

import java.util.List;
import java.util.Arrays;
import org.mozilla.intl.chardet.*;

import android.util.Log;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class CharsetDetector
{
	private nsDetector detector;
	private String charset;
	private String charsetpreference;

	private boolean isAscii;
	private boolean detected;
	private LanguagePolicy policy;

	public CharsetDetector()
	{
	}

	public void begin(String charsetpreference)
	{
		this.charset = null;
		this.charsetpreference = charsetpreference;
		this.isAscii = true;
		this.detected = false;

		this.policy = this.createPolicy();

		this.detector = new nsDetector(this.policy.getDetector());
		this.detector.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset)
			{
				CharsetDetector.this.charset = charset;
				Log.d("dawne.CD", String.format("detected %s", charset));
			}
		});
	}

	private LanguagePolicy createPolicy()
	{
		String charset = this.charsetpreference;

		if (charset != null)
		{
			if (charset.equals("japanese"))
				return new JapanesePolicy();

			if (charset.equals("chinese"))
				return new ChinesePolicy();

			if (charset.equals("simplified_chinese"))
				return new SimplifiedChinesePolicy();

			if (charset.equals("traditional_chinese"))
				return new TraditionalChinesePolicy();

			if (charset.equals("korean"))
				return new KoreanPolicy();
		}

		return new BasePolicy();
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

	private class ChinesePolicy extends BasePolicy implements LanguagePolicy
	{
		public int getDetector()
		{
			return nsPSMDetector.CHINESE;
		}
	}

	private class SimplifiedChinesePolicy extends BasePolicy implements LanguagePolicy
	{
		public int getDetector()
		{
			return nsPSMDetector.SIMPLIFIED_CHINESE;
		}
	}

	private class TraditionalChinesePolicy extends BasePolicy implements LanguagePolicy
	{
		public int getDetector()
		{
			return nsPSMDetector.TRADITIONAL_CHINESE;
		}
	}

	private class KoreanPolicy extends BasePolicy implements LanguagePolicy
	{
		public int getDetector()
		{
			return nsPSMDetector.KOREAN;
		}
	}
}
