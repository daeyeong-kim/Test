package com.furence.furence;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    int nextT = (24*60*60*1000);
//    int nextT = 0;

    int MY_PERMISSIONS_REQUEST_SYSTEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = getSharedPreferences("pref", MODE_PRIVATE);
                editor = pref.edit();
                editor.putBoolean("autoLogin", false);
                editor.commit();
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.name);
        nav_user.setText(pref.getString("id", ""));


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SYSTEM_ALERT_WINDOW);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SYSTEM_ALERT_WINDOW)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
//                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                startActivity(myIntent);
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_SYSTEM);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

//        if(Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)){
//
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.SYSTEM_ALERT_WINDOW
//            },1);
//        }

        // SWITCH EVENT & LOAD
        swichLoad();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void swichLoad(){

        Switch sw = (Switch)findViewById(R.id.autoLogin);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        //스위치의 체크 이벤트를 위한 리스너 등록
        System.out.println(pref.getBoolean("attendance", false));
        if(pref.getBoolean("attendance", false)){
            sw.setChecked(true);
        }

        // 서비스 돌고 있는지 확인
//        if(isServiceRunning()){
//            sw.setChecked(true);
//        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                //체크 되었을때
                if(isChecked){
                    // 서비스 돌고 있는지 확인 값 - 저장
                    editor.putBoolean("attendance", true);
                    editor.commit();

                    /*
                    *
                    *
                    *       출근 알람 서비스
                    *
                    *
                    */
                    Intent Attendance = new Intent(MainActivity.this, Attendance.class);

//                    startService(Attendance);           // 서비스 테스트

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 00);
                    calendar.set(Calendar.MINUTE, 10);
                    calendar.set(Calendar.SECOND, 0);

                    PendingIntent slPendingIntent = PendingIntent.getService(MainActivity.this, 1, Attendance, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + nextT ,
                            AlarmManager.INTERVAL_DAY, slPendingIntent);

                    /*
                     *
                     *
                     *       퇴근 알람 서비스
                     *
                     *
                     */

                    Intent Leavework = new Intent(MainActivity.this, Leavework.class);

//                    startService(Leavework);           // 서비스 테스트

                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTimeInMillis(System.currentTimeMillis());
                    calendar2.set(Calendar.HOUR_OF_DAY, 00);
                    calendar2.set(Calendar.MINUTE, 30);
                    calendar2.set(Calendar.SECOND, 0);

                    PendingIntent slPendingIntent2 = PendingIntent.getService(MainActivity.this, 1, Leavework, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager2=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager2.setRepeating(alarmManager2.RTC_WAKEUP, calendar2.getTimeInMillis() + nextT,
                            alarmManager2.INTERVAL_DAY, slPendingIntent2);

                }else{
                    // 서비스 돌고 있는지 확인 값  - 저장
                    editor.putBoolean("attendance", false);
                    editor.commit();

                    /*
                     *
                     *
                     *       출근 알람 서비스 중지
                     *
                     *
                     */
                    Intent intent = new Intent(MainActivity.this, Attendance.class);

                    PendingIntent slPendingIntent = PendingIntent.getService(MainActivity.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.cancel(slPendingIntent);

                    /*
                     *
                     *
                     *       퇴근 알람 서비스 중지
                     *
                     *
                     */
                    Intent intent2 = new Intent(MainActivity.this, Leavework.class);

                    PendingIntent slPendingIntent2 = PendingIntent.getService(MainActivity.this, 1, intent2, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager2=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.cancel(slPendingIntent2);


                    //서비스 중지
                    Intent test = new Intent(MainActivity.this, Attendance.class);
                    stopService(test);
                }
            }
        });

    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (Attendance.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

}
