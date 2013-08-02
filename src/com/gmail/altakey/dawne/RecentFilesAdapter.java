/**
 * Copyright (C) 2013 Takahiro Yoshimura, Renjaya Raga Zenta
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.altakey.dawne.util.StackRecentFiles;

public class RecentFilesAdapter extends BaseAdapter {

    private StackRecentFiles mStack;
    private LayoutInflater mInflater;

    public RecentFilesAdapter(Context context, StackRecentFiles stack) {
        mStack = stack;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mStack.size();
    }

    @Override
    public Object getItem(int position) {
        return mStack.getRecentFileAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.text.setText(mStack.getRecentFileAt(position).getFileName());
        return view;
    }

    static class ViewHolder {
        TextView text;
    }

}
