package com.furence.furence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Calendar;

public class Attendance extends Service {

    WindowManager wm;
    View mView;

    WebView webview;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String id = "";
    String passwd = "";

    public Attendance() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Calendar calendar = Calendar.getInstance();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Log.i("day_of_time",String.valueOf(dayOfWeek));

        if(dayOfWeek == 7 || dayOfWeek == 1){
            // 서비스 죽이기
            serviceDestroy();
        }


        pref = getApplicationContext().getSharedPreferences("pref", MainActivity.MODE_PRIVATE);

        id = pref.getString("id", "");
        passwd = pref.getString("pw", "");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(300,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.LEFT | Gravity.TOP;
        mView = inflater.inflate(R.layout.web_browser , null);
        webview = (WebView) mView.findViewById(R.id.webview);

        // 웹뷰 설정

        Handler handler = new Handler();
        AutoClickJavascriptInterface javascriptInterface = new AutoClickJavascriptInterface(handler);

        webview.setWebViewClient(new WebViewClient(){

        });

        WebSettings set = webview.getSettings();

        webview.addJavascriptInterface(javascriptInterface,"android");

        set.setJavaScriptEnabled(true);
        set.setJavaScriptCanOpenWindowsAutomatically(true);

        webview.setWebContentsDebuggingEnabled(true);

        wm.addView(mView, params);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    synchronizedLoadUrl("https://loginc.ecounterp.com");
                    synchronizedLoadUrl("javascript:" + querySelector("#com_code")+".value = 64583");
                    synchronizedLoadUrl("javascript:void(" + querySelector("#id")+".value = '"+id+"');");
                    synchronizedLoadUrl("javascript:void(" + querySelector("#passwd")+".value = '"+passwd+"');");
                    synchronizedLoadUrl("javascript:void(" + querySelector("#logintimeinck")+".checked = true );");
//                    System.out.println("ID:::::::::::::::::::::::::" + id);
//                    System.out.println("PASSWARD:::::::::::::::::::::::::" + passwd);
                    synchronizedLoadUrl("javascript:void(" + querySelector("#save")+".click());");

                    // 서비스 죽이기
                    Intent destory = new Intent(getBaseContext(), Attendance.class);
                    stopService(destory);
                }catch(Exception e){

                }
            }
        });
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webview.destroy();
        webview = null;
        wm.removeView(mView);
    }


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
        Thread.sleep(2000);
        Thread.sleep(randomNum);
    }

    private String querySelector(String paramString){
        return querySelectorAll(paramString) + "[0]";
    }

    private String querySelectorAll(String paramString){
        return "document.querySelectorAll('" + paramString.replace("'" , "\\'") + "')";
    }

    public void serviceDestroy(){
        // 서비스 죽이기
        Intent destory = new Intent(getBaseContext(), Attendance.class);
        stopService(destory);
    }
}
