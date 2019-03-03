package com.example.thekidself.activity.forgot_password;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.activity.login.LoginParentActivity;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskSelectDB;

import java.util.concurrent.ExecutionException;

import static android.text.TextUtils.isEmpty;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mEmailView;
    String CheckEmailResult, IP;
    int errorCount;
    private boolean isResetInitiated = false;
    private ProgressBar progress;
    SharedPreferences mSharedPreferences;
    String email;

    @Override
    public void onBackPressed() {
        Intent intentBack = new Intent(getApplicationContext(), LoginParentActivity.class);
        startActivity(intentBack);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmailView = findViewById(R.id.email);
        Button mContinueButton = findViewById(R.id.continue_button);
        IP = getString(R.string.stringIP);
        progress = findViewById(R.id.progress);

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attempt();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void  attempt() throws ExecutionException, InterruptedException {
        String methodCheckEmail = "CheckEmail";
        errorCount =0;
        mEmailView.setBackgroundResource(R.drawable.field_parent);
        email = mEmailView.getText().toString().trim();
        AdditionalMethods m = new AdditionalMethods();
        BackgroundTaskSelectDB backgroundTaskSelectDBCheckEmail = new BackgroundTaskSelectDB(this);
        CheckEmailResult = backgroundTaskSelectDBCheckEmail.execute(IP, methodCheckEmail, email).get();

        if (isEmpty(email)) {
            mEmailView.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        } else if (!m.EmailValidator(email)) {
            mEmailView.setBackgroundResource(R.drawable.field_parent_error);
            Toast.makeText(getBaseContext(), "Email is incorrect!", Toast.LENGTH_SHORT).show();
            errorCount++;
        } else if (CheckEmailResult.equals("0")) {
            mEmailView.setBackgroundResource(R.drawable.field_parent_error);
            Toast.makeText(getBaseContext(), "This email does not exist!", Toast.LENGTH_SHORT).show();
            errorCount++;
        }

        if (errorCount == 0)
        {
            progress.setVisibility(View.VISIBLE);
            initiateResetPasswordProcess(email);
        }
    }

    private void initiateResetPasswordProcess(final String email){

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
//
//        User user = new User();
//        user.setEmail(email);
//        ServerRequest request = new ServerRequest();
//        request.setOperation(Constants.RESET_PASSWORD_INITIATE);
//        request.setUser(user);
//
//        Call<ServerResponse> response = requestInterface.operation(request);
//
//        response.enqueue(new Callback<ServerResponse>() {
//            @Override
//            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
//
//                System.out.println("User: " + response.message());
//                System.out.println("Operation: " + response.headers());
//
//                ServerResponse resp = response.body();
//                Toast.makeText(getBaseContext(), resp.getMessage(), Toast.LENGTH_LONG).show();
//
////                if(resp.getResult().equals(Constants.SUCCESS)){
////
////                    Toast.makeText(getBaseContext(), resp.getMessage(), Toast.LENGTH_LONG).show();
////                    isResetInitiated = true;
////                    mSharedPreferences = getSharedPreferences("ForgotPass", MODE_PRIVATE);
////                    SharedPreferences.Editor editor = mSharedPreferences.edit();
////                    editor.putString("email", email);
////                    editor.commit();
////                    Intent intentNext = new Intent(getApplicationContext(), ForgotPasswordGetCodeActivity.class);
////                    startActivity(intentNext);
////
////                } else {
////
////                    Toast.makeText(getBaseContext(), resp.getMessage(), Toast.LENGTH_LONG).show();
////                }
//                progress.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onFailure(Call<ServerResponse> call, Throwable t) {
//
//                progress.setVisibility(View.INVISIBLE);
//                Log.d(Constants.TAG,"failed");
//                Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//            }
//
//            });
    }

}
