package com.furence.furence;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class AutoClickJavascriptInterface {
    public static final String INTERFACE_NAME = "android";

    private Handler handler;
    private BlockingQueue<String> onResultQueue = new ArrayBlockingQueue(1);

    public AutoClickJavascriptInterface(Handler paramHandler){ this.handler = paramHandler; }


    @JavascriptInterface
    public void onResult(String paramString) { this.onResultQueue.add(paramString); }

    public String requestHTML(WebView paramWebView) throws Exception
    {
        return requestResult(paramWebView , "document.getElementsByTagName('html')[0].innerHTML");
    }

    public String requestResult(final WebView paramWebView , final String paramString) throws Exception
    {
        this.onResultQueue.clear();
        this.handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        paramWebView.loadUrl("javascript:void(window.android.onResult(" + paramString + "));");
                    }
                }
        );

        return ((String)this.onResultQueue.poll(60000L , TimeUnit.MILLISECONDS)).toString();
    }


}
