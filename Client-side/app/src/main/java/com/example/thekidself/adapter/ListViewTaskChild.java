package com.example.thekidself.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListViewTaskChild extends ArrayAdapter<String> {

    private String[] id, status, parents, title, description, endTime, startTime;
    Context context;
    String ip;
    ArrayList<String> ids = new ArrayList<String>();
    Button mSubmit;
    TextView mText;
    RadioGroup radioStatusGroup;
    RadioButton radioYesButton, radioNoButton;
    Dialog dialogCheckStatusTask;


    public ListViewTaskChild (@NonNull Context context, String id[], String status[], String parents[],
                              String title[], String description[], String start_time[], String end_time[], String IP) {
        super(context, R.layout.items_style_task_child);

        this.id = id;
        this.ip = IP;
        this.parents = parents;
        this.status = status;
        this.title = title;
        this.description = description;
        this.context = context;
        this.endTime = end_time;
        this.startTime = start_time;
    }

    @Override
    public int getCount() {
        return id.length;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.items_style_task_child, parent,false);
            viewHolder.mStartTime= convertView.findViewById(R.id.startTime);
            viewHolder.mDescription = convertView.findViewById(R.id.description);
            viewHolder.mStatus = convertView.findViewById(R.id.status_task);
            viewHolder.mDuration = convertView.findViewById(R.id.duration);
            ids.add(id[position]);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mDescription.setText(description[position]);
        viewHolder.mStartTime.setText(startTime[position]);

        if (status[position].equals("N"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.ic_no);
        else if (status[position].equals("Y"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.ic_yes);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(startTime[position]);
            date2 = format.parse(endTime[position]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = date2.getTime() - date1.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        String text = minutes + " min.";
        viewHolder.mDuration.setText(text);

        viewHolder.mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(ids.get(position));
            }
        });

        return convertView;
    }

    public long getItemId(int position) {
        return position;
    }

    public void updateStatus(final String taskCurrent){

        dialogCheckStatusTask = new Dialog(getContext());
        dialogCheckStatusTask.setContentView(R.layout.dialog_check_status_task);
        mText = dialogCheckStatusTask.findViewById(R.id.doneTask);
        mSubmit = dialogCheckStatusTask.findViewById(R.id.submit);

        dialogCheckStatusTask.show();
        mText.setEnabled(true);
        mSubmit.setEnabled(true);

        radioStatusGroup = dialogCheckStatusTask.findViewById(R.id.radioStatus);
        radioYesButton = dialogCheckStatusTask.findViewById(R.id.radioYes);
        radioNoButton = dialogCheckStatusTask.findViewById(R.id.radioNo);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioStatusGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialogCheckStatusTask.findViewById(selectedId);
                String statusSelected = radioButton.getText().toString();
                String statusTask = "";
                if (statusSelected.equals("Yes"))
                    statusTask = "Y";
                else if (statusSelected.equals("No"))
                    statusTask = "N";

                if (statusTask.equals("Y"))
                {
                    String methodRegister = "Register";
                    BackgroundTaskExecuteDB backgroundTaskExecuteDBTaskUpdate = new BackgroundTaskExecuteDB(getContext());
                    String sqlUpdate = "UPDATE task SET status_task='" + statusTask + "' WHERE id='" + taskCurrent +"';";
                    System.out.println(sqlUpdate);
                    backgroundTaskExecuteDBTaskUpdate.execute(ip, methodRegister, sqlUpdate);
                    dialogCheckStatusTask.cancel();
                    Toast.makeText(getContext(), "The status of the request was changed!", Toast.LENGTH_LONG).show();
                    //adaptor.notifyDataSetChanged();
                } else
                {
                    dialogCheckStatusTask.cancel();
                    Toast.makeText(getContext(), "No changes!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    static class ViewHolder{
        TextView mStartTime;
        TextView mStatus;
        TextView mDuration;
        TextView mDescription;
    }

}