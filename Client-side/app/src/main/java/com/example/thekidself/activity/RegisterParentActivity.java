package com.example.thekidself.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.activity.login.LoginChildActivity;
import com.example.thekidself.activity.login.LoginParentActivity;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.menu.ConditionsActivity;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static android.text.TextUtils.isEmpty;

public class RegisterParentActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView, mNameView;
    private Button mEmailSignUpButton;
    private TextView LinkForSignIn, LinkForgotPassword, LinkConditions, mDateView;
    private ImageView Eye;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner mGenderSpinner;
    private ArrayList<String> spinnerGenderList = new ArrayList<>();
    private String sex, code_user, result, code_encrypted = null, password_encrypted = null;
    int age;
    private String CheckPasswordResult,CheckEmailResult,CheckConnectionResult;
    private String statusInternet, methodCheckPassword;
    private String IP;

    @Override
    public void onBackPressed() {
        Intent intentBackToSignIn = new Intent(getApplicationContext(), LoginParentActivity.class);
        startActivity(intentBackToSignIn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register_parent);
        IP = getString(R.string.stringIP);

            mEmailView = findViewById(R.id.email);
            mPasswordView = findViewById(R.id.password);
            mNameView = findViewById(R.id.name);
            mDateView = findViewById(R.id.date);
            Button mEmailSignUpButton = findViewById(R.id.sign_up_button);
            Eye = findViewById(R.id.EyeParent);
            LinkForSignIn = findViewById(R.id.LinkForSignIn);
            LinkForgotPassword = findViewById(R.id.ForgotPassword);
            LinkConditions = findViewById(R.id.LinkConditions);
            mGenderSpinner = findViewById(R.id.sex);

            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterParentActivity.this);
            builder.setMessage("Are you over 18 years old?").setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentNo = new Intent(getApplicationContext(), LoginChildActivity.class);
                            startActivity(intentNo);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Important!");
            alert.show();

            spinnerGenderList.add("Female");
            spinnerGenderList.add("Male");
            HintSpinner<String> hintSpinner = new HintSpinner<>(
                    mGenderSpinner,
                    new HintAdapter<>(this, R.string.stringSex, spinnerGenderList),
                    new HintSpinner.Callback<String>() {
                        @Override
                        public void onItemSelected(int position, String itemAtPosition) {
                            sex = spinnerGenderList.get(position);
                        }

                        public void onNothingSelected(int position, String itemAtPosition) {

                        }
                    });
            hintSpinner.init();

            mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        attemptSignUp();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });

            mDateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            RegisterParentActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                }
            });

            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month +1;
                    String date = year + "-" + month + "-" + dayOfMonth;
                    mDateView.setText(date);
                }
            };

            AdditionalMethods method = new AdditionalMethods();
            method.HideUnhidePassword(Eye, mPasswordView);

            method.setLinks(LinkForSignIn, LoginParentActivity.class, Color.BLACK,0,24);
            method.setLinks(LinkConditions, ConditionsActivity.class, Color.BLACK,38,54);
    }


    public void attemptSignUp() throws ParseException, ExecutionException, InterruptedException {

        AdditionalMethods m = new AdditionalMethods();
        int errorCount = 0;
        mNameView.setBackgroundResource(R.drawable.field_parent);
        mDateView.setBackgroundResource(R.drawable.field_parent);
        mEmailView.setBackgroundResource(R.drawable.field_parent);
        mPasswordView.setBackgroundResource(R.drawable.field_parent);
        mGenderSpinner.setBackgroundResource(R.drawable.field_parent);

        String name = mNameView.getText().toString().trim();
        String db = mDateView.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if (isEmpty(name))
        {
            mNameView.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        }

        if (isEmpty(db))
        {
            mDateView.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        } else
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date birthDate = sdf.parse(db);
                age = m.calculateAge(birthDate);
                result = m.DBValidator(db);
                if (!result.equals("before"))
                {
                    mDateView.setBackgroundResource(R.drawable.field_parent_error);
                    Toast.makeText(getBaseContext(),"Date of Birth cannot be " + result + " current date!", Toast.LENGTH_LONG).show();
                    errorCount++;
                } else if (age < 18)
                {
                    mDateView.setBackgroundResource(R.drawable.field_parent_error);
                    Toast.makeText(getBaseContext(),"You cannot sigh up if you are under 18!", Toast.LENGTH_LONG).show();
                    errorCount++;
                }
            }

        String methodCheckEmail = "CheckEmail";
        BackgroundTaskSelectDB backgroundTaskSelectDBCheckEmail = new BackgroundTaskSelectDB(this);
        CheckEmailResult = backgroundTaskSelectDBCheckEmail.execute(IP, methodCheckEmail, email).get();

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
            else if (CheckEmailResult.equals("1"))
                {
                    mEmailView.setBackgroundResource(R.drawable.field_parent_error);
                    Toast.makeText(getBaseContext(),"This email is already occupied!", Toast.LENGTH_SHORT).show();
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

        if (isEmpty(sex))
        {
            mGenderSpinner.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        }


        if (errorCount == 0)
        {
            String sexChar;
            if (sex.equals("Female"))
                sexChar = "F";
            else sexChar = "M";

            String methodRegister = "Register";
            methodCheckPassword = "CheckPassword";
            int lengthPassword = 5;

            BackgroundTaskExecuteDB backgroundTaskExecuteDBUser = new BackgroundTaskExecuteDB(this);
            BackgroundTaskExecuteDB backgroundTaskExecuteDBParent = new BackgroundTaskExecuteDB(this);
            BackgroundTaskExecuteDB backgroundTaskExecuteDBFamily = new BackgroundTaskExecuteDB(this);
            BackgroundTaskSelectDB backgroundTaskSelectDBCheckPassword = new BackgroundTaskSelectDB(this);

            AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();

            try {
                code_user = m.GeneratePassword(lengthPassword);
                code_encrypted = methodEncryptionDecryption.Encryption(code_user, getString(R.string.StringKey));
                password_encrypted = methodEncryptionDecryption.Encryption(password, getString(R.string.StringKey));
                CheckPasswordResult = backgroundTaskSelectDBCheckPassword.execute(IP, methodCheckPassword, code_encrypted).get();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }

            finish();
            //TODO : repeat with AsyncTask
//          while (CheckPasswordResult.equals("1")) {
//                code_user = m.GeneratePassword(lengthPassword);
//                code_encrypted = methodEncryptionDecryption.Encryption(code_user, getString(R.string.StringKey));
//                CheckPasswordResult = backgroundTaskSelectDBCheckPassword.execute(IP, methodCheckPassword, code_encrypted).get();
//              System.out.println("second: " + CheckPasswordResult);
//            }

            //TODO: add to database th data about phone needed to connect with another phone

            if (CheckPasswordResult.equals("0"))
            {
                String sqlUser = "INSERT INTO all_users (code_user) VALUES ('" + code_encrypted +"');";
                backgroundTaskExecuteDBUser.execute(IP,methodRegister, sqlUser);


                String sqlParent = "INSERT INTO parent (code_parent, bd, email, name, password_parent, sex) VALUES ('" + code_encrypted +
                        "', '" + db + "', '" + email + "', '" + name + "', '" + password_encrypted + "', '" + sexChar + "');";
                backgroundTaskExecuteDBParent.execute(IP,methodRegister, sqlParent);

                String sqlFamily = "INSERT INTO family (parent_1) values('" + code_encrypted +"');";
                backgroundTaskExecuteDBFamily.execute(IP,methodRegister, sqlFamily);
                finish();

                Toast.makeText(getBaseContext(),R.string.SuccessSignUp, Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(),R.string.LogIn, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginParentActivity.class);
                startActivity(intent);
            } else System.out.println("The code already exist!Sign up again!");
        }

    }

}
