package com.example.android.datatracker.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import com.example.android.datatracker.R;


public class PreferenceTheme extends Preference {
    private String mSelectedTheme;

    public PreferenceTheme(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.preference_theme);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return getContext().getString(R.string.settings_theme_default);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        if (restorePersistedValue) {
            mSelectedTheme = getPersistedString(null);
        } else {
            mSelectedTheme = (String) defaultValue;
        }
        if (mSelectedTheme.equals(getContext().getString(R.string.settings_coral_theme_value))) {
            setSummary(getContext().getString(R.string.settings_coral_theme_label));
        }
        if (mSelectedTheme.equals(getContext().getString(R.string.settings_green_theme_value))) {
            setSummary(getContext().getString(R.string.settings_green_theme_label));
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false); // disable parent click
        View bt_Dark = holder.findViewById(R.id.theme_dark);
        View bt_Light = holder.findViewById(R.id.theme_light);
        bt_Dark.setClickable(true); // enable custom view click
        bt_Light.setClickable(true); // enable custom view click
        bt_Dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //persist value
                mSelectedTheme = getContext().getString(R.string.settings_green_theme_value);
                //summary label
                setSummary(getContext().getString(R.string.settings_green_theme_label));
                persistString(mSelectedTheme);
            }
        });
        bt_Light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //persist value
                mSelectedTheme = getContext().getString(R.string.settings_coral_theme_value);
                //summary lebel
                setSummary(getContext().getString(R.string.settings_coral_theme_label));
                persistString(mSelectedTheme);
            }
        });
    }
}
