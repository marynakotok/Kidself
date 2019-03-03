package com.example.thekidself.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ListViewRequestChild extends ArrayAdapter<String> {

    private String[] id, status, parents, title, reason;
    Context context;
    String parentData;
    String[] nameParentDatabase;
    String ip;


    public ListViewRequestChild(@NonNull Context context, String id[], String status[], String parents[],
                                String title[], String reason[], String IP) {
        super(context, R.layout.items_style_request_child);

        this.id = id;
        this.parents = parents;
        this.status = status;
        this.title = title;
        this.reason = reason;
        this.context = context;
        this.ip = IP;
    }

    @Override
    public int getCount() {
        return id.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.items_style_request_child, parent,false);
            //viewHolder.mTitle= (TextView) convertView.findViewById(R.id.title);
            viewHolder.mParent = convertView.findViewById(R.id.parent);
            viewHolder.mReason = convertView.findViewById(R.id.reason);
            viewHolder.mStatus = convertView.findViewById(R.id.status_request);

            convertView.setTag(viewHolder);
        } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

        final String tableParent= "parent";
        String methodGetData = "Selection";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetParent = new BackgroundTaskSelectDB(context);
        String sqlParent= "SELECT code_parent, name, sex, bd, created_on, last_login, email, password_parent FROM parent WHERE code_parent='" + parents[position] + "';";
        parentData = null;
        try {
            parentData = backgroundTaskSelectDBGetParent.execute(ip, methodGetData, sqlParent, tableParent).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayParent = null;
        JSONObject objParent = null;
        try {
            jsonArrayParent = new JSONArray(parentData);
            nameParentDatabase = new String[jsonArrayParent.length()];
            for (int i=0; i<jsonArrayParent.length(); i++)
            {
                objParent = jsonArrayParent.getJSONObject(i);
                nameParentDatabase[i] = objParent.getString("name");
                viewHolder.mParent.setText(nameParentDatabase[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.mReason.setText(reason[position]);
        //viewHolder.mParent.setText(parents[position]);
        //viewHolder.mTitle.setText(title[position]);
        if (status[position].equals("P"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.status_process);
        else if (status[position].equals("D"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.status_declined);
        else if (status[position].equals("A"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.status_approved);

        return convertView;
    }

    static class ViewHolder{
        //TextView mTitle;
        TextView mStatus;
        TextView mParent;
        TextView mReason;
    }

}
