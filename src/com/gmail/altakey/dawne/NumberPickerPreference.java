/**
 * Copyright (C) 2013 Takahiro Yoshimura
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

import android.preference.DialogPreference;
import android.widget.NumberPicker;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.TypedArray;

public class NumberPickerPreference extends DialogPreference {
    private int mDefault, mMin, mMax;
    private NumberPicker mPicker;
    private static final String androidNS =
        "http://schemas.android.com/apk/res/android";

    public NumberPickerPreference(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        handleAttributeSet(attr);
    }
    private void handleAttributeSet (AttributeSet attr) {
        mMin = attr.getAttributeIntValue(null, "min", 2);
        mMax = attr.getAttributeIntValue(androidNS, "max", 96);
    }
    @Override
    protected View onCreateDialogView() {
        mPicker = new NumberPicker(getContext());
        mPicker.setMinValue(mMin);
        mPicker.setMaxValue(mMax);
        mPicker.setValue(Integer.parseInt(
            getPersistedString(Integer.toString(mDefault))
        ));
        return mPicker;
    }
    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        if (restore)
            mDefault = Integer.parseInt(
                getPersistedString(Integer.toString(mDefault))
            );
        else {
            mDefault = (Integer)defaultValue;
            if (shouldPersist())
                persistString(Integer.toString(mDefault));
        }
    }
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        mDefault = a.getInt(index, 12);
        return mDefault;
    }
    @Override
    protected void onDialogClosed(boolean result) {
        super.onDialogClosed(result);
        if (result) {
            int new_value = mPicker.getValue();
            if (callChangeListener(new_value)) {
                persistString(Integer.toString(new_value));
            }
        }
    }
}
