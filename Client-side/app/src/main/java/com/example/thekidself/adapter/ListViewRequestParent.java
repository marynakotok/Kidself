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
import android.widget.Toast;

import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListViewRequestParent extends ArrayAdapter<String> {

    private String[] id, status, child, title, reason;
    Context context;
    String childData,ip;
    String[] nameChildDatabase;
    ArrayList<String> ids = new ArrayList<String>();
    Dialog dialogCheckStatusRequest;
    Button mSubmit;
    TextView mText;
    RadioGroup radioStatusGroup;
    RadioButton radioApproveButton, radioDeclineButton;




    public ListViewRequestParent(@NonNull Context context, String id[], String status[], String child[],
                                 String title[], String reason[], String IP) {
        super(context, R.layout.items_style_request_parent);

        this.id = id;
        this.ip = IP;
        this.child = child;
        this.status = status;
        this.title = title;
        this.reason = reason;
        this.context = context;
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
            convertView = inflater.inflate(R.layout.items_style_request_parent, parent,false);
            //viewHolder.mTitle= (TextView) convertView.findViewById(R.id.title);
            viewHolder.mChild = convertView.findViewById(R.id.child);
            viewHolder.mReason = convertView.findViewById(R.id.reason);
            viewHolder.mStatus = convertView.findViewById(R.id.status_request);
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


        viewHolder.mReason.setText(reason[position]);
        //viewHolder.mTitle.setText(title[position]);
        if (status[position].equals("P"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.status_process);
        else if (status[position].equals("D"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.status_declined);
        else if (status[position].equals("A"))
            viewHolder.mStatus.setBackgroundResource(R.drawable.status_approved);

        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this request from your child?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            delete(ids.get(position));
                                            //Toast.makeText(context, "Request is deleted!", Toast.LENGTH_LONG).show();
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

    public void updateStatus(final String requestCurrent){

        dialogCheckStatusRequest = new Dialog(context);
        dialogCheckStatusRequest.setContentView(R.layout.dialog_check_status_request);
        mText = dialogCheckStatusRequest.findViewById(R.id.SetStatus);
        mSubmit = dialogCheckStatusRequest.findViewById(R.id.submit);

        dialogCheckStatusRequest.show();
        mText.setEnabled(true);
        mSubmit.setEnabled(true);

        radioStatusGroup = dialogCheckStatusRequest.findViewById(R.id.radioStatus);
        radioApproveButton = dialogCheckStatusRequest.findViewById(R.id.radioApprove);
        radioDeclineButton = dialogCheckStatusRequest.findViewById(R.id.radioDecline);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioStatusGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialogCheckStatusRequest.findViewById(selectedId);
                String statusSelected = radioButton.getText().toString();
                String statusRequest = "";
                if (statusSelected.equals("Approve"))
                    statusRequest = "A";
                else if (statusSelected.equals("Decline"))
                    statusRequest = "D";
                String methodRegister = "Register";
                BackgroundTaskExecuteDB backgroundTaskExecuteDBRequestUpdate = new BackgroundTaskExecuteDB(context);
                String sqlUpdate = "UPDATE request SET status_request='" + statusRequest + "' WHERE id='" + requestCurrent +"';";
                backgroundTaskExecuteDBRequestUpdate.execute(ip, methodRegister, sqlUpdate);
                dialogCheckStatusRequest.cancel();
                Toast.makeText(context, "The status of the request was changed!", Toast.LENGTH_LONG).show();

                    //TODO : update fragment
                }
            });
    }

    public void delete (String request)
    {
        String methodRegister = "Register";
        BackgroundTaskExecuteDB backgroundTaskExecuteDBDeleteRequest = new BackgroundTaskExecuteDB(context);
        String sql = "DELETE FROM request WHERE id='" + request +"';";
        backgroundTaskExecuteDBDeleteRequest.execute(ip, methodRegister, sql);

        //TODO : update fragment
    }


    static class ViewHolder{
        TextView mTitle;
        TextView mStatus;
        TextView mChild;
        TextView mReason;
        TextView mDelete;
    }

}
