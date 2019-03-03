package com.example.thekidself.menu.parent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.adapter.ListViewTaskParent;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;


public class TaskAreaFragment extends Fragment {


    View view;
    SharedPreferences mSharedPreferences;
    String codeParent;
    String methodGetData, IP, task;
    ListView list;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener1, mTimeSetListener2;
    ListViewTaskParent adaptor;
    Dialog dialogCreateTask;
    TextView mStartTime, mEndTime, mDuration;
    EditText mTitle, mDescription;
    Button mCreateTask;
    String family, child;
    int errorCount;
    RadioButton radioChild1Button, radioChild3Button, radioChild2Button, radioChild4Button, radioChild5Button, radioChild6Button;
    RadioGroup radioChildrenGroup;
    String selected_child;
    String Child1Database, Child2Database, Child3Database, Child4Database, Child5Database, Child6Database;
    String [] idDatabase, childDatabase, titleDatabase, descriptionDatabase, startTimeDatabase, endTimeDatabase, statusDatabase, nameChildDatabase, codeChildDatabase;
    long difference;
    String text, start_time, end_time, start_time_new, end_time_new;

    public TaskAreaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_task_area, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringTaskArea));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSharedPreferences = view.getContext().getSharedPreferences("Parent", MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateTask();
            }
        });

        IP = getString(R.string.stringIP);
        methodGetData = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetTask = new BackgroundTaskSelectDB(view.getContext());
        String sqlTask = "SELECT id, title, description, parent, child, status_task, ringtone, start_time, end_time, created_on FROM task WHERE parent='" + codeParent + "';";
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
            childDatabase = new String[jsonArrayTask.length()];
            statusDatabase = new String[jsonArrayTask.length()];
            titleDatabase = new String[jsonArrayTask.length()];
            startTimeDatabase = new String[jsonArrayTask.length()];
            endTimeDatabase = new String[jsonArrayTask.length()];
            descriptionDatabase = new String[jsonArrayTask.length()];
            for (int i=0; i<jsonArrayTask.length(); i++)
            {
                objTask = jsonArrayTask.getJSONObject(i);
                idDatabase[i] = objTask.getString("id");
                childDatabase[i] = objTask.getString("child");
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
            ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.header_style_task_parent, list, false);
            list.addHeaderView(header);

            adaptor = new ListViewTaskParent(getContext(), idDatabase, statusDatabase, childDatabase, titleDatabase, descriptionDatabase, startTimeDatabase, endTimeDatabase, IP);
            list.setAdapter(adaptor);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("So far, you do not have any task! You may create one by clicking on the down button!").setCancelable(false).setPositiveButton("Ok",
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



    private void CreateTask(){

        dialogCreateTask = new Dialog(getContext());
        dialogCreateTask.setContentView(R.layout.dialog_create_task);
        mTitle = dialogCreateTask.findViewById(R.id.title);
        mDescription = dialogCreateTask.findViewById(R.id.description);
        mCreateTask = dialogCreateTask.findViewById(R.id.CreateTask);
        mStartTime = dialogCreateTask.findViewById(R.id.startTime);
        mEndTime = dialogCreateTask.findViewById(R.id.endTime);
        mDuration = dialogCreateTask.findViewById(R.id.Duration);

        dialogCreateTask.show();
        mTitle.setEnabled(true);
        mDescription.setEnabled(true);
        mCreateTask.setEnabled(true);
        mStartTime.setEnabled(true);
        mEndTime.setEnabled(false);
        mDuration.setEnabled(false);

        radioChildrenGroup = dialogCreateTask.findViewById(R.id.radioChildren);
        radioChild1Button = dialogCreateTask.findViewById(R.id.radioChild1);
        radioChild2Button = dialogCreateTask.findViewById(R.id.radioChild2);
        radioChild3Button = dialogCreateTask.findViewById(R.id.radioChild3);
        radioChild4Button = dialogCreateTask.findViewById(R.id.radioChild4);
        radioChild5Button = dialogCreateTask.findViewById(R.id.radioChild5);
        radioChild6Button = dialogCreateTask.findViewById(R.id.radioChild6);


        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.MILLISECOND);

                TimePickerDialog dialog = new TimePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener1,
                        hour, minute, DateFormat.is24HourFormat(getActivity()));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String time = hour + ":" + minute;
                mStartTime.setText(time);
                mEndTime.setEnabled(true);
            }
        };

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.MILLISECOND);

                TimePickerDialog dialog = new TimePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener2,
                        hour, minute, DateFormat.is24HourFormat(getActivity()));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String time = hour + ":" + minute;
                mEndTime.setText(time);
                start_time = mStartTime.getText().toString().trim();
                end_time = mEndTime.getText().toString().trim();
                start_time_new = start_time + ":00";
                end_time_new = end_time + ":00";
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(start_time_new);
                    date2 = format.parse(end_time_new);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                difference = date2.getTime() - date1.getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
                text = "Duration: " + minutes + " min.";
                mDuration.setText(text);
                mStartTime.setEnabled(false);
            }
        };

        String sqlFamily = "SELECT parent_1,parent_2,child_1,child_2,child_3,child_4,child_5,child_6 FROM family WHERE parent_1='" + codeParent + "' OR parent_2='" + codeParent + "';";
        String tableFamily = "family";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetFamily = new BackgroundTaskSelectDB(view.getContext());
        family = null;
        JSONArray jsonArrayFamily = null;
        JSONObject objFamily = null;
        try {
            family = backgroundTaskSelectDBGetFamily.execute(IP, methodGetData, sqlFamily, tableFamily).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            jsonArrayFamily = new JSONArray(family);
            objFamily = jsonArrayFamily.getJSONObject(0);
            Child1Database = objFamily.getString("child_1");
            Child2Database = objFamily.getString("child_2");
            Child3Database = objFamily.getString("child_3");
            Child4Database = objFamily.getString("child_4");
            Child5Database = objFamily.getString("child_5");
            Child6Database = objFamily.getString("child_6");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String tableChild = "child";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetChild = new BackgroundTaskSelectDB(view.getContext());
        String sqlChild = "SELECT code_child, name, sex, bd, created_on, last_login FROM child WHERE code_child='" + Child1Database + "' OR code_child='" + Child2Database
                + "' OR code_child='" + Child3Database + "' OR code_child='" + Child4Database + "' OR code_child='" + Child5Database + "' OR code_child='" + Child6Database + "';";
        child = null;
        try {
            child = backgroundTaskSelectDBGetChild.execute(IP, methodGetData, sqlChild, tableChild).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayChild = null;
        JSONObject objChild = null;
        try {
            jsonArrayChild = new JSONArray(child);
            nameChildDatabase = new String[jsonArrayChild.length()];
            codeChildDatabase = new String[jsonArrayChild.length()];
            for (int i=0; i<jsonArrayChild.length(); i++)
            {
                objChild = jsonArrayChild.getJSONObject(i);
                nameChildDatabase[i] = objChild.getString("name");
                codeChildDatabase[i] = objChild.getString("code_child");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArrayChild.length() == 1)
        {
            if (!codeChildDatabase[0].equals("null"))
            {
                radioChild1Button.setVisibility(View.VISIBLE);
                radioChild1Button.setText(nameChildDatabase[0]);
            }
        } else if (jsonArrayChild.length() == 2)
            {
                if (!codeChildDatabase[1].equals("null"))
                {
                    radioChild1Button.setVisibility(View.VISIBLE);
                    radioChild1Button.setText(nameChildDatabase[0]);
                    radioChild2Button.setVisibility(View.VISIBLE);
                    radioChild2Button.setText(nameChildDatabase[1]);

                }
            } else if (jsonArrayChild.length() == 3)
        {
            if (!codeChildDatabase[2].equals("null"))
            {
                radioChild1Button.setVisibility(View.VISIBLE);
                radioChild1Button.setText(nameChildDatabase[0]);
                radioChild2Button.setVisibility(View.VISIBLE);
                radioChild2Button.setText(nameChildDatabase[1]);
                radioChild3Button.setVisibility(View.VISIBLE);
                radioChild3Button.setText(nameChildDatabase[2]);

            }
        } else if (jsonArrayChild.length() == 4)
        {
            if (!codeChildDatabase[3].equals("null"))
            {
                radioChild1Button.setVisibility(View.VISIBLE);
                radioChild1Button.setText(nameChildDatabase[0]);
                radioChild2Button.setVisibility(View.VISIBLE);
                radioChild2Button.setText(nameChildDatabase[1]);
                radioChild3Button.setVisibility(View.VISIBLE);
                radioChild3Button.setText(nameChildDatabase[2]);
                radioChild4Button.setVisibility(View.VISIBLE);
                radioChild4Button.setText(nameChildDatabase[3]);

            }
        } else if (jsonArrayChild.length() == 5)
        {
            if (!codeChildDatabase[4].equals("null"))
            {
                radioChild1Button.setVisibility(View.VISIBLE);
                radioChild1Button.setText(nameChildDatabase[0]);
                radioChild2Button.setVisibility(View.VISIBLE);
                radioChild2Button.setText(nameChildDatabase[1]);
                radioChild3Button.setVisibility(View.VISIBLE);
                radioChild3Button.setText(nameChildDatabase[2]);
                radioChild4Button.setVisibility(View.VISIBLE);
                radioChild4Button.setText(nameChildDatabase[3]);
                radioChild5Button.setVisibility(View.VISIBLE);
                radioChild5Button.setText(nameChildDatabase[4]);

            }
        } else if (jsonArrayChild.length() == 6)
        {
            if (!codeChildDatabase[5].equals("null"))
            {
                radioChild1Button.setVisibility(View.VISIBLE);
                radioChild1Button.setText(nameChildDatabase[0]);
                radioChild2Button.setVisibility(View.VISIBLE);
                radioChild2Button.setText(nameChildDatabase[1]);
                radioChild3Button.setVisibility(View.VISIBLE);
                radioChild3Button.setText(nameChildDatabase[2]);
                radioChild4Button.setVisibility(View.VISIBLE);
                radioChild4Button.setText(nameChildDatabase[3]);
                radioChild5Button.setVisibility(View.VISIBLE);
                radioChild5Button.setText(nameChildDatabase[4]);
                radioChild6Button.setVisibility(View.VISIBLE);
                radioChild6Button.setText(nameChildDatabase[5]);
            }
        }

        mCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTitle.setBackgroundResource(R.drawable.field_parent);
                mDescription.setBackgroundResource(R.drawable.field_parent);
                mEndTime.setBackgroundResource(R.drawable.field_parent);
                mStartTime.setBackgroundResource(R.drawable.field_parent);

                errorCount = 0;
                String title = mTitle.getText().toString().trim();
                String description = mDescription.getText().toString().trim();
                start_time = mStartTime.getText().toString().trim();
                end_time = mEndTime.getText().toString().trim();
                start_time_new = start_time + ":00";
                end_time_new = end_time + ":00";

                if (isEmpty(start_time))
                {
                    mStartTime.setBackgroundResource(R.drawable.field_parent_error);
                    errorCount++;
                }

                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(start_time_new);
                    date2 = format.parse(end_time_new);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                difference = date2.getTime() - date1.getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);

                if (isEmpty(end_time))
                {
                    mEndTime.setBackgroundResource(R.drawable.field_parent_error);
                    errorCount++;
                } else if (minutes<0)
                    {
                        System.out.println("min: " + minutes);
                        mEndTime.setBackgroundResource(R.drawable.field_parent_error);
                        errorCount++;
                        Toast.makeText(getContext(), "The End Time of the task cannot be less than Start Time! Change the End Time, please!", Toast.LENGTH_LONG).show();
                    }

                if (isEmpty(description)) {
                    mDescription.setBackgroundResource(R.drawable.field_parent_error);
                    errorCount++;
                }

                if (isEmpty(title)) {
                    mTitle.setBackgroundResource(R.drawable.field_parent_error);
                    errorCount++;
                }

                if (errorCount == 0)
                {
                    int selectedId = radioChildrenGroup.getCheckedRadioButtonId();
                    RadioButton radioChildButton = dialogCreateTask.findViewById(selectedId);
                    String code_child = radioChildButton.getText().toString();

                    if (code_child.equals(nameChildDatabase[0]))
                        selected_child = codeChildDatabase[0];
                    else if (code_child.equals(nameChildDatabase[1]))
                        selected_child = codeChildDatabase[1];
                    else if (code_child.equals(nameChildDatabase[2]))
                        selected_child = codeChildDatabase[2];
                    else if (code_child.equals(nameChildDatabase[3]))
                        selected_child = codeChildDatabase[3];
                    else if (code_child.equals(nameChildDatabase[4]))
                        selected_child = codeChildDatabase[4];
                    else if (code_child.equals(nameChildDatabase[5]))
                        selected_child = codeChildDatabase[5];

                    putToDatabase(title, description, selected_child, start_time_new, end_time_new);
                    dialogCreateTask.cancel();
                    Snackbar.make(view, "You have just created a task!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    public void putToDatabase(String title, String description, String code_child, String start_time, String end_time){

        int length_code_request = 6;
        AdditionalMethods m = new AdditionalMethods();
        String code_task = m.GeneratePassword(length_code_request);
        String status_task = "N";
        String methodRegister = "Register";

        BackgroundTaskExecuteDB backgroundTaskExecuteDBTask = new BackgroundTaskExecuteDB(getContext());
        String sqlTask = "INSERT INTO task (id, title, description, status_task, child, parent, start_time, end_time) VALUES ('" + code_task +
                "', '" + title + "', '" + description + "', '" + status_task + "', '" + code_child + "', '" + codeParent + "', '" + start_time + "', '" + end_time + "');";
        backgroundTaskExecuteDBTask.execute(IP, methodRegister, sqlTask);

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.detach(this).attach(this).commit();

    }

}
