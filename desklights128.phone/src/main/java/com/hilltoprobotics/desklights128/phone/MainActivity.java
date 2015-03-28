package com.hilltoprobotics.desklights128.phone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        IPAddress = sharedPrefs.getString("prefIP","127.0.1.1");

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
}
