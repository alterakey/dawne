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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;

public class TextLoader {
    private Context context;
    private Uri uri;
    private String charsetpreference;

    public TextLoader(Context context, Uri uri, String charsetpreference) {
        this.context = context;
        this.uri = uri;
        this.charsetpreference = charsetpreference;
    }

    public static TextLoader create(Context context, Uri uri,
            String charsetpreference) {
        return new TextLoader(context, uri, charsetpreference);
    }

    public String read() {
        CharsetDetector det = new CharsetDetector();

        try {
            InputStream in = this.context.getContentResolver().openInputStream(
                    this.uri);

            byte[] buffer = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            det.begin(this.charsetpreference);

            int read;
            while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                det.feed(buffer, read);
                os.write(buffer, 0, read);
            }

            det.end();

            return new String(os.toByteArray(), det.getCharset());
        } catch (java.io.IOException e) {
            return "Cannot load URI";
        }
    }
}
