package com.example.thekidself.menu.parent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.R;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.Context.MODE_PRIVATE;


public class AccountAreaFragment extends Fragment {


    View view;
    SharedPreferences mSharedPreferences;
    TextView mDeviceInfo, mCodeInfo;
    String codeParent, code_decrypted;
    public AccountAreaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_area, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringAccount));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mDeviceInfo = view.findViewById(R.id.deviceInfo);
        mCodeInfo = view.findViewById(R.id.codeGeneralInfo);

        mSharedPreferences = getActivity().getSharedPreferences("Parent",MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");

//        String sqlFamily = "SELECT parent_1,parent_2, child_1, child_2, child_3, child_4, child_5, child_6 FROM family WHERE child_1='" +
//                codeChild + "' OR child_2='" + codeChild + "' OR child_3='" + codeChild + "' OR child_4='" +
//                codeChild + "' OR child_5='" + codeChild + "' OR child_6='" + codeChild + "';";
//        String methodGetData = "Selection";
//        String tableFamily = "family";
//        BackgroundTaskSelectDB backgroundTaskSelectDBGetParents = new BackgroundTaskSelectDB(view.getContext());
//
//        family = null;
//        JSONArray jsonArrayFamily = null;
//        JSONObject objFamily = null;
//        try {
//            family = backgroundTaskSelectDBGetParents.execute(IP, methodGetData, sqlFamily, tableFamily).get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            jsonArrayFamily = new JSONArray(family);
//            objFamily = jsonArrayFamily.getJSONObject(0);
//            codeParent1 = objFamily.getString("parent_1");
//            codeParent2 = objFamily.getString("parent_2");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
        try {
            code_decrypted = methodEncryptionDecryption.Decryption(codeParent, getString(R.string.StringKey));
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        mCodeInfo.setText(code_decrypted);

        String device = Build.MODEL;
        mDeviceInfo.setText(device);

        mDeviceInfo.setText(device);

        return view;
    }

}
