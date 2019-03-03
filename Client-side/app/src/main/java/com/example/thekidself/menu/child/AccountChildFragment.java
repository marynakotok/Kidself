package com.example.thekidself.menu.child;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.thekidself.R;
import com.example.thekidself.helper.AdditionalMethods;
import com.example.thekidself.helper.BackgroundTaskSelectDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;


public class AccountChildFragment extends Fragment {

    SharedPreferences mSharedPreferences;
    String codeChild;
    TextView mDeviceInfo, mCodeInfo;
    String code_decrypted;
    Switch mServiceSwitch;

    NsdManager _nsdManager;
    NsdManager.RegistrationListener _registrationListener;
    Thread _serviceThread;
    String addressText, IP, nameDatabase;


    View view;
    public AccountChildFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void serviceConnection(Socket socket) throws IOException
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String statusText = "Streaming...";
                System.out.println("statusText: " + statusText);
            }
        });

        final int frequency = 11025;
        final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        final int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                frequency, channelConfiguration,
                audioEncoding, bufferSize);

        final int byteBufferSize = bufferSize*2;
        final byte[] buffer = new byte[byteBufferSize];

        try
        {
            audioRecord.startRecording();

            final OutputStream out = socket.getOutputStream();

            socket.setSendBufferSize(byteBufferSize);
            Log.d("", "Socket send buffer size: " + socket.getSendBufferSize());

            while (socket.isConnected() && !Thread.currentThread().isInterrupted())
            {
                final int read = audioRecord.read(buffer, 0, bufferSize);
                out.write(buffer, 0, read);
            }
        }
        finally
        {
            audioRecord.stop();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account_child, container, false);
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

        mSharedPreferences = getActivity().getSharedPreferences("Child",MODE_PRIVATE);
        codeChild = mSharedPreferences.getString("codeChild","");
        IP = getString(R.string.stringIP);

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

        String sql = "SELECT code_child,name,sex,bd,created_on,last_login FROM child WHERE code_child='" + codeChild + "';";
        String table = "child";
        String methodGetNameGender = "Selection";

        BackgroundTaskSelectDB backgroundTaskSelectDBGetNameGenderChild = new BackgroundTaskSelectDB(getActivity());
        String stream = null;
        try {
            stream = backgroundTaskSelectDBGetNameGenderChild.execute(IP, methodGetNameGender, sql, table).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = null;
        JSONObject obj = null;
        try {
            jsonArray = new JSONArray(stream);
            obj = jsonArray.getJSONObject(0);
            nameDatabase = obj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AdditionalMethods methodEncryptionDecryption = new AdditionalMethods();
        try {
            code_decrypted = methodEncryptionDecryption.Decryption(codeChild, getString(R.string.StringKey));
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

        _nsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);

        mServiceSwitch = (Switch) view.findViewById(R.id.serviceSwitch);

        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    serviceThread();
                    Snackbar.make(view,
                            "Registered for listening to", Snackbar.LENGTH_SHORT).show();
                } else {
                    unregisterService();
                    onDestroy();
                    Snackbar.make(view,
                            "Unregistered from listening to", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }



    public void serviceThread()
    {
        _serviceThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(!Thread.currentThread().isInterrupted())
                {
                    ServerSocket serverSocket = null;

                    try
                    {
                        // Initialize a server socket on the next available port.
                        serverSocket = new ServerSocket(0);

                        // Store the chosen port.
                        final int localPort = serverSocket.getLocalPort();

                        // Register the service so that parent devices can
                        // locate the child device
                        registerService(localPort);

                        // Wait for a parent to find us and connect
                        Socket socket = serverSocket.accept();
                        Log.i("", "Connection from parent device received");

                        // We now have a client connection.
                        // Unregister so no other clients will
                        // attempt to connect
                        serverSocket.close();
                        serverSocket = null;
                        unregisterService();

                        try
                        {
                            serviceConnection(socket);
                        }
                        finally
                        {
                            socket.close();
                        }
                    }
                    catch(IOException e)
                    {
                        Log.e("", "Connection failed", e);
                    }

                    // If an exception was thrown before the connection
                    // could be closed, clean it up
                    if(serverSocket != null)
                    {
                        try
                        {
                            serverSocket.close();
                        }
                        catch (IOException e)
                        {
                            Log.e("", "Failed to close stray connection", e);
                        }
                        serverSocket = null;
                    }
                }
            }
        });
        _serviceThread.start();

        // Use the application context to get WifiManager, to avoid leak before Android 5.1
        final WifiManager wifiManager =
                (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        final WifiInfo info = wifiManager.getConnectionInfo();
        final int address = info.getIpAddress();
        if(address != 0)
        {
            @SuppressWarnings("deprecation")
            final String ipAddress = Formatter.formatIpAddress(address);
            addressText = ipAddress;
            System.out.println("IP mobile :" + ipAddress);
        }
        else
        {
            addressText = "Not connected to a Wi-Fi network";
        }
    }

    @Override
    public void onDestroy()
    {
        Log.i("", "Baby monitor stop");

        unregisterService();

        if(_serviceThread != null)
        {
            _serviceThread.interrupt();
            _serviceThread = null;
        }

        super.onDestroy();
    }

    private void registerService(final int port)
    {
        final NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setServiceName(nameDatabase); //("TheKidSelfListening");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);

        _registrationListener = new NsdManager.RegistrationListener()
        {
            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                final String serviceName = nsdServiceInfo.getServiceName();

                Log.i("", "Service name: " + serviceName);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        String statusText = "Waiting for Parent...";
                        String serviceText = serviceName;
                        String portText = Integer.toString(port);
                    }
                });
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode)
            {
                // Registration failed!  Put debugging code here to determine why.
                Log.e("", "Registration failed: " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0)
            {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.

                Log.i("", "Unregistering service");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode)
            {
                // Unregistration failed.  Put debugging code here to determine why.

                Log.e("", "Unregistration failed: " + errorCode);
            }
        };

        _nsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, _registrationListener);
    }

    /**
     * Uhregistered the service and assigns the listener
     * to null.
     */
    private void unregisterService()
    {
        if(_registrationListener != null)
        {
            Log.i("", "Unregistering monitoring service");

            _nsdManager.unregisterService(_registrationListener);
            _registrationListener = null;
        }
    }
}
