package com.example.thekidself.menu.child;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.adapter.ListViewTaskChild;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class TaskChildFragment extends Fragment {

    View view;
    SharedPreferences mSharedPreferences;
    String codeChild, IP, methodGetData, task;
    ListView list;
    ListViewTaskChild adaptor;
    String[] idDatabase, parentDatabase, statusDatabase, titleDatabase, startTimeDatabase, endTimeDatabase, descriptionDatabase;


    public TaskChildFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_task_child, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringTaskChild));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSharedPreferences = view.getContext().getSharedPreferences("Child", MODE_PRIVATE);
        codeChild = mSharedPreferences.getString("codeChild","");

        IP = getString(R.string.stringIP);
        methodGetData = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetTask = new BackgroundTaskSelectDB(view.getContext());
        String sqlTask = "SELECT id, title, description, parent, child, status_task, ringtone, start_time, end_time, created_on FROM task WHERE child='" + codeChild + "';";
        String tableTask = "task";
        task = null;
        JSONArray jsonArrayTask = null;
        JSONObject objTask = null;
        try {
            task = backgroundTaskSelectDBGetTask.execute(IP, methodGetData, sqlTask, tableTask).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            jsonArrayTask = new JSONArray(task);
            idDatabase = new String[jsonArrayTask.length()];
            parentDatabase = new String[jsonArrayTask.length()];
            statusDatabase = new String[jsonArrayTask.length()];
            titleDatabase = new String[jsonArrayTask.length()];
            startTimeDatabase = new String[jsonArrayTask.length()];
            endTimeDatabase = new String[jsonArrayTask.length()];
            descriptionDatabase = new String[jsonArrayTask.length()];
            for (int i=0; i<jsonArrayTask.length(); i++)
            {
                objTask = jsonArrayTask.getJSONObject(i);
                idDatabase[i] = objTask.getString("id");
                parentDatabase[i] = objTask.getString("parent");
                statusDatabase[i] = objTask.getString("status_task");
                descriptionDatabase[i] = objTask.getString("description");
                titleDatabase[i] = objTask.getString("title");
                endTimeDatabase[i] = objTask.getString("end_time");
                startTimeDatabase[i] = objTask.getString("start_time");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonArrayTask.length() != 0)
        {
            list = view.findViewById(R.id.listView);
            ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.header_style_task_child, list, false);
            list.addHeaderView(header);

            adaptor = new ListViewTaskChild(getContext(), idDatabase, statusDatabase, parentDatabase, titleDatabase, descriptionDatabase, startTimeDatabase, endTimeDatabase, IP);
            list.setAdapter(adaptor);

//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//                    System.out.println("position: " + (position-1));
//
//                }
//            });


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("So far, you do not have any task!").setCancelable(false).setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }

        return view;
    }

}
