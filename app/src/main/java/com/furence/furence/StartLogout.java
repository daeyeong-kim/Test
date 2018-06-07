package com.furence.furence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class StartLogout extends Service {


    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    public static final String ACTION_PLAY = "ACTION_PLAY";


    SharedPreferences pref;
    SharedPreferences.Editor editor;


    String id = "";
    String passwd = "";

    public StartLogout() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        pref = getApplicationContext().getSharedPreferences("pref", MainActivity.MODE_PRIVATE);

        id = pref.getString("id", "");
        passwd = pref.getString("pw", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(android.os.Build.VERSION.SDK_INT >= 26){
            startForegroundService();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("START LOGOUT TEST ::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    HttpClient client = new DefaultHttpClient();

                    HttpGet get = new HttpGet("http://183.101.181.140:8888/?out="+id+"&pass="+passwd+"&");

                    String USER_AGENT = "Mozila/5.0";

                    System.out.println("LOGOUT GO");

                    //agent 정보 설정
                    get.addHeader("User-Agent", USER_AGENT);

                    //get 요청
                    HttpResponse httpResponse = client.execute(get);

                    System.out.println("::GET Response Status::");

                    //response의 status 코드 출력
                    System.out.println(httpResponse.getStatusLine().getStatusCode());

                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        Thread.sleep(4000);
                        // 서비스 죽이기
                        Intent destory = new Intent(getBaseContext(), StartLogin.class);
                        stopService(destory);
                    }else{
                        while(true) {
                            try {
                                System.out.println("WHILE::::::::::::::::::::::::::::::::::::");
                                //get 요청
                                HttpClient client2 = new DefaultHttpClient();

                                HttpGet get2 = new HttpGet("http://183.101.181.140:8888/?out="+id+"&pass="+passwd+"&");

                                String USER_AGENT2 = "Mozila/5.0";

                                System.out.println("LOGOUT GO");

                                //agent 정보 설정
                                get2.addHeader("User-Agent", USER_AGENT2);

                                //get 요청
                                HttpResponse httpResponse2 = client2.execute(get);
                                System.out.println(httpResponse2.getStatusLine().getStatusCode());
                                if (httpResponse2.getStatusLine().getStatusCode() == 200) {
                                    break;
                                }else{
                                    System.out.println("조금있다가 실행::::::::::::::::::::::::::::::::::::");
                                    Thread.sleep(12000);
                                }
                            } catch (Exception e) {

                            }
                        }

                        Intent destory = new Intent(getBaseContext(), StartLogout.class);
                        stopService(destory);
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return START_STICKY;
    }


    public void startForegroundService(){
        try{
            if(android.os.Build.VERSION.SDK_INT >= 26){
                Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

//            // Create notification default intent.
//            Intent intent = new Intent();
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channelMessage = new NotificationChannel("furence_channel", "furence_channel", android.app.NotificationManager.IMPORTANCE_DEFAULT);
                channelMessage.setDescription("channel description");
                channelMessage.enableLights(true);
                channelMessage.setLightColor(Color.GREEN);
                channelMessage.enableVibration(true);
                channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
                channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(channelMessage);
//
//            // Create notification builder.
                Notification.Builder builder = new Notification.Builder(this, "furence_channel")
                        .setContentTitle("Furence")
                        .setContentText("LOGIN SUCCESS")
                        .setAutoCancel(true);

                // Start foreground service.
                startForeground(1, builder.build());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
