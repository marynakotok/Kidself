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

public class CreateAnotherParentFragment extends Fragment {

    private final String  password_encrypted = "NoPassword";
    View view;
    private Spinner mGenderSpinner;
    private ArrayList<String> spinnerGenderList = new ArrayList<>();
    private String sex, IP;
    private TextView mDateView, getmDateView;
    private EditText mNameView, mEmailView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    int age;
    private SharedPreferences mSharedPreferences;
    String sexChar;
    String result;
    String placeInFamily;
    String code_user, code_encrypted, CheckPasswordResult, CheckEmailResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_another_parent, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringCreateSecondParent));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        IP = getString(R.string.stringIP);
        mGenderSpinner = view.findViewById(R.id.sexParent);
        mNameView = view.findViewById(R.id.nameParent);
        mDateView = view.findViewById(R.id.dateParent);
        mEmailView = view.findViewById(R.id.emailParent);
        Button mSaveButton = view.findViewById(R.id.SaveButtonParent);
        Button mCancelButton = view.findViewById(R.id.CancelButtonParent);

        spinnerGenderList.add("Female");
        spinnerGenderList.add("Male");
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
                builder.setMessage("Are you sure you want to create this parent?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    attemptCreateParent();
                                } catch (ParseException e) {
                                    e.printStackTrace();
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

    public  void  attemptCreateParent() throws ParseException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ExecutionException, InterruptedException {

        AdditionalMethods m = new AdditionalMethods();
        int errorCount = 0;
        mNameView.setBackgroundResource(R.drawable.field_parent);
        mDateView.setBackgroundResource(R.drawable.field_parent);
        mGenderSpinner.setBackgroundResource(R.drawable.field_parent);
        mEmailView.setBackgroundResource(R.drawable.field_parent);

        String name = mNameView.getText().toString().trim();
        String db = mDateView.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();

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
            } else if (age < 18)
            {
                mDateView.setBackgroundResource(R.drawable.field_parent_error);
                Toast.makeText(view.getContext(),"You cannot create a parent who is under 18!", Toast.LENGTH_LONG).show();
                errorCount++;
            }
        }

        String methodCheckEmail = "CheckEmail";
        BackgroundTaskSelectDB backgroundTaskSelectDBCheckEmail = new BackgroundTaskSelectDB(getActivity());
        CheckEmailResult = backgroundTaskSelectDBCheckEmail.execute(IP, methodCheckEmail, email).get();

        if (isEmpty(email))
        {
            mEmailView.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        } else if (!m.EmailValidator(email))
            {
                mEmailView.setBackgroundResource(R.drawable.field_parent_error);
                Toast.makeText(getContext(),"Email is incorrect!", Toast.LENGTH_SHORT).show();
                errorCount++;
            }
            else if (CheckEmailResult.equals("1"))
                {
                    mEmailView.setBackgroundResource(R.drawable.field_parent_error);
                    Toast.makeText(getContext(),"This email is already occupied!", Toast.LENGTH_SHORT).show();
                    errorCount++;
                }

        if (isEmpty(sex))
        {
            mGenderSpinner.setBackgroundResource(R.drawable.field_parent_error);
            errorCount++;
        }


        if (errorCount == 0)
        {
            if (sex.equals("Female"))
                sexChar = "F";
            else sexChar = "M";
            int lengthPassword = 5;
            AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
            String methodCheckPassword = "CheckPassword";
            String methodRegister = "Register";

            BackgroundTaskExecuteDB backgroundTaskExecuteDBUser = new BackgroundTaskExecuteDB(view.getContext());
            BackgroundTaskExecuteDB backgroundTaskExecuteDBParent = new BackgroundTaskExecuteDB(view.getContext());
            BackgroundTaskExecuteDB backgroundTaskExecuteDBFamily = new BackgroundTaskExecuteDB(view.getContext());
            BackgroundTaskSelectDB backgroundTaskSelectDBCheckPassword = new BackgroundTaskSelectDB(view.getContext());

            code_user = m.GeneratePassword(lengthPassword);
            code_encrypted = methodEncryptionDecryption.Encryption(code_user, getString(R.string.StringKey));
            CheckPasswordResult = backgroundTaskSelectDBCheckPassword.execute(IP, methodCheckPassword, code_encrypted).get();

            if (CheckPasswordResult.equals("0"))
            {
                mSharedPreferences = getActivity().getSharedPreferences("Family",MODE_PRIVATE);
                String Parent1SharedPreferences = mSharedPreferences.getString("parent_1","");
                String Parent2SharedPreferences = mSharedPreferences.getString("parent_2","");

                if (Parent2SharedPreferences.equals("null"))
                {
                    String sqlUser = "INSERT INTO all_users (code_user) VALUES ('" + code_encrypted +"');";
                    backgroundTaskExecuteDBUser.execute(IP, methodRegister, sqlUser);

                    String sqlParent = "INSERT INTO parent (code_parent, bd, email, name, password_parent, sex) VALUES ('" + code_encrypted +
                            "', '" + db + "', '" + email + "', '" + name + "', '" + password_encrypted + "', '" + sexChar + "');";
                    backgroundTaskExecuteDBParent.execute(IP, methodRegister, sqlParent);

                    String sqlFamily = "UPDATE family SET parent_2='" + code_encrypted + "' WHERE parent_1='" + Parent1SharedPreferences +"';";
                    backgroundTaskExecuteDBFamily.execute(IP, methodRegister, sqlFamily);
//
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("After installing the application on the parent's phone, go to the Parent mode and provide this code: " + code_user).setCancelable(false)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToFamilyArea();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("Way to add the parent! Remember this code!");
                    alert.show();
                }
                else {
                    Toast.makeText(getContext(),"You cannot add more than one parent!", Toast.LENGTH_SHORT).show();
                    goToFamilyArea();
                }
            } else System.out.println("The code already exist! Create a child again!");
        }
    }

}
