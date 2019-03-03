package com.example.thekidself.menu;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.thekidself.R;
import com.example.thekidself.chat.app.AppController;
import com.example.thekidself.chat.gcm.GCMRegistrationIntentService;
import com.example.thekidself.chat.helper.Constants;
import com.example.thekidself.chat.helper.Message;
import com.example.thekidself.chat.helper.ThreadAdapter;
import com.example.thekidself.chat.helper.URLs;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class FamilyChatFragment extends Fragment implements View.OnClickListener{

    View view;
    SharedPreferences mSharedPreferencesParent, mSharedPreferencesChild;
    String codeParent, codeChild;
    String codeFamily, code_user, username, IP;
    //Broadcast receiver to receive broadcasts
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //Progress dialog
    private ProgressDialog dialog;

    //Recyclerview objects
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<ThreadAdapter.ViewHolder> adapter;

    //ArrayList of messages to store the thread messages
    private ArrayList<Message> messages;

    private Button buttonSend;
    private EditText editTextMessage;

    public FamilyChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_family_chat, container, false);
        view.setTag("RecyclerViewFragment");
        IP = getString(R.string.stringIP);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringFamilyChat));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Opening chat room...");
        dialog.show();

        mSharedPreferencesParent = view.getContext().getSharedPreferences("Parent", MODE_PRIVATE);
        codeParent = mSharedPreferencesParent.getString("codeParent","");

        mSharedPreferencesChild = view.getContext().getSharedPreferences("Child", MODE_PRIVATE);
        codeChild = mSharedPreferencesChild.getString("codeChild","");

        mSharedPreferencesParent = view.getContext().getSharedPreferences("Family", MODE_PRIVATE);
        codeFamily = mSharedPreferencesParent.getString("codeFamily","");

        if (codeChild.equals(""))
        {
            code_user = codeParent;
            username = getParentName(codeParent);

        } else {
            code_user = codeChild;
            username = getChildName(codeChild);
        }

        AppController.getInstance().loginUser(code_user, username, codeFamily);

        //Displaying dialog while the chat room is being ready

        //Initializing recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThreadAdapter(getContext(), messages, AppController.getInstance().getCodeUser());

        //Initializing message arraylist
        messages = new ArrayList<>();

        adapter = new ThreadAdapter(getContext(), messages, AppController.getInstance().getCodeUser());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        scrollToBottom();

        //Calling function to fetch the existing messages on the thread
        fetchMessages();

        dialog.dismiss();

        //initializing button and edittext
        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        editTextMessage = (EditText) view.findViewById(R.id.editTextMessage);

        //Adding listener to button
        buttonSend.setOnClickListener(this);

        //Creating broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {

                    //When gcm registration is success do something here if you need

                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT)) {

                    //When the registration token is sent to ther server displaying a toast

                    //When we received a notification when the app is in foreground
                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    //Getting message data
                    String name = intent.getStringExtra("name");
                    String message = intent.getStringExtra("message");
                    String code_user = intent.getStringExtra("code_user");
                    String code_family = intent.getStringExtra("code_family");

                    //processing the message to add it in current thread
                    processMessage(name, message, code_user, code_family);
                }
            }
        };

        //if the google play service is not in the device app won't work
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());

        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode,getContext());

            } else {
                Toast.makeText(getContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(getContext(), GCMRegistrationIntentService.class);
            getActivity().startService(intent);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSend)
            sendMessage();
    }

    public String getParentName(String code)
    {
        String name="";
        String parent;
        String methodGetData = "Selection";
        String tableParent = "parent";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetParent = new BackgroundTaskSelectDB(view.getContext());
        String sqlParent = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE code_parent='" + code + "';";

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
            objParent = jsonArrayParent.getJSONObject(0);
            name = objParent.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return name;
    }

    public String getChildName(String code)
    {
        String name="";
        String child;
        String methodGetData = "Selection";
        String sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + code + "';";
        String tableChild = "child";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetChild = new BackgroundTaskSelectDB(view.getContext());

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
                objChild = jsonArrayChild.getJSONObject(0);
                name = objChild.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return name;
    }

    //This method will fetch all the messages of the thread
    private void fetchMessages() {
        final String code_family = AppController.getInstance().getCodeFamily();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_FETCH_MESSAGES + code_family,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray thread = res.getJSONArray("messages");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                String code_user = obj.getString("code_user");
                                String message = obj.getString("message");
                                String name = obj.getString("name");
                                String created_on = obj.getString("created_on");
                                String code_family = obj.getString("code_family");
                                Message messageObject = new Message(code_user, message, created_on, name, code_family);
                                messages.add(messageObject);
                            }

//                            adapter = new ThreadAdapter(getContext(), messages, AppController.getInstance().getCodeUser());
//                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            scrollToBottom();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    //Processing message to add on the thread
    private void processMessage(String name, String message, String code_user, String code_family) {
        Message m = new Message(code_user, message, getTimeStamp(), name, code_family);
        messages.add(m);
        scrollToBottom();
    }

    //This method will send the new message to the thread
    private void sendMessage() {
        final String message = editTextMessage.getText().toString().trim();
        if (message.equalsIgnoreCase(""))
            return;
        String codeUser = AppController.getInstance().getCodeUser();
        String name = AppController.getInstance().getUserName();
        String created_on = getTimeStamp();
        String code_family = AppController.getInstance().getUserName();

        Message m = new Message(codeUser, message, created_on, name, code_family);
        messages.add(m);
        adapter.notifyDataSetChanged();

        scrollToBottom();

        editTextMessage.setText("");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("code_user", AppController.getInstance().getCodeUser());
                params.put("message", message);
                params.put("name", AppController.getInstance().getUserName());
                params.put("code_family", AppController.getInstance().getCodeFamily());
                return params;
            }
        };

        //Disabling retry to prevent duplicate messages
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    //This method will return current timestamp
    public static String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    //Registering broadcast receivers
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));
    }


    //Unregistering receivers
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
