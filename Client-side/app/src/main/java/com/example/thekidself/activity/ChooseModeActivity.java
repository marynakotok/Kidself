package com.example.thekidself.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.thekidself.R;
import com.example.thekidself.activity.login.LoginChildActivity;
import com.example.thekidself.activity.login.LoginParentActivity;

public class ChooseModeActivity extends AppCompatActivity {


    private Button ChildButton;
    private Button ParentButton;
    ProgressBar progress;

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
        setContentView(R.layout.activity_choose_mode);

        ChildButton = findViewById(R.id.buttonChild);
        ParentButton = findViewById(R.id.buttonParent);
        progress = findViewById(R.id.progress);

        ChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                Intent openLoginChild = new Intent(getApplicationContext(), LoginChildActivity.class);
                startActivity(openLoginChild);

            }
        });
        ParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                Intent openLoginParent = new Intent(getApplicationContext(), LoginParentActivity.class);
                startActivity(openLoginParent);
            }
        });
    }
}
