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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.altakey.dawne.ViewerFragment.OnContentTapListener;
import com.gmail.altakey.dawne.util.ConfigKey;
import com.gmail.altakey.dawne.util.ObjectSerializer;
import com.gmail.altakey.dawne.util.RecentFile;
import com.gmail.altakey.dawne.util.StackRecentFiles;

import java.io.IOException;

public class NewMainActivity extends ActionBarActivity implements OnContentTapListener {

    private static final int BROWSE_FILE_REQUEST_CODE = 4869;
    private static final String TAG = "dawne";

    private ActionBar mActionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private StackRecentFiles mStackRecentFiles;
    private RecentFilesAdapter mDrawerAdapter;

    private Handler mHandler;
    private final Runnable mHideActionBarDelayed = new Runnable() {
        @Override
        public void run() {
            mActionBar.hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mHandler = new Handler();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String recentFiles = pref.getString(ConfigKey.RECENT_FILES, null);
        if (recentFiles == null || recentFiles.equals("")) {
            mStackRecentFiles = new StackRecentFiles();
        } else {
            try {
                mStackRecentFiles = (StackRecentFiles) ObjectSerializer.deserialize(recentFiles);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_HOME_AS_UP);

        mDrawerAdapter = new RecentFilesAdapter(this, mStackRecentFiles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                final ViewerFragment f = (ViewerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.content_frame);
                if (f != null) {
                    if (f.isSearching()) {
                        mActionBar.hide();
                    } else {
                        hideActionBarDelayed();
                    }
                    mActionBar.setTitle("");
                } else {
                    mActionBar.setTitle(getTitle());
                }
                supportInvalidateOptionsMenu();

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                stopHideActionBarDelayed();
                if (!mActionBar.isShowing()) {
                    mActionBar.show();
                }
                mActionBar.setTitle(R.string.recent_file);
                supportInvalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String type = intent.getType();
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if (type.startsWith("text/")) {
                handleTextFile(intent.getData());
            }
        }

        final Button buttonBrowse = (Button) findViewById(R.id.buttonBrowse);
        buttonBrowse.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                browseFile();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_main_open:
                browseFile();
                return true;
            case R.id.menu_main_clear_recent:
                clearRecentFiles();
                return true;
            case R.id.menu_main_config:
                startActivity(new Intent(this, ConfigActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BROWSE_FILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final Uri uri = data.getData();
                final String intentType = data.getType();
                final String mimeType = getContentResolver().getType(uri);
                final String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                Log.d(TAG, "File type: " + intentType + "::" + extension + "::" + mimeType);
                if ((intentType != null && intentType.startsWith("text/")) ||
                        (mimeType != null && mimeType.startsWith("text/")) ||
                        (extension != null && extension.equals("txt"))) {
                    final ViewerFragment f = ViewerFragment.newInstance(uri.toString());
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f)
                            .commit();
                    // Save recent file
                    saveRecentFiles(uri);
                    hideActionBarDelayed();
                    Log.d(TAG, "Opening file: " + uri.toString());
                } else {
                    Toast.makeText(this, "Unsupported file type", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean scrollerEnabled = pref.getBoolean(ConfigKey.SCROLL_BY_VOLUME_KEYS, false);

        if (scrollerEnabled) {
            int divisor = Integer.parseInt(pref.getString(ConfigKey.SCROLL_LINES, "2"));

            int action = event.getAction();
            int keyCode = event.getKeyCode();
            final FragmentManager fm = getSupportFragmentManager();
            final ViewerFragment f = (ViewerFragment) fm.findFragmentById(R.id.content_frame);
            if (f != null) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        if (action == KeyEvent.ACTION_DOWN) {
                            f.onVolumeKeyUp(divisor);
                        }
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        if (action == KeyEvent.ACTION_DOWN) {
                            f.onVolumeKeyDown(divisor);
                        }
                        return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    void browseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, BROWSE_FILE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Sorry, there's no file browser available", Toast.LENGTH_LONG)
                    .show();
        }
    }

    void handleTextFile(Uri uri) {
        final ViewerFragment f = ViewerFragment.newInstance(uri.toString());
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        // Save recent file
        saveRecentFiles(uri);
        Log.d(TAG, "Viewing file: " + uri.getLastPathSegment());
        hideActionBarDelayed();
    }

    void saveRecentFiles(Uri uri) {
        mStackRecentFiles.pushRecentFile(new RecentFile(uri.getLastPathSegment(), uri.toString()));
        mDrawerAdapter.notifyDataSetChanged();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = pref.edit();
        try {
            editor.putString(ConfigKey.RECENT_FILES, ObjectSerializer.serialize(mStackRecentFiles));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    void clearRecentFiles() {
        mStackRecentFiles.clear();
        mDrawerAdapter.notifyDataSetChanged();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(ConfigKey.RECENT_FILES, "").commit();

    }

    void hideActionBarDelayed() {
        mHandler.postDelayed(mHideActionBarDelayed, 5000);
    }

    void stopHideActionBarDelayed() {
        mHandler.removeCallbacks(mHideActionBarDelayed);
    }

    boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawerList);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String uriString = ((RecentFile) mDrawerAdapter.getItem(position)).getUri();
            Log.d(TAG, "Re-opening file: " + uriString);
            final ViewerFragment f = ViewerFragment.newInstance(uriString);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f)
                    .commit();
            mDrawerLayout.closeDrawer(mDrawerList);
            // Save recent file
            saveRecentFiles(Uri.parse(uriString));
        }
    }

    @Override
    public void onContentTap() {
        if (!mActionBar.isShowing()) {
            mActionBar.show();
            hideActionBarDelayed();
        }
    }
}
