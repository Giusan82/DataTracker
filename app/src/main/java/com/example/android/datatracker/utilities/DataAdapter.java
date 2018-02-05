package com.example.android.datatracker.utilities;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.datatracker.R;
import com.example.android.datatracker.data.DataContract.DataEntry;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>{
    private Context mContext;
    private Cursor mCursor;

    public final DataAdapter.EntryOnClickHandler mOnClick;

    public interface EntryOnClickHandler{
        void onClickDelete (int id, int position);
        void onClickView (int id, int position);
        void onClickEdit(int id, int position);
    }
    /**
     * Constructor
     */
    public DataAdapter(Context context, DataAdapter.EntryOnClickHandler entry){

        mContext = context;
        mOnClick = entry;
    }

    /**
     * Cache of the view objects for a list.
     */
    @Override
    public DataAdapter.DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int listId = R.layout.list_data;

        View view = LayoutInflater.from(mContext).inflate(listId, viewGroup, false);
        DataAdapter.DataViewHolder viewHolder = new DataAdapter.DataViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DataAdapter.DataViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        double y = mCursor.getDouble(mCursor.getColumnIndex(DataEntry.COLUMN_Y));
        holder.tv_y.setText(String.valueOf(y));

        double x = mCursor.getDouble(mCursor.getColumnIndex(DataEntry.COLUMN_X));
        holder.tv_x.setText(String.valueOf(x));
        holder.itemView.setTag(mCursor.getInt(mCursor.getColumnIndex(DataEntry._ID)));
        //delete(mCursor, holder);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor){
            return null;
        }
        this.mCursor = newCursor;
        if(newCursor != null){
            this.notifyDataSetChanged();
        }
        return mCursor;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_y;
        public TextView tv_x;
        public LinearLayout list_container;

        public DataViewHolder(View view) {
            super(view);
            this.tv_y = (TextView) view.findViewById(R.id.tv_Y);
            this.tv_x = (TextView) view.findViewById(R.id.tv_X);
            this.list_container = (LinearLayout) view.findViewById(R.id.list_container);
            this.list_container.setOnClickListener(mViewListener);
        }

        private View.OnClickListener mDeleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(DataEntry._ID));
                mOnClick.onClickDelete(id, position);
            }
        };
        private View.OnClickListener mViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(DataEntry._ID));
                mOnClick.onClickView(id, position);
            }
        };

        private View.OnClickListener mEditListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(DataEntry._ID));
                mOnClick.onClickEdit(id, position);
            }
        };
    }
}
