package com.example.thekidself.menu.child;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.adapter.ListViewRequestChild;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;


public class RequestChildFragment extends Fragment {


    View view;
    String codeChild, IP;
    SharedPreferences mSharedPreferences;
    Dialog dialogCreateRequest;
    EditText mTitle, mReason;
    Button mCreateRequest;
    String parent, family, request;
    RadioButton radioParent1Button, radioParent2Button;
    RadioGroup radioParentsGroup;
    String[] nameParentsDatabase, emailParentsDatabase, codeParentsDatabase;
    String Parent1Database, Parent2Database;
    String selected_parent;
    int errorCount;
    String methodGetData;
    ListView list;
    int lenghtJsonParent;
    ListViewRequestChild adaptor;
    String[] idDatabase, titleDatabase, reasonDatabase, statusDatabase, parentsDatabase;

    public RequestChildFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_child, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringRequestChild));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSharedPreferences = view.getContext().getSharedPreferences("Child", MODE_PRIVATE);
        codeChild = mSharedPreferences.getString("codeChild","");
        IP = getString(R.string.stringIP);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRequest();
            }
        });

        methodGetData = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetRequest = new BackgroundTaskSelectDB(view.getContext());
        String sqlRequest = "SELECT id, title, reason, status_request, parent, child, created_on FROM request WHERE child='" + codeChild + "';";
        String tableRequest = "request";
        request = null;
        JSONArray jsonArrayRequest = null;
        JSONObject objRequest = null;
        try {
            request = backgroundTaskSelectDBGetRequest.execute(IP, methodGetData, sqlRequest, tableRequest).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            jsonArrayRequest = new JSONArray(request);
            idDatabase = new String[jsonArrayRequest.length()];
            parentsDatabase = new String[jsonArrayRequest.length()];
            statusDatabase = new String[jsonArrayRequest.length()];
            titleDatabase = new String[jsonArrayRequest.length()];
            reasonDatabase = new String[jsonArrayRequest.length()];
            for (int i=0; i<jsonArrayRequest.length(); i++)
            {
                objRequest = jsonArrayRequest.getJSONObject(i);
                idDatabase[i] = objRequest.getString("id");
                parentsDatabase[i] = objRequest.getString("parent");
                statusDatabase[i] = objRequest.getString("status_request");
                reasonDatabase[i] = objRequest.getString("reason");
                titleDatabase[i] = objRequest.getString("title");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArrayRequest.length() != 0)
        {
            list = view.findViewById(R.id.listView);
            ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.header_style_request_child, list, false);
            list.addHeaderView(header);

            adaptor = new ListViewRequestChild(getContext(), idDatabase, statusDatabase, parentsDatabase, titleDatabase, reasonDatabase, IP);
            list.setAdapter(adaptor);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("So far, you do not have any requests! You should create your first one by clicking on down button!").setCancelable(false).setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Important!");
            alert.show();
        }

        return view;
    }


    public void CreateRequest(){
        dialogCreateRequest = new Dialog(getContext());
        dialogCreateRequest.setContentView(R.layout.dialog_create_request);
        mTitle = dialogCreateRequest.findViewById(R.id.title);
        mReason = dialogCreateRequest.findViewById(R.id.reason);
        mCreateRequest = dialogCreateRequest.findViewById(R.id.CreateRequest);

        dialogCreateRequest.show();
        mTitle.setEnabled(true);
        mReason.setEnabled(true);
        mCreateRequest.setEnabled(true);

        radioParentsGroup = dialogCreateRequest.findViewById(R.id.radioParents);
        radioParent1Button = dialogCreateRequest.findViewById(R.id.radioParent1);
        radioParent2Button = dialogCreateRequest.findViewById(R.id.radioParent2);

        String sqlFamily = "SELECT parent_1,parent_2,child_1,child_2,child_3,child_4,child_5,child_6 FROM family WHERE child_1='" + codeChild + "' OR child_2='" + codeChild + "' OR child_3='" + codeChild + "' OR child_4='" + codeChild + "' OR child_5='" + codeChild + "' OR child_6='" + codeChild + "';";
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
            Parent1Database = objFamily.getString("parent_1");
            Parent2Database = objFamily.getString("parent_2");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String tableParent = "parent";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetParent = new BackgroundTaskSelectDB(view.getContext());
        String sqlParent = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE code_parent='" + Parent1Database + "' OR code_parent='" + Parent2Database + "';";
        parent = null;
        try {
            parent = backgroundTaskSelectDBGetParent.execute(IP, methodGetData, sqlParent, tableParent).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayParent = null;
        JSONObject objParent = null;
        try {
            jsonArrayParent = new JSONArray(parent);
            nameParentsDatabase = new String[jsonArrayParent.length()];
            emailParentsDatabase = new String[jsonArrayParent.length()];
            codeParentsDatabase = new String[jsonArrayParent.length()];
            for (int i=0; i<jsonArrayParent.length(); i++)
            {
                objParent = jsonArrayParent.getJSONObject(i);
                nameParentsDatabase[i] = objParent.getString("name");
                emailParentsDatabase[i] = objParent.getString("email");
                codeParentsDatabase[i] = objParent.getString("code_parent");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lenghtJsonParent = jsonArrayParent.length();

        if (jsonArrayParent.length() == 2)
        {
            if (!codeParentsDatabase[0].equals("null") && !codeParentsDatabase[1].equals("null"))
            {
                radioParent1Button.setVisibility(View.VISIBLE);
                radioParent1Button.setText(nameParentsDatabase[0]);
                radioParent2Button.setVisibility(View.VISIBLE);
                radioParent2Button.setText(nameParentsDatabase[1]);
                radioParent1Button.setChecked(true);
            }
        } else if (jsonArrayParent.length() == 1)
            {
                if (!codeParentsDatabase[0].equals("null"))
                {
                    radioParent1Button.setVisibility(View.VISIBLE);
                    radioParent1Button.setText(nameParentsDatabase[0]);
                    radioParent1Button.setChecked(true);
                }
            }

        mCreateRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTitle.setBackgroundResource(R.drawable.field_child);
                mReason.setBackgroundResource(R.drawable.field_child);
                errorCount = 0;
                String title = mTitle.getText().toString().trim();
                String reason = mReason.getText().toString().trim();

                if (isEmpty(reason)) {
                    mReason.setBackgroundResource(R.drawable.field_child_error);
                    errorCount++;
                }

                if (isEmpty(title)) {
                    mTitle.setBackgroundResource(R.drawable.field_child_error);
                    errorCount++;
                }

                if (errorCount == 0)
                {
                    int selectedId = radioParentsGroup.getCheckedRadioButtonId();
                    RadioButton radioParentsButton = dialogCreateRequest.findViewById(selectedId);
                    String code_parent = radioParentsButton.getText().toString();

                    if (lenghtJsonParent == 2)
                    {
                        if (code_parent.equals(nameParentsDatabase[0]))
                        {
                            selected_parent = codeParentsDatabase[0];
                        }  else selected_parent = codeParentsDatabase[1];
                    } else if (lenghtJsonParent == 1)
                        {
                            if (!codeParentsDatabase[0].equals("null"))
                            {
                                if (code_parent.equals(nameParentsDatabase[0]))
                                {
                                    selected_parent = codeParentsDatabase[0];
                                }
                            }
                        }

                    putToDatabase(title, reason, selected_parent);
                    Snackbar.make(view, "You have just created a request! Wait for response..", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    dialogCreateRequest.cancel();
                }
            }
        });
    }

    public void putToDatabase(String title, String reason, String code_parent){

        int length_code_request = 6;
        AdditionalMethods m = new AdditionalMethods();
        String code_request = m.GeneratePassword(length_code_request);
        String status_request = "P";
        String methodRegister = "Register";

        BackgroundTaskExecuteDB backgroundTaskExecuteDBRequest = new BackgroundTaskExecuteDB(getContext());
        String sqlRequest = "INSERT INTO request (id, title, reason, status_request, parent, child) VALUES ('" + code_request +
                "', '" + title + "', '" + reason + "', '" + status_request + "', '" + code_parent + "', '" + codeChild + "');";
        backgroundTaskExecuteDBRequest.execute(IP, methodRegister, sqlRequest);

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.detach(this).attach(this).commit();

    }

}
