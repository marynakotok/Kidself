package com.example.thekidself.activity.forgot_password;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.activity.forgot_password.interfaces.RequestInterface;
import com.example.thekidself.activity.forgot_password.models.ServerRequest;
import com.example.thekidself.activity.forgot_password.models.ServerResponse;
import com.example.thekidself.activity.forgot_password.models.User;
import com.example.thekidself.activity.login.LoginParentActivity;
import com.example.thekidself.app.Constants;
import com.example.thekidself.helper.AdditionalMethods;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.TextUtils.isEmpty;

public class ForgotPasswordGetCodeActivity extends AppCompatActivity {

    private EditText mPasswordView, mCodeView;
    String IP;
    ImageView EyePass,EyeCode;
    int errorCount;
    private CountDownTimer countDownTimer;
    private ProgressBar progress;
    private TextView tv_timer;
    SharedPreferences mSharedPreferences;
    private boolean isResetInitiated = true;

    @Override
    public void onBackPressed() {
        Intent intentBack = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intentBack);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_get_code);

        EyePass = findViewById(R.id.EyeParent2);
        EyeCode = findViewById(R.id.EyeParent1);
        tv_timer = findViewById(R.id.timer);
        mPasswordView = findViewById(R.id.password);
        mCodeView = findViewById(R.id.received_code);
        Button mChangeButton = findViewById(R.id.change_button);
        IP = getString(R.string.stringIP);
        progress = findViewById(R.id.progress);
        startCountdownTimer();

        AdditionalMethods method = new AdditionalMethods();
        method.HideUnhidePassword(EyePass, mPasswordView);
        method.HideUnhidePassword(EyeCode, mCodeView);

        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attempt();
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
                }
            }
        });

    }

    public void attempt() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String password = mPasswordView.getText().toString().trim();
        String code = mCodeView.getText().toString().trim();
        errorCount =0;
        mPasswordView.setBackgroundResource(R.drawable.field_parent);
        mCodeView.setBackgroundResource(R.drawable.field_parent);
        AdditionalMethods m = new AdditionalMethods();

        if (isEmpty(code))
        {
            mCodeView.setBackgroundResource(R.drawable.field_parent_error);
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
            progress.setVisibility(View.VISIBLE);
            mSharedPreferences = getSharedPreferences("ForgotPass",MODE_PRIVATE);
            String emailSharedPreferences = mSharedPreferences.getString("email","");
            AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
            String password_enc = methodEncryptionDecryption.Encryption(password, getString(R.string.StringKey));
            //finishResetPasswordProcess(emailSharedPreferences,code,password_enc);
        }
    }

    private void startCountdownTimer(){
        countDownTimer = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_timer.setText("Time remaining : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(getBaseContext(), "Time Out ! Request again to reset password.", Toast.LENGTH_LONG).show();
                Intent intentNext = new Intent(getApplicationContext(), LoginParentActivity.class);
                startActivity(intentNext);
            }
        }.start();
    }

    private void finishResetPasswordProcess(String email, String code, String password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_FINISH);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Toast.makeText(getBaseContext(), resp.getMessage(), Toast.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){

                    Toast.makeText(getBaseContext(), resp.getMessage(), Toast.LENGTH_LONG).show();
                    countDownTimer.cancel();
                    isResetInitiated = false;
                    Intent intentNext = new Intent(getApplicationContext(), LoginParentActivity.class);
                    startActivity(intentNext);

                } else {

                    Toast.makeText(getBaseContext(), resp.getMessage(), Toast.LENGTH_LONG).show();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


}
