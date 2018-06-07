package com.furence.furence;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class ServerStart extends Service {

    WindowManager wm;
    View mView;

    WebView webview;
    ServerSocket httpServerSocket;

    String msgLog = "";


    public ServerStart() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HttpServerThread httpServerThread = new HttpServerThread();
        httpServerThread.start();

    }


    /*
     *
     *       서버
     *
     */
    private class HttpServerThread extends Thread {

        static final int HttpServerPORT = 8888;

        @Override
        public void run() {
            Socket socket = null;

            try {
                httpServerSocket = new ServerSocket(HttpServerPORT);

                while(true){
                    socket = httpServerSocket.accept();

                    HttpResponseThread httpResponseThread;
                    // 서비스 돌고 있는지 확인
                    if(isServiceRunning()) {
                        httpResponseThread =
                                new HttpResponseThread(
                                        socket,
                                        "Service is Running. \n Please use it later.");
                    }else{
                        httpResponseThread =
                                new HttpResponseThread(
                                        socket,
                                        "SUCCESS.");

                    }
                    httpResponseThread.start();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }




    public class HttpResponseThread extends Thread {

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        Socket socket;
        String h1;

        HttpResponseThread(Socket socket, String msg){
            this.socket = socket;
            h1 = msg;
        }

        @Override
        public void run() {
            BufferedReader is;
            PrintWriter os;
            final String request;


            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                request = is.readLine();

                os = new PrintWriter(socket.getOutputStream(), true);

                String response =
                        "<html><head></head>" +
                                "<body>" +
                                "<h1>" + h1 + "</h1>" +
                                "</body></html>";
                if(isServiceRunning()) {
                    os.print("HTTP/1.0 404" + "\r\n");
                }else{
                    os.print("HTTP/1.0 200" + "\r\n");
                }

                os.print("Content type: text/html" + "\r\n");
                os.print("Content length: " + response.length() + "\r\n");
                os.print("\r\n");
                os.print(response + "\r\n");
                os.flush();
                socket.close();

            msgLog = "Request of " + request
                    + " from " + socket.getInetAddress().toString() + "\n";

                System.out.print(msgLog);

                pref = getSharedPreferences("pref", MODE_PRIVATE);
                editor = pref.edit();

                System.out.println("REQUEST ::::::::::::"+request);
                if(request != null){

                    // 출근 할때
                    if(request.contains("in")){

                        String[] k = request.split("&");
                        try{
                            for(int i = 0 ;i < k.length; i++){
                                String[] param = k[i].split("=");
//                                System.out.println("=================================================== "+  URLDecoder.decode(param[1] , "utf8"));
                                if(i == 0 ){
                                    editor.putString("id", URLDecoder.decode(param[1] , "utf8"));
                                }else if(i == 1){
                                    editor.putString("pw", URLDecoder.decode(param[1] , "utf8"));
                                }
                            }
                            editor.commit();

                            if(isServiceRunning()) {
                                return;
                            }else{
                                Intent GoLogin = new Intent(ServerStart.this, Attendance.class);
                                startService(GoLogin);           // 서비스 테스트

                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }


                    // 출근 할때
                    if(request.contains("out")){

                        String[] k = request.split("&");
                        try{
                            for(int i = 0 ;i < k.length; i++){
                                String[] param = k[i].split("=");
//                                System.out.println("=================================================== "+  URLDecoder.decode(param[1] , "utf8"));
                                if(i == 0 ){
                                    editor.putString("id", URLDecoder.decode(param[1] , "utf8"));
                                }else if(i == 1){
                                    editor.putString("pw", URLDecoder.decode(param[1] , "utf8"));
                                }
                            }
                            editor.commit();

                            if(isServiceRunning()) {
                                return;
                            }else{
                                Intent GoLogout = new Intent(ServerStart.this, Leavework.class);
                                startService(GoLogout);           // 서비스 테스트
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }





                }

//            MainActivity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    if(request != null){
//
//                        // 출근 할때
////                        if(request.contains("in")){
////
////                            String[] k = request.split("&");
////                            try{
////                                for(int i = 0 ;i < k.length; i++){
////                                    String[] param = k[i].split("=");
////                                    System.out.println("=================================================== "+  URLDecoder.decode(param[1] , "utf8"));
////                                    if(i == 0 ){
////                                        editor.putString("id", param[1]);
////                                    }else if(i == 1){
////                                        editor.putString("pw", param[1]);
////                                    }
////                                    editor.commit();
////
////                                    Intent Attendance = new Intent(MainActivity.this, Attendance.class);
////                                    startService(Attendance);
////                                }
//////                                    System.out.println( URLDecoder.decode(k[1] , "utf8"));
////
////                            }catch(Exception e){
////
////                            }
////                        }
//
//                        // 퇴근 할때
//
////                        if(request.contains("out")){
////
////                            String[] k = request.split("&");
////                            try{
////                                for(int i = 0 ;i < k.length; i++){
////                                    String[] param = k[i].split("=");
////                                    System.out.println("=================================================== "+  URLDecoder.decode(param[1] , "utf8"));
////                                    if(i == 0 ){
////                                        editor.putString("id", param[1]);
////                                    }else if(i == 1){
////                                        editor.putString("pw", param[1]);
////                                    }
////                                    editor.commit();
////
////                                    Intent Leavework = new Intent(MainActivity.this, Leavework.class);
////
////                                    startService(Leavework);
////                                }
//////                                    System.out.println( URLDecoder.decode(k[1] , "utf8"));
////
////                            }catch(Exception e){
////
////                            }
////                        }
//
//                    }
//                }
//            });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return;
        }
    }


    /*
     *
     *       서버
     *
     */


    private void synchronizedLoadUrl(final String paramString) throws Exception {
        synchronizedLoadUrl( paramString , 5000);
    }

    private void synchronizedLoadUrl(final String paramString , int randomvalue) throws Exception {
        webview.post(new Runnable() {
            @Override
            public void run() {
                webview.loadUrl(paramString);
            }
        });

        int randomNum = (int) (Math.random() * randomvalue);
        Thread.sleep(5000);
        Thread.sleep(randomNum);
    }

    private String querySelector(String paramString){
        return querySelectorAll(paramString) + "[0]";
    }

    private String querySelectorAll(String paramString){
        return "document.querySelectorAll('" + paramString.replace("'" , "\\'") + "')";
    }


    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (Attendance.class.getName().equals(service.service.getClassName()))
                return true;
            if (Leavework.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

}
