package com.example.thekidself.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.activity.login.LoginParentActivity;
import com.example.thekidself.chat.helper.Constants;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.R;
import com.example.thekidself.menu.FamilyChatFragment;
import com.example.thekidself.menu.HelpFragment;
import com.example.thekidself.menu.parent.AccountAreaFragment;
import com.example.thekidself.menu.parent.FamilyAreaFragment;
import com.example.thekidself.menu.parent.MapAreaFragment;
import com.example.thekidself.menu.parent.RequestAreaFragment;
import com.example.thekidself.menu.parent.TaskAreaFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MenuParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragmentMain;
    SharedPreferences mSharedPreferences, mSharedPreferencesFamily;
    public String codeParent;
    private ImageView mImageUser, mImageParent1;
    private TextView mNameUser, mNameParent1;
    private String IP;
    String genderDatabase;
    String nameDatabase;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_menu_parent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        IP = getString(R.string.stringIP);

        mSharedPreferences = getSharedPreferences("Parent",MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");


        String sql = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE code_parent='" + codeParent + "';";
        String table = "parent";
        String methodGetNameGender = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetNameGender = new BackgroundTaskSelectDB(this);
        String stream = null;
        try {
            stream = backgroundTaskSelectDBGetNameGender.execute(IP, methodGetNameGender, sql, table).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(stream);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject obj = null;
        try {
            obj = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            nameDatabase = obj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            genderDatabase = obj.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headView  = navigationView.getHeaderView(0);
        mNameUser =  headView.findViewById(R.id.nameUser);
        mImageUser = headView.findViewById(R.id.imageUser);
        String text = "Hello, "+ nameDatabase;
        mNameUser.setText(text);
        if (genderDatabase.trim().equals("F"))
        {
            mImageUser.setImageResource(R.drawable.woman);
        } else {
            mImageUser.setImageResource(R.drawable.man);
        }

//        Default fragment
//        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.fragment_area, new HelpFragment());
//        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            mSharedPreferences = getSharedPreferences("Parent", MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove("codeParent");
            editor.remove("email");
            editor.remove("password");
            editor.commit();

            SharedPreferences mSharedPreferencesChat = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorChat = mSharedPreferencesChat.edit();
            editorChat.clear();
            editorChat.apply();

            mSharedPreferencesFamily = getSharedPreferences("Family", MODE_PRIVATE);
            SharedPreferences.Editor editorFamily = mSharedPreferences.edit();
            editorFamily.remove("parent_1");
            editorFamily.remove("parent_2");
            editorFamily.remove("child_1");
            editorFamily.remove("child_2");
            editorFamily.remove("child_3");
            editorFamily.remove("child_4");
            editorFamily.remove("child_5");
            editorFamily.remove("child_6");
            editorFamily.remove("codeFamily");
            editorFamily.commit();
            Intent openSignInParent = new Intent(getApplicationContext(), LoginParentActivity.class);
            startActivity(openSignInParent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragmentMain = null;
        int id = item.getItemId();

        if (id == R.id.nav_family_area) {
            setTitle("My Family Area");
            fragmentMain = new FamilyAreaFragment();
        } else if (id == R.id.nav_map_area) {
            setTitle("My Map Area");
            fragmentMain = new MapAreaFragment();
        } else if (id == R.id.nav_task_area) {
            setTitle("My Task Area");
            fragmentMain = new TaskAreaFragment();
        } else if (id == R.id.nav_request_area) {
            setTitle("My Request Area");
            fragmentMain = new RequestAreaFragment();
        } else if (id == R.id.nav_family_chat) {
            setTitle("My Family Area");
            fragmentMain = new FamilyChatFragment();
        } else if (id == R.id.nav_account) {
            setTitle("My Account Area");
            fragmentMain = new AccountAreaFragment();
        } else if (id == R.id.nav_help) {
            setTitle("My Help Area");
            fragmentMain = new HelpFragment();
        }

        if (fragmentMain != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_area, fragmentMain);
            ft.commit();
        }
        else { Toast.makeText(getBaseContext(), "No fragment has been chosen!" , Toast.LENGTH_LONG).show();}

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
