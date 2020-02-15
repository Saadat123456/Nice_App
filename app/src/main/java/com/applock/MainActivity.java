package com.applock;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.applock.adapter.gridviewAdapter;
import com.applock.dealFiles.files;
import com.applock.fragments.allLockedApps;
import com.applock.fragments.settings;
import com.applock.menuService.menu_service;
import com.applock.model.AppItem;
import com.applock.utils.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements allLockedApps.OnFragmentInteractionListener,settings.OnFragmentInteractionListener{

    /*Global Variables Here**/

    LinearLayout layoutPermission;
    Utils utils;
    List<AppItem> lockedAppList;
    gridviewAdapter gridViewAdapter;

    ImageButton navAllApp,navSettings;
    files file;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    /*________________________*/


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (Build.VERSION.SDK_INT >= 23 ) {
            if (!getDrawPermission())
            {
                Toast.makeText(this, "You don't provided permission to draw over apps.", Toast.LENGTH_SHORT).show();
            }
        }

        layoutPermission = findViewById(R.id.layout_permission);

        initNav();
        clickListener();
        startPaperdb();
        initToolbar();

    }

    /* FUNCTIONS Used by OnCreate Method*/

    /*Initialize Menu At Bottom*/
    private void initNav() {
        navAllApp = findViewById(R.id.allAppsButtonNavigation);
        navSettings = findViewById(R.id.settingsButtonNavigation);
    }
    /*_________________________*/

    /* Click Listeners */
    private void clickListener() {
        navAllApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new allLockedApps());
            }
        });

        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new settings());
            }
        });
    }


    /*_________________*/


    /* Fragment Switch*/
    void startFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();
    }
    /*________________*/


    /* Start PaperDb*/

    private void startPaperdb()
    {
        utils = new Utils(this);
    }

    /*_______________*/



    /* Initialize Toolbar*/

    private void initToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        getSupportActionBar().setTitle("Nice App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /*___________________*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home)
            finish();

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.menu_inbox:
                Toast.makeText(this, "Inbox", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.menu_gifts:
                Toast.makeText(this, "Gifts", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.menu_rate:
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_1, menu);
        return true;
    }

    public void setPermission(View view) {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*__________________________________*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean getDrawPermission()
    {
        if(!Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Toast.makeText(this, "Allow app to draw over apps", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            return true;
        }
        return false;
    }


    @Override
    protected void onResume() {

        stopService(new Intent(MainActivity.this, menu_service.class));

        file = new files();

        file.writeFileOnInternalStorage(this,"Yes");

        if(Utils.checkPermission(this))
        {
            layoutPermission.setVisibility(View.GONE);
        }else
        {
            layoutPermission.setVisibility(View.VISIBLE);
        }





        super.onResume();
    }


    @Override
    public void onBackPressed() {
        startService(new Intent(MainActivity.this, menu_service.class));
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                file = new files();

                file.writeFileOnInternalStorage(MainActivity.this,"NO");
            }
        }, 5000);

        super.onPause();
    }

    @Override
    protected void onStop() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                file = new files();

                file.writeFileOnInternalStorage(MainActivity.this,"NO");
            }
        }, 5000);
        super.onStop();
    }


    /*FRAGMENT INTERFACE*/
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}