package com.example.thekidself.chat.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.thekidself.chat.helper.Constants;

public class AppController extends Application {

    //Getting tag it will be used for displaying log and it is optional
    public static final String TAG = AppController.class.getSimpleName();

    //Creating a volley request queue object
    private RequestQueue mRequestQueue;

    //Creatting class object
    private static AppController mInstance;

    //Creating sharedpreferences object
    //We will store the user data in sharedpreferences
    private SharedPreferences sharedPreferences;

    //class instance will be initialized on app launch
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    //Public static method to get the instance of this class
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    //This method would return the request queue
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


    //This method would add the requeust to the queue for execution
    public <T> void addToRequestQueue(Request<T> req) {
        //Setting a tag to the request
        req.setTag(TAG);

        //calling the method to get the request queue and adding the requeust the the queuue
        getRequestQueue().add(req);
    }

    //method to cancle the pending requests
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    //Chat Activity

    //Method to get sharedpreferences
    public SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    //This method will store the user data on sharedPreferences
    //It will be called on login
    public void loginUser(String code_user, String username, String code_family) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constants.USER_ID, code_user);
        editor.putString(Constants.USER_NAME, username);
        editor.putString(Constants.FAMILY_ID, code_family);
        editor.apply();
    }

    public String getCodeUser() {
        return getSharedPreferences().getString(Constants.USER_ID, null);
    }

    public String getCodeFamily() {
        return getSharedPreferences().getString(Constants.FAMILY_ID, null);
    }

    public String getUserName() {
        return getSharedPreferences().getString(Constants.USER_NAME, null);
    }
}
