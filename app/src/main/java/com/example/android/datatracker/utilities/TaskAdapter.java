package com.example.android.datatracker.utilities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.datatracker.R;
import com.example.android.datatracker.data.TasksContract;

import java.text.SimpleDateFormat;
import java.util.Date;

import at.grabner.circleprogress.CircleProgressView;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    private Context mContext;
    private Cursor mCursor;

    public final EntryOnClickHandler mOnClick;

    public interface EntryOnClickHandler{
        void onClickDelete (int id, String name);
        void onClickView (int id, String name, double duration, double interval);
        void onClickEdit(int id, int position);
    }
    /**
     * Constructor
     */
    public TaskAdapter(Context context, EntryOnClickHandler entry){

        mContext = context;
        mOnClick = entry;
    }

    /**
     * Cache of the view objects for a list.
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int listId = R.layout.list;

        View view = LayoutInflater.from(mContext).inflate(listId, viewGroup, false);
        TaskViewHolder viewHolder = new TaskViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_NAME));
        holder.itemView.setTag(mCursor.getInt(mCursor.getColumnIndex(TasksContract.TasksEntry._ID)));
        holder.tv_name.setText(name);
        holder.cv_completing.setShowBlock(true);
        double taskCompleted = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_TASKS_COMPLETED));
        double duration = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_DURATION));
        double durationUnit = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_DURATION_UNIT));
        double interval = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_INTERVAL));
        double intervalUnit = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_INTERVAL_UNIT));
        holder.cv_completing.setValueAnimated((int)progressJob(taskCompleted, duration*durationUnit, interval*intervalUnit), 1500);
        long creationDate = mCursor.getLong(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_CREATION_DATE));
        Date date = new Date(creationDate);
        String timeCreation = formatDate(date) + ", " + formatTime(date);
        holder.tv_date.setText(timeCreation);
        String description = mCursor.getString(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_DESCRIPTION));
        holder.tv_description.setText(description);
        int alarm = mCursor.getInt(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_ALARM));
        if (alarm == 1){
            holder.iv_alarm.setImageResource(R.drawable.ic_alarm_on_24dp);
        }
        holder.setIsRecyclable(false);
    }

    private double progressJob(double taskCompleted, double duration, double interval){
        double total = duration / interval;
        double progress = taskCompleted / total *100;
        return progress;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(dateObject);
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

    public class TaskViewHolder extends RecyclerView.ViewHolder{
        public  TextView tv_name;
        public TextView tv_date;
        public TextView tv_delete;
        public ImageView iv_delete;
        public ImageView iv_edit;
        public TextView tv_description;
        public ImageView iv_alarm;
        public RelativeLayout list_container;
        public CircleProgressView cv_completing;

        public TaskViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            this.tv_date = (TextView) view.findViewById(R.id.tv_date);
            this.list_container = (RelativeLayout) view.findViewById(R.id.list_container);
            this.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            this.tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            this.iv_edit = (ImageView) view.findViewById(R.id.iv_edit);
            this.tv_description = (TextView) view.findViewById(R.id.tv_description);
            this.iv_alarm = (ImageView) view.findViewById(R.id.iv_alarm);
            this.cv_completing = (CircleProgressView) view.findViewById(R.id.cv_completing);
            this.iv_delete.setOnClickListener(mDeleteListener);
            this.list_container.setOnClickListener(mViewListener);
            this.iv_edit.setOnClickListener(mEditListener);
        }

        private View.OnClickListener mDeleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(TasksContract.TasksEntry._ID));
                String name = mCursor.getString(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_NAME));
                mOnClick.onClickDelete(id, name);
            }
        };
        private View.OnClickListener mViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(TasksContract.TasksEntry._ID));
                String name = mCursor.getString(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_NAME));
                double duration = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_DURATION));
                double durationUnit = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_DURATION_UNIT));
                double interval = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_INTERVAL));
                double intervalUnit = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TasksEntry.COLUMN_INTERVAL_UNIT));

                mOnClick.onClickView(id, name, duration*durationUnit, interval*intervalUnit);
            }
        };

        private View.OnClickListener mEditListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(TasksContract.TasksEntry._ID));
                mOnClick.onClickEdit(id, position);
            }
        };
    }
}
