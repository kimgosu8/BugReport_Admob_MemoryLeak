package com.healtheworld.bugreport;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;

import android.graphics.Point;
import android.os.Build;
import android.util.Log;

import android.view.Display;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;


public class ATService extends AccessibilityService {

    private final String TAG = "AutoTouch_Service";

    private AccessibilityServiceInfo minfo;

    private int mScreenWidth;
    private int mScreenHeight;

    // =============================================================================================
    // Base Function
    // =============================================================================================

    public ATService() {
        Log.d(TAG, "[Service] Contstructor()");
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "[Service] onRebind()");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "[Service] onCreate()");

        setScreenResolution();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    doSleep(5000);
                }
            }
        }).start();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "[Service] onServiceConnected()");

        Toast.makeText(getApplicationContext(), "Trun On AccessibilityService", Toast.LENGTH_SHORT).show();

        if(minfo == null) {
            minfo = getServiceInfo();
            minfo.packageNames = new String[]{getPackageName()};
            this.setServiceInfo(minfo);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean unbind = super.onUnbind(intent);
        Log.d(TAG, "[Service] onUnbind() : " + unbind);

        this.setServiceInfo(null);

        if(minfo != null) {
            minfo.packageNames = null;
            minfo = null;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "[Service] onDestroy()");
        Toast.makeText(getApplicationContext(), "Trun Off AccessibilityService", Toast.LENGTH_SHORT).show();

        doSleep(500);

        System.exit(0);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "[Service] onInterrupt()");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getPackageName() == null || !event.getPackageName().equals(getPackageName()) || event.getEventType() != AccessibilityEvent.TYPE_ANNOUNCEMENT) {
            Log.e(TAG, "ERROR [Service] onAccessibilityEvent() : " + event);
            return;
        }

        Log.d(TAG, "[Service] onAccessibilityEvent() !!");
    }

    private void setScreenResolution() {
        Log.d(TAG, "[Service] setScreenResolution()");
        WindowManager winmng = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = winmng.getCurrentWindowMetrics();
            mScreenWidth = windowMetrics.getBounds().width();
            mScreenHeight = windowMetrics.getBounds().height();
        } else {
            Display display = winmng.getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            mScreenWidth = size.x;
            mScreenHeight = size.y;
        }
        Log.d(TAG, "[Service] Width=" + mScreenWidth + ", Height=" + mScreenHeight);
    }

    public void doSleep(int ms) {
        try {
            // Log.d(TAG, "[Service] doSleep : " + ms + "ms");
            Thread.sleep(ms);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
