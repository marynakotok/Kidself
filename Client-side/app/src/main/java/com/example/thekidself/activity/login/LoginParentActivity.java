package com.example.thekidself.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.activity.ChooseModeActivity;
import com.example.thekidself.activity.MenuParentActivity;
import com.example.thekidself.activity.RegisterParentActivity;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.activity.forgot_password.ForgotPasswordActivity;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.text.TextUtils.isEmpty;

public class LoginParentActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView;
    private Button mEmailSignInButton;
    CheckBox mKeepSigned;
    private ImageView Eye;
    int errorCount;
    private SharedPreferences mSharedPreferences, mSharedPreferencesFamily;
    Boolean KeepSigned = false;
    String email,password;
    String IP, codeDatabase, codeFamily, family;


    @Override
    public void onBackPressed() {
        Intent intentBackToChoose = new Intent(getApplicationContext(), ChooseModeActivity.class);
        startActivity(intentBackToChoose);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login_parent);
        IP = getString(R.string.stringIP);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        TextView LinkForRegister = findViewById(R.id.LinkForRegister);
        TextView LinkForgotPassword = findViewById(R.id.ForgotPassword);
        TextView LinkAnotherParent = findViewById(R.id.AnotherParent);
        Eye = findViewById(R.id.EyeParent);
        mKeepSigned = findViewById(R.id.keepMe);


        mSharedPreferences = getSharedPreferences("Parent",MODE_PRIVATE);
        String emailSharedPreferences = mSharedPreferences.getString("email","");
        String passwordSharedPreferences = mSharedPreferences.getString("password","");
        String codeParent = mSharedPreferences.getString("codeParent","");

        String methodCheckCodeUser = "CheckCodeUser";

        BackgroundTaskSelectDB backgroundTaskSelectDBCheckCodeUser = new BackgroundTaskSelectDB(this);

        String result = null;
        try {
            result = backgroundTaskSelectDBCheckCodeUser.execute(IP, methodCheckCodeUser, codeParent).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (result.equals("1"))
        {
            if((!emailSharedPreferences.equals("") && !passwordSharedPreferences.equals("")) || !codeParent.equals("")) {
                Intent intentToMenu = new Intent(getApplicationContext(), MenuParentActivity.class);
                startActivity(intentToMenu);
            }
        }

        mEmailSignInButton = findViewById(R.id.sign_in_button);

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        AdditionalMethods method = new AdditionalMethods();
        method.HideUnhidePassword(Eye, mPasswordView);

        method.setLinks(LinkForRegister, RegisterParentActivity.class, Color.BLACK,0,30);
        method.setLinks(LinkForgotPassword, ForgotPasswordActivity.class, Color.BLACK,0,16);
        method.setLinks(LinkAnotherParent, LoginAnotherParentActivity.class, Color.BLACK,0,49);
    }

    void attemptLogin()
    {

        mEmailView.setBackgroundResource(R.drawable.field_parent);
        mPasswordView.setBackgroundResource(R.drawable.field_parent);
        errorCount = 0;
        AdditionalMethods m = new AdditionalMethods();
        email = mEmailView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();

        if (isEmpty(email))
        {
            mEmailView.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        } else if (!m.EmailValidator(email))
            {
                mEmailView.setBackgroundResource(R.drawable.field_parent_error);
                Toast.makeText(getBaseContext(),"Email is incorrect!", Toast.LENGTH_SHORT).show();
                errorCount++;
            }

        if (isEmpty(password))
        {
            mPasswordView.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        } else if (!m.PasswordValidator(password))
            {
                mPasswordView.setBackgroundResource(R.drawable.field_parent_error);
                Toast.makeText(getBaseContext(),"Password should be greater than 6 characters!", Toast.LENGTH_SHORT).show();
                errorCount++;
            }

            if (errorCount == 0)
            {
                if (mKeepSigned.isChecked()) {
                    KeepSigned = true;
                    enter();
                } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginParentActivity.this);
                        builder.setMessage("Are you sure you do not want to keep you signed in?").setCancelable(false).setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        KeepSigned = false;
                                        dialog.cancel();
                                        enter();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mKeepSigned.setChecked(true);
                                        KeepSigned = true;
                                        dialog.cancel();
                                        enter();
                                    }
                                });
                    AlertDialog alertConn = builder.create();
                    alertConn.setTitle("Error!");
                    alertConn.show();

                    }
            }
    }

    private void enter()
    {
            try {
                String methodCheckEmail = "CheckEmail";
                BackgroundTaskSelectDB backgroundTaskSelectDBCheckEmailLogin = new BackgroundTaskSelectDB(this);
                String CheckEmailResult = backgroundTaskSelectDBCheckEmailLogin.execute(IP, methodCheckEmail, email).get();

                if (CheckEmailResult.equals("1"))
                {
                    String passwordDatabase;
                    String sql = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE email='" + email + "';";
                    String table = "parent";
                    String methodGetDataParentLogin = "Selection";

                    BackgroundTaskSelectDB backgroundTaskSelectDBGetPassword = new BackgroundTaskSelectDB(this);
                    String stream = backgroundTaskSelectDBGetPassword.execute(IP, methodGetDataParentLogin, sql, table).get();

                    JSONArray jsonArray = new JSONArray(stream);
                    JSONObject obj = jsonArray.getJSONObject(0);
                    passwordDatabase = obj.getString("password_parent");
                    codeDatabase = obj.getString("code_parent");

                    AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
                    String password_decrypted = methodEncryptionDecryption.Decryption(passwordDatabase, getString(R.string.StringKey));


                    if (password.equals(password_decrypted))
                    {
                        SharedPreferences mSharedPreferencesChild = getSharedPreferences("Child", MODE_PRIVATE);
                        String codeChild = mSharedPreferencesChild.getString("codeChild","");
                        if (codeChild.equals(""))
                        {
                            Intent openMenuParent = new Intent(getApplicationContext(), MenuParentActivity.class);
                            startActivity(openMenuParent);
                            if (KeepSigned)
                            {
                                mSharedPreferences = getSharedPreferences("Parent", MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("password", passwordDatabase);
                                editor.putString("codeParent", codeDatabase);
                                editor.commit();
                            }
                            getFamily();
                            Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                        } else Toast.makeText(getApplicationContext(), "You are logged in already on this device as a child!", Toast.LENGTH_LONG).show();
                    } else  Toast.makeText(getApplicationContext(), "Password is not correct! Try again!", Toast.LENGTH_LONG).show();

            } else Toast.makeText(getApplicationContext(), "User with this email does not exist! Try again!", Toast.LENGTH_LONG).show();

            } catch (Exception except) {
                String TAG = "Error: ";
                Log.e(TAG,except.getMessage());
            }
    }

    public void getFamily()
    {
        String methodGetData = "Selection";
        String sqlFamily = "SELECT parent_1,parent_2,child_1,child_2,child_3,child_4,child_5,child_6 FROM family WHERE parent_1='" + codeDatabase + "' OR parent_2='" + codeDatabase + "';";
        String tableFamily = "family";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetFamily = new BackgroundTaskSelectDB(this);
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
            codeFamily = objFamily.getString("parent_1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSharedPreferencesFamily = getSharedPreferences("Family", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferencesFamily.edit();
        editor.putString("codeFamily", codeFamily);
        editor.commit();
    }
}

