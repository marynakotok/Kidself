package com.example.thekidself.map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.thekidself.R;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class AvailableChildServicesFragment extends Fragment {

    View view;
    NsdManager _nsdManager;
    NsdManager.DiscoveryListener _discoveryListener;
    SharedPreferences mSharedPreferences;
    String family, codeParent, child;
    String[] codeChild, nameChildDatabase, codeChildDatabase;
    final static int mNotificationId = 1;
    NotificationManagerCompat _mNotifyMgr;
    Thread _listenThread;
    Dialog dialogListenChild;
    Button mStart, mStop;


    public AvailableChildServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void streamAudio(final Socket socket) throws IllegalArgumentException, IllegalStateException, IOException
    {
        Log.i("", "Setting up stream");

        final int frequency = 11025;
        final int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
        final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        final int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        final int byteBufferSize = bufferSize*2;

        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                frequency,
                channelConfiguration,
                audioEncoding,
                bufferSize,
                AudioTrack.MODE_STREAM);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final InputStream is = socket.getInputStream();
        int read = 0;

        audioTrack.play();

        try
        {
            final byte [] buffer = new byte[byteBufferSize];

            while(socket.isConnected() && read != -1 && Thread.currentThread().isInterrupted() == false)
            {
                read = is.read(buffer);

                if(read > 0)
                {
                    audioTrack.write(buffer, 0, read);
                }
            }
        }
        finally
        {
            audioTrack.stop();
            socket.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_available_child_services, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Discover Child");
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSharedPreferences = getActivity().getSharedPreferences("Parent",MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");
        String IP = getString(R.string.stringIP);

        String sqlFamily = "SELECT parent_1,parent_2, child_1, child_2, child_3, child_4, child_5, child_6 FROM family WHERE parent_1='" + codeParent + "' OR parent_2='" + codeParent + "';";
        String methodGetData = "Selection";
        String tableFamily = "family";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetChildren = new BackgroundTaskSelectDB(view.getContext());

        family = null;
        JSONArray jsonArrayFamily = null;
        JSONObject objFamily = null;
        try {
            family = backgroundTaskSelectDBGetChildren.execute(IP, methodGetData, sqlFamily, tableFamily).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            int length = 6;
            jsonArrayFamily = new JSONArray(family);
            objFamily = jsonArrayFamily.getJSONObject(0);
            codeChild = new String[length];
            codeChild[0] = objFamily.getString("child_1");
            codeChild[1] = objFamily.getString("child_2");
            codeChild[2] = objFamily.getString("child_3");
            codeChild[3] = objFamily.getString("child_4");
            codeChild[4] = objFamily.getString("child_5");
            codeChild[5] = objFamily.getString("child_6");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sqlNameChild = "SELECT code_child, name, sex, bd, created_on, last_login FROM child WHERE code_child='" +
                codeChild[0] + "' OR code_child='" + codeChild[1] + "' OR code_child='" + codeChild[2]
                + "' OR code_child='" + codeChild[3] + "' OR code_child='" + codeChild[4] + "' OR code_child='" + codeChild[5] + "';";
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
            codeChildDatabase = new String[jsonArrayChild.length()];
            for (int i=0; i<jsonArrayChild.length(); i++)
            {
                objChild = jsonArrayChild.getJSONObject(i);
                nameChildDatabase[i] = objChild.getString("name");
                codeChildDatabase[i] = objChild.getString("code_child");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _nsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);
        loadDiscoveryViaMdns();  // discover Child

        return view;
    }

    private void loadDiscoveryViaMdns()
    {
        startServiceDiscovery("_http._tcp.");
    }

    @Override
    public void onDestroy()
    {
        Log.i("", "Child monitoring stop");

        if(_discoveryListener != null)
        {
            Log.i("", "Unregistering monitoring service");

            _nsdManager.stopServiceDiscovery(_discoveryListener);
            _discoveryListener = null;
        }

        super.onDestroy();
    }

    public void startServiceDiscovery(final String serviceType)
    {
        final NsdManager nsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);

        final ListView serviceTable = (ListView) view.findViewById(R.id.ServiceTable);

        final ArrayAdapter<ServiceInfoWrapper> availableServicesAdapter = new ArrayAdapter<>(getContext(),
                R.layout.available_children_list);
        serviceTable.setAdapter(availableServicesAdapter);

        serviceTable.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id)
            {
                dialogListenChild = new Dialog(getContext());
                dialogListenChild.setContentView(R.layout.dialog_listen_child);
                mStart = dialogListenChild.findViewById(R.id.start);
                mStop = dialogListenChild.findViewById(R.id.stop);

                dialogListenChild.show();
                mStart.setEnabled(true);
                mStop.setEnabled(true);

                mStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ServiceInfoWrapper info = (ServiceInfoWrapper) parent.getItemAtPosition(position);
                        connectToChild(info.getAddress(), info.getPort(), info.getName());
                    }
                });

                mStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _listenThread.interrupt();
                        _listenThread = null;
                        dialogListenChild.dismiss();
                    }
                });
            }
        });


        // Instantiate a new DiscoveryListener
        _discoveryListener = new NsdManager.DiscoveryListener()
        {
            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType)
            {
                Log.d("", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d("", "Service discovery success: " + service);

                if (!service.getServiceType().equals(serviceType))
                {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d("", "Unknown Service Type: " + service.getServiceType());
                }
                else if (ArrayUtils.contains(nameChildDatabase, service.getServiceName()))//(service.getServiceName().contains("TheKidSelfListening"))
                {
                    NsdManager.ResolveListener resolver = new NsdManager.ResolveListener()
                    {
                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode)
                        {
                            // Called when the resolve fails.  Use the error code to debug.
                            Log.e("", "Resolve failed: error " + errorCode + " for service: " + serviceInfo);
                        }

                        @Override
                        public void onServiceResolved(final NsdServiceInfo serviceInfo)
                        {
                            Log.i("", "Resolve Succeeded: " + serviceInfo);

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    availableServicesAdapter.add(new ServiceInfoWrapper(serviceInfo));
                                }
                            });
                        }
                    };

                    _nsdManager.resolveService(service, resolver);
                }
                    else
                    {
                        Log.d("", "Unknown Service name: " + service.getServiceName());
                    }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service)
            {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("", "Service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType)
            {
                Log.i("", "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode)
            {
                Log.e("", "Discovery failed: Error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode)
            {
                Log.e("", "Discovery failed: Error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };

        nsdManager.discoverServices(
                serviceType, NsdManager.PROTOCOL_DNS_SD, _discoveryListener);
    }

    /**
     * Launch the ListenActivity to connect to the given child device
     *
     * @param address
     * @param port
     * @param name
     */
    private void connectToChild(final String address, final int port, final String name)
    {
        listenTo(address, port, name);
    }

    public void listenTo(final String address, final int port, String name)
    {
        _mNotifyMgr =
                NotificationManagerCompat.from(getContext());

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.logo_icon)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Listening...");

        _mNotifyMgr.notify(mNotificationId, mBuilder.build());

        _listenThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final Socket socket = new Socket(address, port);
                    streamAudio(socket);
                }
                catch (UnknownHostException e)
                {
                    Log.e("", "Failed to stream audio", e);
                }
                catch (IOException e)
                {
                    Log.e("", "Failed to stream audio", e);
                }

                if(!Thread.currentThread().isInterrupted())
                {
                    // If this thread has not been interrupted, likely something
                    // bad happened with the connection to the child device. Play
                    // an alert to notify the user that the connection has been
                    // interrupted.
                    playAlert();

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getContext())
                                            .setOngoing(false)
                                            .setSmallIcon(R.drawable.logo_icon)
                                            .setContentTitle(getString(R.string.app_name))
                                            .setContentText("Disconnected");
                            _mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                    });
                }
            }
        });

        _listenThread.start();
    }

    private void playAlert()
    {
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.notification);
        if(mp != null)
        {
            Log.i("", "Playing alert");
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mp.start();
        }
        else
        {
            Log.e("", "Failed to play alert");
        }
    }



}

class ServiceInfoWrapper
{
    private NsdServiceInfo _info;

    public ServiceInfoWrapper(NsdServiceInfo info)
    {
        _info = info;
    }

    public String getAddress()
    {
        return _info.getHost().getHostAddress();
    }

    public int getPort()
    {
        return _info.getPort();
    }

    public String getName()
    {
        String serviceName = _info.getServiceName();
        serviceName = serviceName.replace("\\\\032", " ");
        serviceName = serviceName.replace("\\032", " ");
        return serviceName;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
