package com.example.android.datatracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.datatracker.utilities.JobsPagerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import com.example.android.datatracker.data.DataContract.DataEntry;


public class ChartFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    public static final int CHART_DATA_LOADER_ID = 300;
    private LineChart mChart;
    private Uri mCurrentUri;
    private int mTaskId;
    private String mNameTask;
    private ArrayList<Entry> entries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_job);
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);

        // Examine the intent used to launch this activity
        Intent intent = getActivity().getIntent();
        mNameTask = intent.getStringExtra("name");
        mCurrentUri = intent.getData();
        mTaskId = Integer.valueOf(mCurrentUri.getLastPathSegment());

        mChart = (LineChart) rootView.findViewById(R.id.lc_chart);


        getActivity().getSupportLoaderManager().initLoader(CHART_DATA_LOADER_ID, null, this);



        return rootView;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity activity = getActivity();
            if(activity != null) activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case CHART_DATA_LOADER_ID:
                String selection = DataEntry.COLUMN_TASK_ID + "=?";
                String selectionArgs1[]={String.valueOf(mTaskId)};
                Uri dataUri = DataEntry.CONTENT_URI;
                String sortOrder = DataEntry.COLUMN_CREATION_DATE + " ASC";
                return new CursorLoader(getContext(),
                        dataUri,
                        null,//projection,
                        selection,
                        selectionArgs1,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        // Proceed with moving to the first row of the cursor and reading data from it
        if(loader.getId() == CHART_DATA_LOADER_ID){
            entries = new ArrayList<>();
//            do {
//                entries.add(new Entry(x, y));
//            }while (cursor.moveToNext());
            while(cursor.moveToNext()){
                float x = cursor.getFloat(cursor.getColumnIndex(DataEntry.COLUMN_X));
                float y = cursor.getFloat(cursor.getColumnIndex(DataEntry.COLUMN_Y));
                entries.add(new Entry(x, y));
            }
            for (int i = 0; i < entries.size(); i++){
                Entry currentEntry = entries.get(i);
                Log.e("ChartFragment", currentEntry.toString());
            }
            LineDataSet dataSet = new LineDataSet(entries, mNameTask);
            LineData data = new LineData(dataSet);
            if(entries.size() > 0){
                mChart.setAutoScaleMinMaxEnabled(true);
                mChart.animateXY(3000, 3000);
                mChart.setData(data);
                //mChart.setBackground(getContext().getResources().getDrawable(R.drawable.gradient_dark_grey));
                mChart.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                mChart.getData().setValueTextColor(Color.WHITE);
                mChart.getXAxis().setTextColor(Color.WHITE);
                mChart.getAxisLeft().setTextColor(Color.WHITE);
                mChart.getAxisRight().setTextColor(Color.WHITE);
                mChart.getLegend().setTextColor(Color.WHITE);
                mChart.getDescription().setEnabled(false);
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mChart.invalidate();
        entries.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Chart-OnResume", "onResume");
    }


}
