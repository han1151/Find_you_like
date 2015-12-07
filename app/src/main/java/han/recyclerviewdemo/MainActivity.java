package han.recyclerviewdemo;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> TAB_NAME = new ArrayList<>();
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private ActionButton actionButton_search;
    private DataBaseHelper dataBaseHelper;
    private double[] location = new double[2];
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location[0] = 0;
        location[1] = 0;
        dataBaseHelper = new DataBaseHelper(this);
        Cursor cursor = dataBaseHelper.getAllNames();

        while(cursor.moveToNext()){
            TAB_NAME.add(cursor.getString(0));
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageButton mark = (ImageButton)toolbar.findViewById(R.id.main_mark);
        ImageButton edit_tag =(ImageButton)toolbar.findViewById(R.id.main_tag_setting);
        edit_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TagSelect.class);
                startActivity(intent);
                new ActivityAnimator().PullRightPushLeft(MainActivity.this);
                finish();
            }
        });
        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,markList.class);
                startActivity(intent);
                new ActivityAnimator().PullRightPushLeft(MainActivity.this);
            }
        });
        actionButton_search = (ActionButton)findViewById(R.id.main_search);
        actionButton_search.setRippleEffectEnabled(true);
        actionButton_search.setShowAnimation(ActionButton.Animations.ROLL_FROM_RIGHT);
        actionButton_search.playHideAnimation();
        actionButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButton_search.playShowAnimation();
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("location",location);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        if(TAB_NAME.size() >5) {
            tabs.setShouldExpand(false);
        } else{
            tabs.setShouldExpand(true);
        }
        tabs.setTabPaddingLeftRight(10);
        tabs.setTextSize(35);
        tabs.setTabPaddingLeftRight(20);
        tabs.setTextColor(Color.BLACK);
        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                location[0] = loc.getLatitude();
                location[1] = loc.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        initInfo();

    }

    private void initGPS() {

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Please open GPS to locate your positon");
            dialog.setPositiveButton("OK",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0);
                        }
                    });
            dialog.setNeutralButton("cancel", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            updateLocation();
        }
    }


    private void updateLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                location[0] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                location[1] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                location[0] = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                location[1] = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
            }
        } catch (SecurityException e) {

        }catch (Exception ex){

        }

    }

    private void initInfo() {
        if (!isOnline()) {
           Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            initGPS();
            updateLocation();
        }
    }



    public boolean isOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }
    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_NAME.get(position);
        }

        @Override
        public int getCount() {
            return TAB_NAME.size();
        }

        @Override
        public Fragment getItem(int position) {
                String tag_name = TAB_NAME.get(position);
                return suggestFragment.newInstance(tag_name, location);

        }

    }

}
