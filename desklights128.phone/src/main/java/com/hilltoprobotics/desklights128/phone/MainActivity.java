package com.hilltoprobotics.desklights128.phone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ServiceListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static ActionBar actionBar;
    public int theColor = -12303292;
    public static SharedPreferences sharedPrefs;
    public static String IPAddress = "";
    private static final String START_ACTIVITY = "/start_activity";
    GoogleApiClient mApiClient = null;
    private static final UUID APP_UUID = UUID.fromString("07a3fb4a-b4e5-4a98-9a5a-58bedcaf132f");
    private static final int DATA_KEY = 0;
    public static boolean wearInstalled = false;
    public static boolean pebbleInstalled = false;
    android.net.wifi.WifiManager.MulticastLock lock;
    public static JmDNS jmdns;
    private String type = "_desklights._tcp.local.";
    private static String TAG = "dl128";
    public static String bonjourIP = "None discovered";
    android.os.Handler handler = new android.os.Handler();
    private boolean connected = false;
    public static int notificationID = 0;
    Notification.Builder mBuilder;
    NotificationManager mNotificationManager;
    public static StableArrayAdapter adapter;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public AmbilWarnaDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int initialColor = 0;

        initGoogleApiClient();

        FragmentManager fragmentManager = getFragmentManager();
        String menuFragment = getIntent().getStringExtra("menufragment");
        if (menuFragment != null) {
            Log.d(TAG, menuFragment);
            if (menuFragment.equals("BonjourTablesFragment")) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(8)) //attach number 8, BonjourTablesFragment
                        .commit();
            }
        }


        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        IPAddress = sharedPrefs.getString("prefIP", "127.0.1.1");

        try {
            getPackageManager().getPackageInfo("com.google.android.wearable.app", PackageManager.GET_META_DATA);
            wearInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            wearInstalled = false;
        }
        try {
            getPackageManager().getPackageInfo("com.getpebble.android", PackageManager.GET_META_DATA);
            pebbleInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            pebbleInstalled = false;
        }

        if(pebbleInstalled) {
            PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
            PebbleKit.PebbleDataReceiver dataHandler;
            dataHandler = new PebbleKit.PebbleDataReceiver(APP_UUID) {
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                    PebbleKit.sendAckToPebble(context, transactionId);
                    int theData = data.getUnsignedIntegerAsLong(DATA_KEY).intValue();

                    switch (theData) {
                        case 0: {
                            sendData("color?h=FF0000");
                            break;
                        }
                        case 1: {
                            sendData("color?h=FF6600");
                            break;
                        }
                        case 2: {
                            sendData("color?h=FFFF00");
                            break;
                        }
                        case 3: {
                            sendData("color?h=336600");
                            break;
                        }
                        case 4: {
                            sendData("color?h=003333");
                            break;
                        }
                        case 5: {
                            sendData("color?h=330033");
                            break;
                        }
                        case 6: {
                            sendData("default?id=1");
                            break;
                        }
                        case 7: {
                            sendData("default?id=2");
                            break;
                        }
                        case 8: {
                            sendData("default?id=3");
                            break;
                        }
                        case 9: {
                            sendData("off");
                            break;
                        }
                    }
                }
            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), dataHandler);
        }

       dialog = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                ActionBar bar = getActionBar();
                bar.setBackgroundDrawable(new ColorDrawable(color));
                theColor = color;
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        handler.postDelayed(new Runnable() {
            public void run() {
                setUp();
                Log.d(TAG, "enabling jmdns");
                ThreadExecutor.runTask(new Runnable() {

                    public void run() {
                        try {
                            jmdns = JmDNS.create();
                            jmdns.addServiceListener(type, MainActivity.this);
                            connected = true;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            public void run() {
                setUp();
                if(jmdns != null) {
                    jmdns.removeServiceListener(type, MainActivity.this);

                    ThreadExecutor.runTask(new Runnable() {

                        public void run() {
                            try {
                                jmdns.close();
                                jmdns = null;
                            } catch (IOException e) {
                                Log.d(TAG, String.format("ZeroConf Error: %s", e.getMessage()));
                            }
                        }
                    });

                    lock.release();
                    lock = null;
                    connected = false;
                }
            }
        }, 3000);
    }

    private void setUp() { //set up wifi multicast lock
        android.net.wifi.WifiManager wifi =
                (android.net.wifi.WifiManager)
                        getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("jmDNSlock");
        lock.setReferenceCounted(true);
        lock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNotificationManager != null) {
            mNotificationManager.cancel(notificationID);
        }
        if (lock != null) lock.release(); //release wifi multicast lock
    }

    void sendData(String theData) {
        String url = "http://" + sharedPrefs.getString("prefIP", "127.0.0.1") + "/" + theData;
        final ThreadedRequest tReq = new ThreadedRequest(url);
        tReq.start(new Runnable()
        {
            public void run()
            {
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        Fragment fragment = null;
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                fragment = new AlertFragment();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                fragment = new ColorAllFragment();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                fragment = new ColorCommandsFragment();
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                fragment = new ColorPixelFragment();
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                fragment = new ColorWipeFragment();
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                fragment = new WriteCharFragment();
                break;
            case 7:
                mTitle = getString(R.string.title_section8);
                fragment = new PlaySnakeFragment();
                break;
            case 8:
                mTitle = getString(R.string.title_section9);
                fragment = new BonjourTablesFragment();
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
        }


    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
        }
        if (id == R.id.colorPick) {
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
        PebbleKit.PebbleDataReceiver dataHandler;
        dataHandler = new PebbleKit.PebbleDataReceiver(APP_UUID) {
            public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                PebbleKit.sendAckToPebble(context, transactionId);
                int theData = data.getUnsignedIntegerAsLong(DATA_KEY).intValue();

                switch(theData) {
                    case 0: {
                        sendData("color?h=FF0000");
                        break;
                    }
                    case 1: {
                        sendData("color?h=FF6600");
                        break;
                    }
                    case 2: {
                        sendData("color?h=FFFF00");
                        break;
                    }
                    case 3: {
                        sendData("color?h=336600");
                        break;
                    }
                    case 4: {
                        sendData("color?h=003333");
                        break;
                    }
                    case 5: {
                        sendData("color?h=330033");
                        break;
                    }
                    case 6: {
                        sendData("default?id=1");
                        break;
                    }
                    case 7: {
                        sendData("default?id=2");
                        break;
                    }
                    case 8: {
                        sendData("default?id=3");
                        break;
                    }
                    case 9: {
                        sendData("off");
                        break;
                    }
                }
            }
        };
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), dataHandler);
    }

    @Override
    public void serviceResolved(ServiceEvent ev) {
        String additions = "";
        if (ev.getInfo().getInetAddresses() != null && ev.getInfo().getInetAddresses().length > 0) {
            additions = ev.getInfo().getInetAddresses()[0].getHostAddress();
        }
        Log.d(TAG, "Service resolved: " + ev.getInfo().getQualifiedName() + " port:" + ev.getInfo().getPort() + additions);
        MainActivity.bonjourIP = additions;
        final ArrayList<String> list = new ArrayList<String>();
        list.add(MainActivity.bonjourIP);
        adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Bonjour Table Discovered")
                        .setContentText(bonjourIP);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("menufragment", "BonjourTablesFragment");

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    @Override
    public void serviceRemoved(ServiceEvent ev) {
        Log.d(TAG, "Service removed: " + ev.getName());
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        // Required to force serviceResolved to be called again (after the first search)
        jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
        Log.d(TAG, event.getName());
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendMessage( START_ACTIVITY, "" );
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }

            }
        }).start();
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
