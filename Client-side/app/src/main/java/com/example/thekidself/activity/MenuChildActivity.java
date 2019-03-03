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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.activity.login.LoginChildActivity;
import com.example.thekidself.chat.helper.Constants;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.menu.FamilyChatFragment;
import com.example.thekidself.menu.HelpFragment;
import com.example.thekidself.menu.child.AccountChildFragment;
import com.example.thekidself.menu.child.EmergenceButtonChildFragment;
import com.example.thekidself.menu.child.RequestChildFragment;
import com.example.thekidself.menu.child.TaskChildFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class MenuChildActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragmentMain;
    SharedPreferences mSharedPreferences;
    String IP;
    String nameDatabase, genderDatabase;
    TextView mNameUser;
    ImageView mImageUser;

    String codeChild;

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
        setContentView(R.layout.activity_menu_child);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        IP = getString(R.string.stringIP);

        mSharedPreferences = getSharedPreferences("Child", MODE_PRIVATE);
        codeChild = mSharedPreferences.getString("codeChild", "");

        if (codeChild.equals("")) {
            Intent intent = new Intent(getApplicationContext(), ChooseModeActivity.class);
            startActivity(intent);
        }

        String sql = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + codeChild + "';";
        String table = "child";
        String methodGetNameGender = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetNameGenderChild = new BackgroundTaskSelectDB(this);
        String stream = null;
        try {
            stream = backgroundTaskSelectDBGetNameGenderChild.execute(IP, methodGetNameGender, sql, table).get();
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

        //Style of Emergence Button
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem emergence_button = menu.findItem(R.id.nav_emergence_button);
        SpannableString spanString = new SpannableString(emergence_button.getTitle());
        //spanString.setSpan(new TextAppearanceSpan(this, R.style.NavigationItemButton), 0, spanString.length(), 0);
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spanString.length(), 0);
        //spanString.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorAccent)), 0,     spanString.length(), 0);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spanString.length(), 0);
        emergence_button.setTitle(spanString);
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);

        mNameUser = headView.findViewById(R.id.nameChild);
        mImageUser = headView.findViewById(R.id.imageChild);
        String text = "Hello, " + nameDatabase;
        mNameUser.setText(text);
        if (genderDatabase.trim().equals("F")) {
            mImageUser.setImageResource(R.drawable.girl);
        } else {
            mImageUser.setImageResource(R.drawable.boy);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child, menu);
        return true;
    }


    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_sign_out) {
                mSharedPreferences = getSharedPreferences("Child", MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove("codeChild");
                editor.commit();

                    SharedPreferences mSharedPreferencesChat = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorChat = mSharedPreferencesChat.edit();
                    editorChat.clear();
                    editorChat.apply();

                SharedPreferences mSharedPreferencesFamily = getSharedPreferences("Family", MODE_PRIVATE);
                SharedPreferences.Editor editorFamily = mSharedPreferencesFamily.edit();
                editorFamily.remove("codeFamily");
                editorFamily.commit();

                Intent openSignInChild = new Intent(getApplicationContext(), LoginChildActivity.class);
                startActivity(openSignInChild);
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            fragmentMain = null;
            int id = item.getItemId();

            if (id == R.id.nav_help) {
                fragmentMain = new HelpFragment();
        } else if (id == R.id.nav_request) {
                fragmentMain = new RequestChildFragment();
        } else if (id == R.id.nav_family_chat) {
                fragmentMain = new FamilyChatFragment();
        } else if (id == R.id.nav_account) {
                fragmentMain = new AccountChildFragment();
        } else if (id == R.id.nav_task) {
                fragmentMain = new TaskChildFragment();
        } else if (id == R.id.nav_emergence_button) {
                fragmentMain = new EmergenceButtonChildFragment();
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
