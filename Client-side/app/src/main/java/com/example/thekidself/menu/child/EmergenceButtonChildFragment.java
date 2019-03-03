package com.example.thekidself.menu.child;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class EmergenceButtonChildFragment extends Fragment {

    View view;
    Button mEmergenceButton;
    SharedPreferences mSharedPreferences;
    private String codeChild;
    String[] emailParentsDatabase, nameChildDatabase;
    String codeParent2, codeParent1;
    String IP, family, parent, child;
    FusedLocationProviderClient mFusedLocationClient;
    private int STORAGE_PERMISSION_CODE = 1;
    LocationManager locationManager;
    Double longitude, latitude;
    String address;


    public EmergenceButtonChildFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_emergence_button_child, container, false);
        IP = getString(R.string.stringIP);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringEmergencyButtonChild));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mEmergenceButton = view.findViewById(R.id.emergence_button);
        mEmergenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    react();
            }
        });

        return view;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("This permission needed for getting your localization")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetAddressTask extends
            AsyncTask<Location, Void, String> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            Location loc = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = String.format(
                        "%s",
                        address.getAddressLine(0));
                return addressText;
            } else {
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String address) {
            //System.out.println("address : " + address);
        }

    }

    public void react() {


        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestLocationPermission();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent())
                            {
                                address = null;
                                try {
                                    address = (new GetAddressTask(getContext())).execute(location).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                mSharedPreferences = getActivity().getSharedPreferences("Child",MODE_PRIVATE);
                                codeChild = mSharedPreferences.getString("codeChild","");

                                //Get codes of family

                                String sqlFamily = "SELECT parent_1,parent_2, child_1, child_2, child_3, child_4, child_5, child_6 FROM family WHERE child_1='" +
                                        codeChild + "' OR child_2='" + codeChild + "' OR child_3='" + codeChild + "' OR child_4='" +
                                        codeChild + "' OR child_5='" + codeChild + "' OR child_6='" + codeChild + "';";
                                String methodGetData = "Selection";
                                String tableFamily = "family";
                                BackgroundTaskSelectDB backgroundTaskSelectDBGetParents = new BackgroundTaskSelectDB(view.getContext());

                                family = null;
                                JSONArray jsonArrayFamily = null;
                                JSONObject objFamily = null;
                                try {
                                    family = backgroundTaskSelectDBGetParents.execute(IP, methodGetData, sqlFamily, tableFamily).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonArrayFamily = new JSONArray(family);
                                    objFamily = jsonArrayFamily.getJSONObject(0);
                                    codeParent1 = objFamily.getString("parent_1");
                                    codeParent2 = objFamily.getString("parent_2");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Get emails of parents

                                String sqlEmailParent = "SELECT code_parent,name,sex,bd,email,password_parent,created_on,last_login FROM parent WHERE code_parent='" +
                                        codeParent1 + "' OR code_parent='" + codeParent2 + "';";
                                String tableParent = "parent";
                                BackgroundTaskSelectDB backgroundTaskSelectDBGetEmailParents = new BackgroundTaskSelectDB(view.getContext());

                                parent = null;
                                try {
                                    parent = backgroundTaskSelectDBGetEmailParents.execute(IP, methodGetData, sqlEmailParent, tableParent).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                JSONArray jsonArrayParent = null;
                                JSONObject objParent = null;
                                try {
                                    jsonArrayParent = new JSONArray(parent);
                                    emailParentsDatabase = new String[jsonArrayParent.length()];
                                    for (int i=0; i<jsonArrayParent.length(); i++)
                                    {
                                        objParent = jsonArrayParent.getJSONObject(i);
                                        emailParentsDatabase[i] = objParent.getString("email");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Get name of the Child

                                String sqlNameChild = "SELECT code_child, name, sex, bd, created_on, last_login FROM child WHERE code_child='" +
                                        codeChild + "';";
                                String tableChild = "child";
                                BackgroundTaskSelectDB backgroundTaskSelectDBGetNameChild = new BackgroundTaskSelectDB(view.getContext());

                                child = null;
                                try {
                                    child = backgroundTaskSelectDBGetNameChild.execute(IP, methodGetData, sqlNameChild, tableChild).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                JSONArray jsonArrayChild = null;
                                JSONObject objChild = null;
                                try {
                                    jsonArrayChild = new JSONArray(child);
                                    nameChildDatabase = new String[jsonArrayChild.length()];
                                    for (int i=0; i<jsonArrayChild.length(); i++)
                                    {
                                        objChild = jsonArrayChild.getJSONObject(i);
                                        nameChildDatabase[i] = objChild.getString("name");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

                                BackgroundTaskSelectDB backgroundTaskSelectDBSendEmail = new BackgroundTaskSelectDB(getContext());

                                String addressReady = address.replaceAll(" ", "%20");
                                String timeReady = time.replaceAll(" ", "%20");
                                String methodSendEmail = "SendEmail";
                                String urlSendEmail = "";

                                if (jsonArrayParent.length() == 2)
                                {
                                    if (!emailParentsDatabase[1].equals("null") && !emailParentsDatabase[0].equals("null"))
                                    {
                                        urlSendEmail = "http://" + IP + "/TheKidSelfPHP/SendEmail.php?email_parent1=" +
                                                emailParentsDatabase[0] + "&email_parent2=" + emailParentsDatabase[1] + "&child=" + nameChildDatabase[0] + "&longitude=" +
                                                longitude + "&latitude=" + latitude + "&time=" + timeReady + "&address=" + addressReady;
                                    }} else if (jsonArrayParent.length() == 1)
                                    {
                                        if (!emailParentsDatabase[0].equals("null"))
                                        {
                                            urlSendEmail = "http://" + IP + "/TheKidSelfPHP/SendEmail.php?email_parent1=" +
                                                    emailParentsDatabase[0] + "&email_parent2=null&child=" + nameChildDatabase[0] + "&longitude=" +
                                                    longitude + "&latitude=" + latitude + "&time=" + timeReady + "&address=" + addressReady;
                                        }
                                    }

                                try {
                                    String result = backgroundTaskSelectDBSendEmail.execute(IP, methodSendEmail, urlSendEmail).get();
                                    //System.out.println("result : " + result);
                                    Snackbar.make(view, "You have just sent a message to your parents! Help is coming..", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }



                            }
                        }
                    }
                });
    }
}
