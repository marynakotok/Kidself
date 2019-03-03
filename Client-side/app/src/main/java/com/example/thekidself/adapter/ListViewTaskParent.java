package com.example.thekidself.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListViewTaskParent extends ArrayAdapter<String> {

    private String[] id, status, child, title, description, endTime, startTime;
    Context context;
    String childData,ip;
    String[] nameChildDatabase;
    ArrayList<String> ids = new ArrayList<String>();
    Dialog dialogCheckStatusRequest;
    Button mSubmit;
    TextView mText;
    RadioGroup radioStatusGroup;
    RadioButton radioApproveButton, radioDeclineButton;


    public ListViewTaskParent(@NonNull Context context, String id[], String status[], String child[],
                                 String title[], String description[], String start_time[], String end_time[], String IP) {
        super(context, R.layout.items_style_task_parent);

        this.id = id;
        this.ip = IP;
        this.child = child;
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
            convertView = inflater.inflate(R.layout.items_style_task_parent, parent,false);
            viewHolder.mStartTime= convertView.findViewById(R.id.startTime);
            viewHolder.mChild = convertView.findViewById(R.id.child);
            viewHolder.mDescription = convertView.findViewById(R.id.description);
            viewHolder.mStatus = convertView.findViewById(R.id.status_task);
            viewHolder.mDelete = convertView.findViewById(R.id.delete);
            ids.add(id[position]);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String tableChild = "child";
        String methodGetData = "Selection";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetChild = new BackgroundTaskSelectDB(context);
        String sqlChild = "SELECT code_child, name, sex, bd, created_on, last_login FROM child WHERE code_child='" + child[position] + "';";
        childData = null;
        try {
            childData = backgroundTaskSelectDBGetChild.execute(ip, methodGetData, sqlChild, tableChild).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayChild = null;
        JSONObject objChild = null;
        try {
            jsonArrayChild= new JSONArray(childData);
            nameChildDatabase = new String[jsonArrayChild.length()];
            for (int i=0; i<jsonArrayChild.length(); i++)
            {
                objChild = jsonArrayChild.getJSONObject(i);
                nameChildDatabase[i] = objChild.getString("name");
                viewHolder.mChild.setText(nameChildDatabase[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.mDescription.setText(description[position]);
        viewHolder.mStartTime.setText(startTime[position]);
        if (status[position].equals("N"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.ic_no);
        else if (status[position].equals("Y"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.ic_yes);

        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this task for your child?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            delete(ids.get(position));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Dangerous!");
                alert.show();
            }
        });

        return convertView;
    }

    public long getItemId(int position) {
        return position;
    }

    public void delete (String task)
    {
        String methodRegister = "Register";
        BackgroundTaskExecuteDB backgroundTaskExecuteDBDeleteTask = new BackgroundTaskExecuteDB(context);
        String sql = "DELETE FROM task WHERE id='" + task +"';";
        backgroundTaskExecuteDBDeleteTask.execute(ip, methodRegister, sql);

        //TODO : update fragment
    }

    static class ViewHolder{
        TextView mStartTime;
        TextView mStatus;
        TextView mChild;
        TextView mDescription;
        TextView mDelete;
    }

}