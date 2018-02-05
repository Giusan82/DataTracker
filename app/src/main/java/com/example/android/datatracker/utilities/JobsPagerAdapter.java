package com.example.android.datatracker.utilities;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.datatracker.ChartFragment;
import com.example.android.datatracker.DataFragment;
import com.example.android.datatracker.R;

public class JobsPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;


    public JobsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new ChartFragment();
        }else{
            return new DataFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position){
        if (position == 0){
            return mContext.getString(R.string.tab_chart);
        }else{
            return mContext.getString(R.string.tab_data);
        }
    }

}
