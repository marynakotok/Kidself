package com.example.thekidself.menu.parent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskExecuteDB;
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

import static android.content.Context.MODE_PRIVATE;


public class FamilyAreaFragment extends Fragment {

    private String codeParent;
    SharedPreferences mSharedPreferences;
    private TextView mNameParent1, mNameParent2, mNameChild1, mNameChild2, mNameChild3, mNameChild4, mNameChild5, mNameChild6;
    private ImageView mImageParent1, mImageParent2,mImageChild1, mImageChild2,mImageChild3,mImageChild4,mImageChild5, mImageChild6;
    private ImageView mInfoParent2,mInfoChild1, mInfoChild2,mInfoChild3,mInfoChild4,mInfoChild5, mInfoChild6;
    private ImageView mBinParent2,mBinChild1, mBinChild2,mBinChild3,mBinChild4,mBinChild5, mBinChild6;
    View view;
    String IP, parent, child, family;
    String nameParent1Database, genderParent1Database, nameParent2Database, genderParent2Database;
    String sqlParent, sqlChild;
    String Parent1Database, Parent2Database, Child1Database,Child2Database,Child3Database,Child4Database,Child5Database,Child6Database;
    String[] nameParentsDatabase, genderParentsDatabase, codeParentsDatabase;
    String[] nameChildrenDatabase, genderChildrenDatabase, codeChildrenDatabase;
    int i, j, countChildren;
    String child1, child2,child3,child4,child5,child6;
    TextView name;
    ImageView image;
    ProgressDialog progressDialog;
    String place, spot;
    private String[] arrayToSwap;

    public FamilyAreaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_family_area, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringFamilyArea));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSharedPreferences = view.getContext().getSharedPreferences("Parent", MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");
        IP = getString(R.string.stringIP);
        bundle();
        String methodGetData = "Selection";

        // Get data from family database

        getFamily();

        if (Child1Database.equals("null") &&  Child2Database.equals("null") &&  Child3Database.equals("null") &&  Child4Database.equals("null") &&  Child5Database.equals("null") &&  Child6Database.equals("null"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("You should create at least one child to continue working with current application!").setCancelable(false).setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Important!");
            alert.show();
        }
        // Get data from parent database

        String tableParent = "parent";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetParent = new BackgroundTaskSelectDB(view.getContext());
        if (!Parent2Database.equals("null"))
        {
            sqlParent = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE code_parent='" + Parent1Database + "' OR code_parent='" + Parent2Database + "';";

        } else sqlParent = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE code_parent='" + Parent1Database + "';";

        parent = null;
        try {
            parent = backgroundTaskSelectDBGetParent.execute(IP, methodGetData, sqlParent, tableParent).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            JSONArray jsonArrayParent = null;
            JSONObject objParent = null;
            try {
                jsonArrayParent = new JSONArray(parent);
                nameParentsDatabase = new String[jsonArrayParent.length()];
                genderParentsDatabase = new String[jsonArrayParent.length()];
                codeParentsDatabase = new String[jsonArrayParent.length()];
                for (int i=0; i<jsonArrayParent.length(); i++)
                {
                    objParent = jsonArrayParent.getJSONObject(i);
                    nameParentsDatabase[i] = objParent.getString("name");
                    genderParentsDatabase[i] = objParent.getString("sex");
                    codeParentsDatabase[i] = objParent.getString("code_parent");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        //Set Parents on Screen Family Area
        if (!Parent2Database.equals("null")) {

            if (codeParentsDatabase[0].equals(Parent1Database)) {
                i = 0;
                j = 1;
            } else {
                i = 1;
                j = 0;
            }

            mNameParent1.setText(nameParentsDatabase[i]);
            mNameParent2.setText(nameParentsDatabase[j]);
            if (genderParentsDatabase[i].trim().equals("F")) {
                mImageParent1.setImageResource(R.drawable.woman);
            } else {
                mImageParent1.setImageResource(R.drawable.man);
            }

            if (genderParentsDatabase[j].trim().equals("F")) {
                mImageParent2.setImageResource(R.drawable.woman);
            } else {
                mImageParent2.setImageResource(R.drawable.man);
            }

        } else
            {
                mNameParent1.setText(nameParentsDatabase[0]);
                if (genderParentsDatabase[0].trim().equals("F"))
                {
                    mImageParent1.setImageResource(R.drawable.woman);
                } else {
                    mImageParent1.setImageResource(R.drawable.man);
                }
            }


            //Children

        if (Child1Database.equals("null"))
        {
            countChildren = 0;
            sqlChild ="";
        } else if (Child2Database.equals("null"))
        {
            countChildren=1;
            sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + Child1Database + "';";
        } else if (Child3Database.equals("null"))
        {
            countChildren=2;
            sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + Child1Database + "' OR code_child='" + Child2Database + "';";
        } else if (Child4Database.equals("null"))
        {
            countChildren=3;
            sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + Child1Database + "' OR code_child='" + Child2Database + "' OR code_child='" + Child3Database + "';";
        } else if (Child5Database.equals("null"))
        {
            countChildren=4;
            sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + Child1Database + "' OR code_child='" + Child2Database + "' OR code_child='" + Child3Database + "' OR code_child='" + Child4Database + "';";
        } else if (Child6Database.equals("null"))
        {
            countChildren=5;
            sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + Child1Database + "' OR code_child='" + Child2Database + "' OR code_child='" + Child3Database + "' OR code_child='" + Child4Database + "' OR code_child='" + Child5Database + "';";
        } else {countChildren=6; sqlChild = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + Child1Database + "' OR code_child='" + Child2Database + "' OR code_child='" + Child3Database + "' OR code_child='" + Child4Database + "' OR code_child='" + Child5Database + "' OR code_child='" + Child6Database + "';";}

        String tableChild = "child";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetChild = new BackgroundTaskSelectDB(view.getContext());

        if (countChildren>0)
        {
            child = null;
            try {
                child = backgroundTaskSelectDBGetChild.execute(IP, methodGetData, sqlChild, tableChild).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

                JSONArray jsonArrayChild = null;
                JSONObject objChild = null;
                try {
                    jsonArrayChild = new JSONArray(child);
                    nameChildrenDatabase = new String[jsonArrayChild.length()];
                    genderChildrenDatabase = new String[jsonArrayChild.length()];
                    codeChildrenDatabase = new String[jsonArrayChild.length()];
                    for (int i=0; i<jsonArrayChild.length(); i++)
                    {
                        objChild = jsonArrayChild.getJSONObject(i);
                        nameChildrenDatabase[i] = objChild.getString("name");
                        genderChildrenDatabase[i] = objChild.getString("sex");
                        codeChildrenDatabase[i] = objChild.getString("code_child");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            for (int i=0; i<jsonArrayChild.length(); i++)
            {
                switch (i){
                    case 0:
                        name = mNameChild1;
                        image = mImageChild1;
                        break;
                    case 1:
                        name = mNameChild2;
                        image = mImageChild2;
                        break;
                    case 2:
                        name = mNameChild3;
                        image = mImageChild3;
                        break;
                    case 3:
                        name = mNameChild4;
                        image = mImageChild4;
                        break;
                    case 4:
                        name = mNameChild5;
                        image = mImageChild5;
                        break;
                    case 5:
                        name = mNameChild6;
                        image = mImageChild6;
                        break;
                }

                name.setText(nameChildrenDatabase[i]);
                if (genderChildrenDatabase[i].trim().equals("F"))
                {
                    image.setImageResource(R.drawable.girl);
                } else {
                    image.setImageResource(R.drawable.boy);
                }
            }

        }

            //Parent2

        if (!codeParent.equals(Parent2Database))
        {
            if (!Parent2Database.equals("null"))
            {
                mBinParent2.setImageResource(R.drawable.ic_delete);
                mInfoParent2.setImageResource(R.drawable.ic_info);
                mBinParent2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFromDatabase("Parent", Parent2Database, "");
                    }
                });

                mInfoParent2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ShowInfo(nameParentsDatabase[j], Parent2Database);
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

            } else
            {
                mImageParent2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowCreateParentFragment();
                    }
                });
            }
        }

        //Child1

        if (!Child1Database.equals("null"))
        {
            mBinChild1.setImageResource(R.drawable.ic_delete);
            mInfoChild1.setImageResource(R.drawable.ic_info);
                mBinChild1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String spot = checkChildInFamily(codeChildrenDatabase[0]);
                        deleteFromDatabase("Child", codeChildrenDatabase[0], spot);
                    }
                });

                mInfoChild1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ShowInfo(nameChildrenDatabase[0], codeChildrenDatabase[0]);
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
        } else
            {
                mImageChild1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowCreateChildFragment();
                    }
                });
            }

        //Child2

        if (!Child2Database.equals("null"))
        {
            mBinChild2.setImageResource(R.drawable.ic_delete);
            mInfoChild2.setImageResource(R.drawable.ic_info);
                mBinChild2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String spot = checkChildInFamily(codeChildrenDatabase[1]);
                        deleteFromDatabase("Child", codeChildrenDatabase[1], spot);
                    }
                });

                mInfoChild2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ShowInfo(nameChildrenDatabase[1], codeChildrenDatabase[1]);
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

        } else {
                mImageChild2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowCreateChildFragment();
                    }
                });
            }

        //Child3


        if (!Child3Database.equals("null"))
        {
            mBinChild3.setImageResource(R.drawable.ic_delete);
            mInfoChild3.setImageResource(R.drawable.ic_info);
            mBinChild3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String spot = checkChildInFamily(codeChildrenDatabase[2]);
                    deleteFromDatabase("Child", codeChildrenDatabase[2], spot);
                }
            });

            mInfoChild3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ShowInfo(nameChildrenDatabase[2], codeChildrenDatabase[2]);
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
        } else
            {
                mImageChild3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowCreateChildFragment();
                    }
                });
            }

        //Child4

        if (!Child4Database.equals("null"))
        {
            mBinChild4.setImageResource(R.drawable.ic_delete);
            mInfoChild4.setImageResource(R.drawable.ic_info);

            mBinChild4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String spot = checkChildInFamily(codeChildrenDatabase[3]);
                    deleteFromDatabase("Child", codeChildrenDatabase[3], spot);
                }
            });

            mInfoChild4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ShowInfo(nameChildrenDatabase[3], codeChildrenDatabase[3]);
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
        } else {
            mImageChild4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowCreateChildFragment();
                }
            });
        }

        //Child5

        if (!Child5Database.equals("null"))
        {
            mBinChild5.setImageResource(R.drawable.ic_delete);
            mInfoChild5.setImageResource(R.drawable.ic_info);
            mBinChild5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String spot = checkChildInFamily(codeChildrenDatabase[4]);
                    deleteFromDatabase("Child", codeChildrenDatabase[4], spot);
                }
            });

            mInfoChild5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ShowInfo(nameChildrenDatabase[4], codeChildrenDatabase[4]);
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
        } else {
            mImageChild5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowCreateChildFragment();
                }
            });
        }

        //Child6
        if (!Child6Database.equals("null"))
        {
            mBinChild6.setImageResource(R.drawable.ic_delete);
            mInfoChild6.setImageResource(R.drawable.ic_info);
            mBinChild6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String spot = checkChildInFamily(codeChildrenDatabase[5]);
                    deleteFromDatabase("Child", codeChildrenDatabase[5], spot);
                }
            });

            mInfoChild6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ShowInfo(nameChildrenDatabase[5], codeChildrenDatabase[5]);
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
        } else {
            mImageChild6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowCreateChildFragment();
                }
            });
        }

        return view;
    }

    public void getFamily()
    {
        String methodGetData = "Selection";
        String sqlFamily = "SELECT parent_1,parent_2,child_1,child_2,child_3,child_4,child_5,child_6 FROM family WHERE parent_1='" + codeParent + "' OR parent_2='" + codeParent + "';";
        String tableFamily = "family";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetFamily = new BackgroundTaskSelectDB(view.getContext());
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
            Parent1Database = objFamily.getString("parent_1");
            Parent2Database = objFamily.getString("parent_2");
            Child1Database = objFamily.getString("child_1");
            Child2Database = objFamily.getString("child_2");
            Child3Database = objFamily.getString("child_3");
            Child4Database = objFamily.getString("child_4");
            Child5Database = objFamily.getString("child_5");
            Child6Database = objFamily.getString("child_6");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSharedPreferences = getActivity().getSharedPreferences("Family", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("parent_1", Parent1Database);
        editor.putString("parent_2", Parent2Database);
        editor.putString("child_1", Child1Database);
        editor.putString("child_2", Child2Database);
        editor.putString("child_3", Child3Database);
        editor.putString("child_4", Child4Database);
        editor.putString("child_5", Child5Database);
        editor.putString("child_6", Child6Database);
        editor.commit();
    }

    public void deleteFromDatabase(final String table, final String who, final String child)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this " + table + "?").setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Deleting...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    delete(table, who, child);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }).start();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Dangerous!");
        alert.show();
}

    public void delete (String table, String who, String child)
    {
        String methodRegister = "Register";
        String column,columnFamily;

        BackgroundTaskExecuteDB backgroundTaskExecuteDBUser = new BackgroundTaskExecuteDB(view.getContext());
        BackgroundTaskExecuteDB backgroundTaskExecuteDBParent = new BackgroundTaskExecuteDB(view.getContext());
        BackgroundTaskExecuteDB backgroundTaskExecuteDBFamily = new BackgroundTaskExecuteDB(view.getContext());
        BackgroundTaskExecuteDB backgroundTaskExecuteDBMessages = new BackgroundTaskExecuteDB(view.getContext());

        if (table.equals("Parent"))
            columnFamily="parent_2";
        else columnFamily=child;

        String sqlFamily = "UPDATE family SET " + columnFamily + "=null WHERE parent_1='" + Parent1Database +"';";
        backgroundTaskExecuteDBFamily.execute(IP, methodRegister, sqlFamily);

        if (table.equals("Parent"))
            column="code_parent";
        else column="code_child";

        String sqlTable = "DELETE FROM " + table + " WHERE " + column + "='" + who +"';";
        backgroundTaskExecuteDBParent.execute(IP, methodRegister, sqlTable);

        String sqlMessages = "DELETE FROM messages WHERE code_user='" + who +"';";
        backgroundTaskExecuteDBMessages.execute(IP, methodRegister, sqlMessages);

        String sqlUser = "DELETE FROM all_users WHERE code_user='" + who +"';";
        backgroundTaskExecuteDBUser.execute(IP, methodRegister, sqlUser);

        swapFamily();

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.detach(this).attach(this).commit();

        Snackbar.make(view, table + " is deleted", Snackbar.LENGTH_LONG).show();
    }

    public void swapFamily(){

        getFamily();
        int loop = 5;
        String methodRegister = "Register";
        arrayToSwap = new String[6];
        arrayToSwap[0] = Child1Database;
        arrayToSwap[1] = Child2Database;
        arrayToSwap[2] = Child3Database;
        arrayToSwap[3] = Child4Database;
        arrayToSwap[4] = Child5Database;
        arrayToSwap[5] = Child6Database;

        while (loop >= 0)
        {
            for (int i = 5; i>0 ; i--)
            {
                if(!arrayToSwap[i].equals("null") && arrayToSwap[i-1].equals("null"))
                {
                    arrayToSwap[i-1] = arrayToSwap[i];
                    arrayToSwap[i] = "null";
                }
            }
            loop--;
        }
        if (!arrayToSwap[0].equals("null"))
        {
            child1 = "'" + arrayToSwap[0] + "'";
        } else child1 = arrayToSwap[0];

        if (!arrayToSwap[1].equals("null"))
        {
            child2 = "'" + arrayToSwap[1] + "'";
        } else child2 = arrayToSwap[1];

        if (!arrayToSwap[2].equals("null"))
        {
            child3 = "'" + arrayToSwap[2] + "'";
        } else child3 = arrayToSwap[2];

        if (!arrayToSwap[3].equals("null"))
        {
            child4 = "'" + arrayToSwap[3] + "'";
        } else child4 = arrayToSwap[3];

        if (!arrayToSwap[4].equals("null"))
        {
            child5 = "'" + arrayToSwap[4] + "'";
        } else child5 = arrayToSwap[4];

        if (!arrayToSwap[5].equals("null"))
        {
            child6 = "'" + arrayToSwap[5] + "'";
        } else child6 = arrayToSwap[5];

        BackgroundTaskExecuteDB backgroundTaskExecuteDBSwap = new BackgroundTaskExecuteDB(view.getContext());
        String sqlSwap = "UPDATE family SET child_1=" + child1 + ", child_2=" + child2 + ", child_3=" + child3 + ", child_4=" + child4 + ", child_5=" + child5 + ", child_6=" + child6 + " WHERE parent_1='" + Parent1Database +"';";
        backgroundTaskExecuteDBSwap.execute(IP, methodRegister, sqlSwap);
    }

    public void ShowInfo(String who, String whoCode) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
        String code = methodEncryptionDecryption.Decryption(whoCode, getString(R.string.StringKey));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This family member " + who + " has the following code: " + code).setCancelable(false).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Here you have the member's code!");
        alert.show();
    }

    public void ShowCreateParentFragment()
    {
        CreateAnotherParentFragment fragment;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment = new CreateAnotherParentFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_area, fragment).addToBackStack(null);
        ft.commit();
    }

    public void ShowCreateChildFragment()
    {
        CreateChildFragment fragment;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment = new CreateChildFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_area, fragment).addToBackStack(null);
        ft.commit();
    }

    public String checkChildInFamily(String code)
    {
        if (code.equals(Child1Database)) place = "child_1";
        else if (code.equals(Child2Database)) place = "child_2";
        else if (code.equals(Child3Database)) place = "child_3";
        else if (code.equals(Child4Database)) place = "child_4";
        else if (code.equals(Child5Database)) place = "child_5";
        else if (code.equals(Child6Database)) place = "child_6";

        return place;
    }

    private void bundle()
    {
        mNameParent1 = view.findViewById(R.id.textParent1);
        mNameParent2 = view.findViewById(R.id.textParent2);
        mNameChild1 = view.findViewById(R.id.textChild1);
        mNameChild2 = view.findViewById(R.id.textChild2);
        mNameChild3 = view.findViewById(R.id.textChild3);
        mNameChild4 = view.findViewById(R.id.textChild4);
        mNameChild5 = view.findViewById(R.id.textChild5);
        mNameChild6 = view.findViewById(R.id.textChild6);

        mBinParent2 = view.findViewById(R.id.binParent2);
        mBinChild1 = view.findViewById(R.id.binChild1);
        mBinChild2 = view.findViewById(R.id.binChild2);
        mBinChild3 = view.findViewById(R.id.binChild3);
        mBinChild4 = view.findViewById(R.id.binChild4);
        mBinChild5 = view.findViewById(R.id.binChild5);
        mBinChild6 = view.findViewById(R.id.binChild6);

        mImageParent1 = view.findViewById(R.id.imageParent1);
        mImageParent2 = view.findViewById(R.id.imageParent2);
        mImageChild1 = view.findViewById(R.id.imageChild1);
        mImageChild2 = view.findViewById(R.id.imageChild2);
        mImageChild3 = view.findViewById(R.id.imageChild3);
        mImageChild4 = view.findViewById(R.id.imageChild4);
        mImageChild5 = view.findViewById(R.id.imageChild5);
        mImageChild6 = view.findViewById(R.id.imageChild6);

        mInfoParent2 = view.findViewById(R.id.infoParent2);
        mInfoChild1 = view.findViewById(R.id.infoChild1);
        mInfoChild2 = view.findViewById(R.id.infoChild2);
        mInfoChild3 = view.findViewById(R.id.infoChild3);
        mInfoChild4 = view.findViewById(R.id.infoChild4);
        mInfoChild5 = view.findViewById(R.id.infoChild5);
        mInfoChild6 = view.findViewById(R.id.infoChild6);
    }

}
