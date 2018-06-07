package com.furence.furence;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    Boolean Server = false;

//    int nextT = (24*60*60*1000);                    //다음날 부터
    int nextT = 0;


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

        boolean canDrawOverlays = Settings.canDrawOverlays(this);
        if (!canDrawOverlays) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 1234);
        }

        // 서버면 돌리기
        if(Server){
            Intent serverStart = new Intent(MainActivity.this, ServerStart.class);
            startService(serverStart);           // 서비스 테스트


        }

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
//        System.out.println(pref.getBoolean("attendance", false));
        if(pref.getBoolean("attendance", false)){
            sw.setChecked(true);
            START();
        }else{
            END();
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

                    START();

                }else{
                    // 서비스 돌고 있는지 확인 값  - 저장
                    editor.putBoolean("attendance", false);
                    editor.commit();

                    END();
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

    public void START(){

        /*
         *
         *
         *       출근 알람 서비스
         *
         *
         */
        System.out.println("_--------------------------자동 출퇴근 시스템 @David--------------------------- ");

        Intent GoLogin = new Intent(MainActivity.this, StartLogin.class);

//        startService(GoLogin);           // 서비스 테스트
//
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 16);
        calendar.set(Calendar.SECOND, 0);

        PendingIntent slPendingIntent;
        if(android.os.Build.VERSION.SDK_INT >= 26){
           slPendingIntent = PendingIntent.getForegroundService(MainActivity.this, 1, GoLogin, PendingIntent.FLAG_CANCEL_CURRENT);
        }else{
            slPendingIntent = PendingIntent.getService(MainActivity.this, 1, GoLogin, PendingIntent.FLAG_CANCEL_CURRENT);
        }


        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

//        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis() , slPendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + nextT ,
                AlarmManager.INTERVAL_DAY, slPendingIntent);


//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//                60*1000, slPendingIntent);

        /*
         *
         *
         *       퇴근 알람 서비스
         *
         *
         */
        Intent GoLogout = new Intent(MainActivity.this, StartLogout.class);

//                    startService(Leavework);           // 서비스 테스트

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, 20);
        calendar2.set(Calendar.MINUTE, 20 );
        calendar2.set(Calendar.SECOND, 0);


        PendingIntent slPendingIntent2;

        if(android.os.Build.VERSION.SDK_INT >= 26){
            slPendingIntent2 = PendingIntent.getForegroundService(MainActivity.this, 1, GoLogout, PendingIntent.FLAG_CANCEL_CURRENT);
        }else{
            slPendingIntent2 = PendingIntent.getService(MainActivity.this, 1, GoLogout, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        AlarmManager alarmManager2=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

//                    alarmManager2.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP , calendar2.getTimeInMillis() , slPendingIntent2);

        alarmManager2.setRepeating(alarmManager2.RTC_WAKEUP, calendar2.getTimeInMillis() + nextT,
                alarmManager2.INTERVAL_DAY, slPendingIntent2);

//        alarmManager2.setRepeating(alarmManager2.RTC_WAKEUP, System.currentTimeMillis() + (60 * 2000),
//                60*5000, slPendingIntent2);


    }

    public void END(){
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
