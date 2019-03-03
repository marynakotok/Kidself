package com.example.thekidself.activity.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.activity.ChooseModeActivity;
import com.example.thekidself.activity.MenuChildActivity;
import com.example.thekidself.activity.RegisterParentActivity;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginChildActivity extends AppCompatActivity{

    private Button ConnectButton;
    private TextView LinkForRegister;
    private ImageView Eye;
    private EditText mCodeView;
    private Boolean setTypePassword;
    private SharedPreferences mPreferences;
    private String codeString, IDusernameString;
    private SharedPreferences mSharedPreferences, mSharedPreferencesFamily;
    private String IP, code_encrypted, codeFamily, family;

    @Override
    public void onBackPressed() {
        Intent intentBackToChoose = new Intent(getApplicationContext(), ChooseModeActivity.class);
        startActivity(intentBackToChoose);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login_child);
        IP = getString(R.string.stringIP);

        mSharedPreferences = getSharedPreferences("Child",MODE_PRIVATE);
        String codeChild = mSharedPreferences.getString("codeChild","");

        String methodCheckCodeUser = "CheckCodeUser";

        BackgroundTaskSelectDB backgroundTaskSelectDBCheckCodeUser = new BackgroundTaskSelectDB(this);

        String result = null;
        try {
            result = backgroundTaskSelectDBCheckCodeUser.execute(IP, methodCheckCodeUser, codeChild).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (result.equals("1"))
        {
            if(!codeChild.equals("")) {
                Intent intent = new Intent(getApplicationContext(), MenuChildActivity.class);
                startActivity(intent);
            }
        }

        ConnectButton = findViewById(R.id.ConnectButton);
        LinkForRegister = findViewById(R.id.LinkForRegister);

            ConnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        login();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            Eye = findViewById(R.id.EyeChild);
            mCodeView = findViewById(R.id.Code);
            AdditionalMethods method = new AdditionalMethods();
            method.HideUnhidePassword(Eye, mCodeView);

            method.setLinks(LinkForRegister, RegisterParentActivity.class, Color.BLACK,0,30);
        }

    void login() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ExecutionException, InterruptedException {
        AdditionalMethods m = new AdditionalMethods();
        String codeString = mCodeView.getText().toString().trim();
        String methodCheckCodeParent = "CheckCodeChild";

        AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
        code_encrypted = methodEncryptionDecryption.Encryption(codeString, getString(R.string.StringKey));

        BackgroundTaskSelectDB backgroundTaskSelectDBCheckCodeParent = new BackgroundTaskSelectDB(this);

        String result = backgroundTaskSelectDBCheckCodeParent.execute(IP, methodCheckCodeParent, code_encrypted).get();

        if (result.equals("1"))
        {
            SharedPreferences mSharedPreferencesParent = getSharedPreferences("Parent", MODE_PRIVATE);
            String codeParent = mSharedPreferencesParent.getString("codeParent","");
            if (codeParent.equals("")) {
                mSharedPreferences = getSharedPreferences("Child", MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("codeChild", code_encrypted);
                editor.commit();
                getFamily();
                Intent openMenuChild = new Intent(getApplicationContext(), MenuChildActivity.class);
                startActivity(openMenuChild);
                Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(getApplicationContext(), "You are logged in already on this device as a parent!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(getApplicationContext(), "This user does not exist!", Toast.LENGTH_SHORT).show();

    }

    public void getFamily()
    {
        String methodGetData = "Selection";
        String sqlFamily = "SELECT parent_1,parent_2,child_1,child_2,child_3,child_4,child_5,child_6 FROM family WHERE child_1='" + code_encrypted + "' OR child_2='" + code_encrypted + "' OR child_3='" + code_encrypted + "' OR child_4='" + code_encrypted + "' OR child_5='" + code_encrypted + "' OR child_6='" + code_encrypted + "';";
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




