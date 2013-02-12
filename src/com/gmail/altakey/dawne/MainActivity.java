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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    protected View rootView;
    protected EditText textView;
    protected View searchBar;
    protected EditText searchField;
    private boolean titleHidden;

    private String currentCharsetpreference;

    private final View.OnClickListener cancelButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showSearchBar(false);
        }
    };
    private final View.OnClickListener prevButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            searchPrevious();
        }
    };
    private final View.OnClickListener nextButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            searchNext();
        }
    };
    private final View.OnTouchListener dummyTouchListener = new View.OnTouchListener() {
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupWindow();

        setContentView(R.layout.main);

        this.rootView = findViewById(R.id.view);
        this.textView = (EditText) findViewById(R.id.textview);
        this.textView.setOnTouchListener(dummyTouchListener);
        this.searchBar = findViewById(R.id.search);
        this.searchField = (EditText) findViewById(R.id.edittext);

        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(cancelButtonListener);
        ImageButton prevButton = (ImageButton) findViewById(R.id.previous);
        prevButton.setOnClickListener(prevButtonListener);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next);
        nextButton.setOnClickListener(nextButtonListener);

        this.restyle();
    }

    protected void setupWindow() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean hideTitle = !pref.getBoolean(ConfigKey.SHOW_TITLE, true);
        String charsetpreference = pref.getString(ConfigKey.CHARSET_PREFERENCE,
                "all");

        if (hideTitle)
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.titleHidden = hideTitle;
        this.currentCharsetpreference = charsetpreference;
    }

    private void restyle() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        String colortheme = pref.getString(ConfigKey.COLORTHEME, "black");
        int foreground = 0xffffffff;
        int background = 0xff000000;

        float fontsize = Float.parseFloat(pref.getString(ConfigKey.FONTSIZE,
                "14"));
        boolean useMonospaceFonts = pref.getBoolean(
                ConfigKey.USE_MONOSPACE_FONTS, false);

        if (colortheme.equals("white")) {
            foreground = 0xff000000;
            background = 0xffffffff;
        }

        TextStyler.create(this.rootView, this.textView, background, foreground,
                fontsize, useMonospaceFonts ? "monospace" : "default").style();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean hideTitle = !pref.getBoolean(ConfigKey.SHOW_TITLE, true);
        String charsetpreference = pref.getString(ConfigKey.CHARSET_PREFERENCE,
                "all");

        if (this.titleHidden != hideTitle
                || this.currentCharsetpreference != charsetpreference) {
            this.restart();
            return;
        }

        this.restyle();
    }

    private void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_main_search:
            showSearchBar(true);
            break;
        case R.id.menu_main_config:
            startActivity(new Intent(this, ConfigActivity.class));
            break;
        }
        return true;
    }

    void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    void showSoftKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    void showSearchBar(boolean shown) {
        if (searchBar == null) {
            throw new IllegalStateException();
        }
        if (shown) {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            showSoftKeyBoard();
        } else {
            hideSoftKeyboard();
            searchBar.setVisibility(View.GONE);
        }
    }

    void searchNext() {
        final String text = textView.getText().toString().toLowerCase();
        final String search = searchField.getText().toString().toLowerCase();
        int selection = textView.getSelectionEnd();
        int next = text.indexOf(search, selection);
        if (next > -1) {
            textView.setSelection(next, next + search.length());
            if (!textView.isFocused()) {
                textView.requestFocus();
            }
        } else {
            next = text.indexOf(search);
            if (next > -1) {
                textView.setSelection(next, next + search.length());
                if (!textView.isFocused()) {
                    textView.requestFocus();
                }
            }
        }

    }

    void searchPrevious() {
        final String text = textView.getText().toString().toLowerCase();
        final String search = searchField.getText().toString().toLowerCase();
        int selection = textView.getSelectionStart() - 1;
        int previous = text.lastIndexOf(search, selection);
        if (previous > -1) {
            textView.setSelection(previous, previous + search.length());
            if (!textView.isFocused()) {
                textView.requestFocus();
            }
        } else {
            previous = text.lastIndexOf(search);
            if (previous > -1) {
                textView.setSelection(previous, previous + search.length());
                if (!textView.isFocused()) {
                    textView.requestFocus();
                }
            }
        }
    }
}
