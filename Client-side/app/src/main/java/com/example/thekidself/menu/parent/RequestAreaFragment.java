package com.example.thekidself.menu.parent;

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
import com.example.thekidself.adapter.ListViewRequestParent;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class RequestAreaFragment extends Fragment {


    View view;
    String methodGetData, IP, codeParent, request;
    SharedPreferences mSharedPreferences;
    ListView list;
    ListViewRequestParent adaptor;
    String [] idDatabase, childDatabase, titleDatabase, reasonDatabase, statusDatabase;
    public RequestAreaFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_area, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringRequestArea));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSharedPreferences = view.getContext().getSharedPreferences("Parent", MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");
        IP = getString(R.string.stringIP);
        methodGetData = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetRequest = new BackgroundTaskSelectDB(view.getContext());
        String sqlRequest = "SELECT id, title, reason, status_request, parent, child, created_on FROM request WHERE parent='" + codeParent + "';";
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
            childDatabase = new String[jsonArrayRequest.length()];
            statusDatabase = new String[jsonArrayRequest.length()];
            titleDatabase = new String[jsonArrayRequest.length()];
            reasonDatabase = new String[jsonArrayRequest.length()];
            for (int i=0; i<jsonArrayRequest.length(); i++)
            {
                objRequest = jsonArrayRequest.getJSONObject(i);
                idDatabase[i] = objRequest.getString("id");
                childDatabase[i] = objRequest.getString("child");
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
            ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.header_style_request_parent, list, false);
            list.addHeaderView(header);

            adaptor = new ListViewRequestParent(getContext(), idDatabase, statusDatabase, childDatabase, titleDatabase, reasonDatabase, IP);
            list.setAdapter(adaptor);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("So far, you do not have any requests!").setCancelable(false).setPositiveButton("Ok",
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
