/**
 * Copyright (C) 2011-2013 Takahiro Yoshimura, Renjaya Raga Zenta
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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gmail.altakey.dawne.util.ConfigKey;
import com.gmail.altakey.dawne.util.TextLoader;
import com.gmail.altakey.dawne.util.TextPager;
import com.gmail.altakey.dawne.util.TextStyler;

public class ViewerFragment extends Fragment {

    private View scrollView;
    private TextView textView;
    private View searchBar;
    private EditText searchField;
    private int selectionStart;
    private int selectionEnd;
    private Uri mUri;
    private OnContentTapListener mCallback;
    private boolean isSearching = false;
    private String currentCharsetPreference;

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
    private final View.OnTouchListener contentTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!isSearching) {
                mCallback.onContentTap();
            }
            // call super listener (for selecting text)
            return false;
        }
    };
    private final View.OnTouchListener dummyTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!isSearching) {
                mCallback.onContentTap();
            }
            // ignore super listener
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

    public ViewerFragment() {
    }

    public static ViewerFragment newInstance(String uriString) {
        final ViewerFragment f = new ViewerFragment();
        final Bundle args = new Bundle(1);
        args.putString("uri", uriString);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments() != null ? Uri.parse(getArguments().getString("uri")) : null;
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnContentTapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContentClickListener");
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.viewer, container, false);
        scrollView = rootView.findViewById(R.id.view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // In Honeycomb or above, we can set TextView to be selectable
            textView = (TextView) rootView.findViewById(R.id.textview);
            textView.setTextIsSelectable(true);
            textView.setOnTouchListener(contentTouchListener);
        } else {
            // Below Honeycomb we need EditText class
            textView = (EditText) rootView.findViewById(R.id.textview);
            // this is needed to make EditText behaves like simple TextView
            // so the soft keyboard won't appear if user touches the text
            textView.setOnTouchListener(dummyTouchListener);
            // this is needed to make EditText won't be changed if user tries
            // to edit the text with physical keyboard
            textView.setOnKeyListener(dummyKeyListener);
        }
        textView.setSaveEnabled(false);
        searchBar = rootView.findViewById(R.id.search);
        searchField = (EditText) rootView.findViewById(R.id.edittext);
        searchField.setOnKeyListener(searchKeyListener);

        ImageButton cancelButton = (ImageButton) rootView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(cancelButtonListener);
        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.previous);
        prevButton.setOnClickListener(prevButtonListener);
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(nextButtonListener);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String charsetPreference = pref.getString(ConfigKey.CHARSET_PREFERENCE, "all");
        currentCharsetPreference = charsetPreference;
        if (savedInstanceState != null) {
            selectionStart = savedInstanceState.getInt("selectionStart", 0);
            selectionEnd = savedInstanceState.getInt("selectionEnd", 0);
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("");
        loadText(charsetPreference);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String charsetPreference = pref.getString(ConfigKey.CHARSET_PREFERENCE, "all");
        if (!currentCharsetPreference.equals(charsetPreference)) {
            loadText(charsetPreference);
            currentCharsetPreference = charsetPreference;
        }
        ((NewMainActivity) getActivity()).hideActionBarDelayed();
        restyle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectionStart", selectionStart);
        outState.putInt("selectionEnd", selectionEnd);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.viewer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_viewer_search:
                showSearchBar(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_viewer_search).setVisible(
                !((NewMainActivity) getActivity()).isDrawerOpen());
        super.onPrepareOptionsMenu(menu);
    }

    private void loadText(String charsetPreference) {
        final TextLoaderParam param = new TextLoaderParam();
        param.context = getActivity().getApplicationContext();
        param.uri = mUri;
        param.charset = charsetPreference;
        new LoadTextTask().execute(param);
    }

    private void restyle() {
        if (getActivity() == null) {
            Log.d("dawne.Viewer", "Activity null :(");
            return;
        }
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

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
        } else if (colortheme.equals("solarized_dark")) {
            foreground = 0xff839496;
            background = 0xff002b36;
        } else if (colortheme.equals("solarized_light")) {
            foreground = 0xff657b83;
            background = 0xfffdf6e3;
        }

        TextStyler.create(scrollView, textView, background, foreground,
                fontsize, useMonospaceFonts ? "monospace" : "default").style();
    }

    void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    void showSoftKeyBoard() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    void showSearchBar(boolean shown) {
        isSearching = shown;
        if (searchBar == null) {
            throw new IllegalStateException();
        }
        if (shown) {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            showSoftKeyBoard();
            ((ActionBarActivity) getActivity()).getSupportActionBar().hide();
        } else {
            hideSoftKeyboard();
            ((ActionBarActivity) getActivity()).getSupportActionBar().show();
            ((NewMainActivity) getActivity()).hideActionBarDelayed();
            searchBar.setVisibility(View.GONE);
        }
    }

    boolean isSearching() {
        return isSearching;
    }

    @SuppressLint("DefaultLocale")
    void searchNext() {
        final String text = textView.getText().toString().toLowerCase();
        final String search = searchField.getText().toString().toLowerCase();
        if (search.length() == 0) {
            return;
        }
        int selection = textView.getSelectionEnd();
        int next = text.indexOf(search, selection);
        if (next > -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Selection.setSelection((Spannable) textView.getText(),
                        selectionStart = next,
                        selectionEnd = next + search.length());
            } else {
                // EditText class is needed here
                ((EditText) textView).setSelection(selectionStart = next,
                        selectionEnd = next + search.length());
            }
            if (!textView.isFocused()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    textView.setCursorVisible(true);
                }
                textView.requestFocus();
            }
        } else { // wrap
            next = text.indexOf(search);
            if (next > -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Selection.setSelection((Spannable) textView.getText(),
                            selectionStart = next,
                            selectionEnd = next + search.length());
                } else {
                    // EditText class is needed here
                    ((EditText) textView).setSelection(selectionStart = next,
                            selectionEnd = next + search.length());
                }
                if (!textView.isFocused()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        textView.setCursorVisible(true);
                    }
                    textView.requestFocus();
                }
            }
        }
        // Before Honeycomb:
        // if text is selected (found), user can actually change it with
        // soft keyboard, so we need to hide the soft keyboard
        if (textView.isFocused()) {
            hideSoftKeyboard();
        }

    }

    @SuppressLint("DefaultLocale")
    void searchPrevious() {
        final String text = textView.getText().toString().toLowerCase();
        final String search = searchField.getText().toString().toLowerCase();
        if (search.length() == 0) {
            return;
        }
        int selection = textView.getSelectionStart() - 1;
        int previous = text.lastIndexOf(search, selection);
        if (previous > -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Selection.setSelection((Spannable) textView.getText(),
                        selectionStart = previous, selectionEnd = previous
                                + search.length());
            } else {
                // EditText class is needed here
                ((EditText) textView).setSelection(selectionStart = previous,
                        selectionEnd = previous + search.length());
            }
            if (!textView.isFocused()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    textView.setCursorVisible(true);
                }
                textView.requestFocus();
            }
        } else { // wrap
            previous = text.lastIndexOf(search);
            if (previous > -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Selection.setSelection((Spannable) textView.getText(),
                            selectionStart = previous, selectionEnd = previous
                                    + search.length());
                } else {
                    // EditText class is needed here
                    ((EditText) textView).setSelection(
                            selectionStart = previous, selectionEnd = previous
                                    + search.length());
                }
                if (!textView.isFocused()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        textView.setCursorVisible(true);
                    }
                    textView.requestFocus();
                }
            }
        }
        if (textView.isFocused()) {
            hideSoftKeyboard();
        }
    }

    private class LoadTextTask extends AsyncTask<TextLoaderParam, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            textView.setText("");
            progressDialog = ProgressDialog.show(getActivity(), "",
                    "Loading ...", true);
        }

        @Override
        protected String doInBackground(TextLoaderParam... params) {
            final TextLoaderParam param = params[0];
            return TextLoader.create(param.context, param.uri, param.charset)
                    .read();
        }

        @Override
        protected void onPostExecute(String result) {
            restyle();
            textView.setText(result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Selection.setSelection(
                        (Spannable) textView.getText(), selectionStart, selectionEnd);
            } else {
                ((EditText) textView).setSelection(selectionStart, selectionEnd);
            }
            progressDialog.dismiss();
        }

    }

    static class TextLoaderParam {
        Context context;
        Uri uri;
        String charset;
    }

    public void onVolumeKeyUp(int divisor) {
        (TextPager.create(textView, (ScrollView) scrollView, divisor)).up();
    }

    public void onVolumeKeyDown(int divisor) {
        (TextPager.create(textView, (ScrollView) scrollView, divisor)).down();
    }

    public interface OnContentTapListener {
        public void onContentTap();
    }
}
