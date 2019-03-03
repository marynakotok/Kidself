package com.example.thekidself.menu.parent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskExecuteDB;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.menu.parent.FamilyAreaFragment;

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

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;

public class CreateChildFragment extends Fragment {

    View view;
    private Spinner mGenderSpinner;
    private ArrayList<String> spinnerGenderList = new ArrayList<>();
    private String sex, IP;
    private TextView mDateView, getmDateView;
    private EditText mNameView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    int age;
    private SharedPreferences mSharedPreferences;
    String sexChar;
    String result;
    String placeInFamily;
    String code_user, code_encrypted, CheckPasswordResult;

    public CreateChildFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_create_child, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringCreateChild));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        IP = getString(R.string.stringIP);
        mGenderSpinner = view.findViewById(R.id.sexChild);
        mNameView = view.findViewById(R.id.nameChild);
        mDateView = view.findViewById(R.id.dateChild);
        Button mSaveButton = view.findViewById(R.id.SaveButtonChild);
        Button mCancelButton = view.findViewById(R.id.CancelButtonChild);

        spinnerGenderList.add("Girl");
        spinnerGenderList.add("Boy");
        HintSpinner<String> hintSpinner = new HintSpinner<>(
                mGenderSpinner,
                new HintAdapter<>(getActivity(), R.string.stringSex, spinnerGenderList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        sex = spinnerGenderList.get(position);
                    }
                });
        hintSpinner.init();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFamilyArea();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to create a child?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    attemptCreateChild();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (InvalidAlgorithmParameterException e) {
                                    e.printStackTrace();
                                } catch (NoSuchPaddingException e) {
                                    e.printStackTrace();
                                } catch (BadPaddingException e) {
                                    e.printStackTrace();
                                } catch (IllegalBlockSizeException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Important!");
                alert.show();
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
                        view.getContext(),
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


        return view;
    }

    public void goToFamilyArea()
    {
        FamilyAreaFragment fragment;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment = new FamilyAreaFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_area, fragment).addToBackStack(null);
        ft.commit();
    }

    public  void  attemptCreateChild() throws ParseException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ExecutionException, InterruptedException {

        AdditionalMethods m = new AdditionalMethods();
        int errorCount = 0;
        mNameView.setBackgroundResource(R.drawable.field_parent);
        mDateView.setBackgroundResource(R.drawable.field_parent);
        mGenderSpinner.setBackgroundResource(R.drawable.field_parent);

        String name = mNameView.getText().toString().trim();
        String db = mDateView.getText().toString().trim();

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
                Toast.makeText(view.getContext(),"Date of Birth cannot be " + result + " current date!", Toast.LENGTH_LONG).show();
                errorCount++;
            } else if (age > 18)
            {
                mDateView.setBackgroundResource(R.drawable.field_parent_error);
                Toast.makeText(view.getContext(),"You cannot create a child who is over 18!", Toast.LENGTH_LONG).show();
                errorCount++;
            }
        }

        if (isEmpty(sex))
        {
            mGenderSpinner.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        }


        if (errorCount == 0)
        {
            if (sex.equals("Girl"))
                sexChar = "F";
            else sexChar = "M";
            int lengthPassword = 5;
            AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
            String methodCheckPassword = "CheckPassword";
            String methodRegister = "Register";

            BackgroundTaskExecuteDB backgroundTaskExecuteDBUser = new BackgroundTaskExecuteDB(view.getContext());
            BackgroundTaskExecuteDB backgroundTaskExecuteDBChild = new BackgroundTaskExecuteDB(view.getContext());
            BackgroundTaskExecuteDB backgroundTaskExecuteDBFamily = new BackgroundTaskExecuteDB(view.getContext());
            BackgroundTaskSelectDB backgroundTaskSelectDBCheckPassword = new BackgroundTaskSelectDB(view.getContext());

            code_user = m.GeneratePassword(lengthPassword);
            code_encrypted = methodEncryptionDecryption.Encryption(code_user, getString(R.string.StringKey));
            CheckPasswordResult = backgroundTaskSelectDBCheckPassword.execute(IP, methodCheckPassword, code_encrypted).get();

            if (CheckPasswordResult.equals("0"))
            {

                mSharedPreferences = getActivity().getSharedPreferences("Family", MODE_PRIVATE);
                String Parent1SharedPreferences = mSharedPreferences.getString("parent_1","");
                String Child1SharedPreferences = mSharedPreferences.getString("child_1","");
                String Child2SharedPreferences = mSharedPreferences.getString("child_2","");
                String Child3SharedPreferences = mSharedPreferences.getString("child_3","");
                String Child4SharedPreferences = mSharedPreferences.getString("child_4","");
                String Child5SharedPreferences = mSharedPreferences.getString("child_5","");
                String Child6SharedPreferences = mSharedPreferences.getString("child_6","");

                    String sqlUser = "INSERT INTO all_users (code_user) VALUES ('" + code_encrypted +"');";
                    backgroundTaskExecuteDBUser.execute(IP, methodRegister, sqlUser);

                    String sqlChild = "INSERT INTO child (code_child, name, sex, bd) VALUES ('" + code_encrypted +
                            "', '" + name + "', '" + sexChar + "', '" + db + "');";
                    System.out.println("sql: " + sqlChild);
                    backgroundTaskExecuteDBChild.execute(IP, methodRegister, sqlChild);


                    if (Child1SharedPreferences.equals("null")) placeInFamily = "child_1";
                    else if (Child2SharedPreferences.equals("null")) placeInFamily = "child_2";
                    else if (Child3SharedPreferences.equals("null")) placeInFamily = "child_3";
                    else if (Child4SharedPreferences.equals("null")) placeInFamily = "child_4";
                    else if (Child5SharedPreferences.equals("null")) placeInFamily = "child_5";
                    else if (Child6SharedPreferences.equals("null")) placeInFamily = "child_6";

                    String sqlFamily = "UPDATE family SET " + placeInFamily + "='" + code_encrypted + "' WHERE parent_1='" + Parent1SharedPreferences +"';";
                    backgroundTaskExecuteDBFamily.execute(IP, methodRegister, sqlFamily);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("After installing the application on the kid's phone, go to the Child mode and provide this code: " + code_user).setCancelable(false)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToFamilyArea();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("Way to add the kid! Remember this code!");
                    alert.show();

            } else System.out.println("The code already exist! Create a child again!");
        }
    }

}
