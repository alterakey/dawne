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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    protected View rootView;
    protected EditText textView; // for search feature, we need EditText class
    protected View searchBar;
    protected EditText searchField;
    protected int selectionStart;
    protected int selectionEnd;
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
            // do nothing
            return true;
        }
    };

    private final View.OnKeyListener dummyKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // disable alpha-numeric input key from physical keyboard
            if (keyCode >= KeyEvent.KEYCODE_0
                    && keyCode <= KeyEvent.KEYCODE_PLUS) {
                if (keyCode >= KeyEvent.KEYCODE_DPAD_UP
                        && keyCode <= KeyEvent.KEYCODE_DPAD_RIGHT) {
                    return false;
                }
                return true;
            }
            // disable DEL
            if (keyCode == KeyEvent.KEYCODE_FORWARD_DEL) {
                return true;
            }
            return false;
        }
    };

    private final View.OnKeyListener searchKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                searchNext();
                return true;
            }
            return false;
        }
    };

    /** Called when the activity is first created. */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupWindow();

        setContentView(R.layout.main);

        this.rootView = findViewById(R.id.view);
        this.textView = (EditText) findViewById(R.id.textview);
        // this is needed to make EditText behaves like simple TextView
        // so the soft keyboard won't appear if user touches the text
        this.textView.setOnTouchListener(dummyTouchListener);
        // this is needed to make EditText won't be changed if user tries
        // to edit the text with physical keyboard
        this.textView.setOnKeyListener(dummyKeyListener);
        this.textView.setSaveEnabled(false);
        this.searchBar = findViewById(R.id.search);
        this.searchField = (EditText) findViewById(R.id.edittext);
        this.searchField.setOnKeyListener(searchKeyListener);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }
        if (hideTitle) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (titleHidden) {
                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.FILL_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                textView.setLayoutParams(params);
            }
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectionStart", selectionStart);
        outState.putInt("selectionEnd", selectionEnd);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectionStart = savedInstanceState.getInt("selectionStart", 0);
            selectionEnd = savedInstanceState.getInt("selectionEnd", 0);
        }
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

    @SuppressLint("NewApi")
    void showSearchBar(boolean shown) {
        if (searchBar == null) {
            throw new IllegalStateException();
        }
        if (shown) {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            showSoftKeyBoard();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (!titleHidden) {
                    final ActionBar actionBar = getActionBar();
                    final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.FILL_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    textView.setLayoutParams(params);
                    actionBar.hide();
                }
            }
        } else {
            hideSoftKeyboard();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (!titleHidden) {
                    final ActionBar actionBar = getActionBar();
                    final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.FILL_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, actionBar.getHeight(), 0, 0);
                    textView.setLayoutParams(params);
                    actionBar.show();
                }
            }
            searchBar.setVisibility(View.GONE);
        }
    }

    void searchNext() {
        final String text = textView.getText().toString().toLowerCase();
        final String search = searchField.getText().toString().toLowerCase();
        if (search.length() == 0) {
            return;
        }
        int selection = textView.getSelectionEnd();
        int next = text.indexOf(search, selection);
        if (next > -1) {
            // EditText class is needed here
            textView.setSelection(selectionStart = next, selectionEnd = next
                    + search.length());
            if (!textView.isFocused()) {
                textView.setCursorVisible(true);
                textView.requestFocus();
            }
        } else { // wrap
            next = text.indexOf(search);
            if (next > -1) {
                textView.setSelection(selectionStart = next,
                        selectionEnd = next + search.length());
                if (!textView.isFocused()) {
                    textView.setCursorVisible(true);
                    textView.requestFocus();
                }
            }
        }
        // if text is selected (found), user can actually change it with
        // soft keyboard, so we need to hide the soft keyboard
        if (textView.isFocused()) {
            hideSoftKeyboard();
        }

    }

    void searchPrevious() {
        final String text = textView.getText().toString().toLowerCase();
        final String search = searchField.getText().toString().toLowerCase();
        if (search.length() == 0) {
            return;
        }
        int selection = textView.getSelectionStart() - 1;
        int previous = text.lastIndexOf(search, selection);
        if (previous > -1) {
            textView.setSelection(selectionStart = previous,
                    selectionEnd = previous + search.length());
            if (!textView.isFocused()) {
                textView.setCursorVisible(true);
                textView.requestFocus();
            }
        } else { // wrap
            previous = text.lastIndexOf(search);
            if (previous > -1) {
                textView.setSelection(selectionStart = previous,
                        selectionEnd = previous + search.length());
                if (!textView.isFocused()) {
                    textView.setCursorVisible(true);
                    textView.requestFocus();
                }
            }
        }
        if (textView.isFocused()) {
            hideSoftKeyboard();
        }
    }
}
