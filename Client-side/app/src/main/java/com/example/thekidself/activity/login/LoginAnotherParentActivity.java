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

import com.example.thekidself.R;
import com.example.thekidself.activity.ChooseModeActivity;
import com.example.thekidself.activity.MenuParentActivity;
import com.example.thekidself.activity.RegisterParentActivity;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskSelectDB;

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

public class LoginAnotherParentActivity extends AppCompatActivity {

    private Button ConnectButton;
    private TextView LinkForRegister;
    private ImageView Eye;
    private EditText mCodeView;
    private SharedPreferences mSharedPreferences, mSharedPreferencesFamily;
    private String codeString, IDusernameString;
    private String IP;
    String code_encrypted, codeFamily, family;

    @Override
    public void onBackPressed() {
        Intent intentBackToChoose = new Intent(getApplicationContext(), ChooseModeActivity.class);
        startActivity(intentBackToChoose);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login_another_parent);
        IP = getString(R.string.stringIP);

        ConnectButton = findViewById(R.id.ConnectButton);
        LinkForRegister = findViewById(R.id.LinkForRegister);
        Eye = findViewById(R.id.EyeAnotherParent);
        mCodeView = findViewById(R.id.Code);

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

        AdditionalMethods method = new AdditionalMethods();
        method.HideUnhidePassword(Eye, mCodeView);

        method.setLinks(LinkForRegister, RegisterParentActivity.class, Color.BLACK,0,30);
    }

    void login() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ExecutionException, InterruptedException {
            AdditionalMethods m = new AdditionalMethods();
            String codeString = mCodeView.getText().toString().trim();
            String methodCheckCodeParent = "CheckCodeParent";

            AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
            code_encrypted = methodEncryptionDecryption.Encryption(codeString, getString(R.string.StringKey));

            BackgroundTaskSelectDB backgroundTaskSelectDBCheckCodeParent = new BackgroundTaskSelectDB(this);

            String result = backgroundTaskSelectDBCheckCodeParent.execute(IP, methodCheckCodeParent, code_encrypted).get();

        if (result.equals("1"))
            {
                SharedPreferences mSharedPreferencesChild = getSharedPreferences("Child", MODE_PRIVATE);
                String codeChild = mSharedPreferencesChild.getString("codeChild","");
                if (codeChild.equals(""))
                {
                    mSharedPreferences = getSharedPreferences("Parent", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("codeParent", code_encrypted);
                    editor.putString("password", "");
                    editor.putString("email", "");
                    editor.commit();
                    getFamily();
                    Intent openMenuChild = new Intent(getApplicationContext(), MenuParentActivity.class);
                    startActivity(openMenuChild);
                    Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "You are logged in already on this device as a child!", Toast.LENGTH_LONG).show();
            } else Toast.makeText(getApplicationContext(), "This user does not exist!", Toast.LENGTH_SHORT).show();

    }

    public void getFamily()
    {
        String methodGetData = "Selection";
        String sqlFamily = "SELECT parent_1,parent_2,child_1,child_2,child_3,child_4,child_5,child_6 FROM family WHERE parent_1='" + code_encrypted + "' OR parent_2='" + code_encrypted + "';";
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
