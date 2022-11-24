package com.healtheworld.bugreport;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


// Test
// 앱 ID : ca-app-pub-3940256099942544~3347511713
// 광고단위 ID
// - 배너광고 : ca-app-pub-3940256099942544/6300978111
// - 전면광고 : ca-app-pub-3940256099942544/1033173712
// - 보상광고 : ca-app-pub-3940256099942544/5224354917


public class MainActivity extends AppCompatActivity {

    private final String TAG = "AutoTouch_FragmentIntro";

    private InterstitialAd mInterstitialAd; /* FullScreen */

    private ActivityResultLauncher<Intent> mLauncherAccess = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(checkStatusAccessibilityPermission()) {
                Log.d(TAG, "[Activity] LauncherAccess RESULT OK");
                checkAppPermission();
            } else {
                Log.d(TAG, "[Activity] LauncherAccess RESULT NG");
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(getApplicationContext(), initializationStatus -> {
            Log.d(TAG, "[AD] onInitializationComplete()");
        });


        loadADFullScreen();

        Button BtnAccess = (Button) findViewById(R.id.BtnAccess) ;
        BtnAccess.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAccessPermission();
            }
        });

        Button BtnAD = (Button) findViewById(R.id.BtnAD);
        BtnAD.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "[Activity] show AD()");
                ADVideoPlay();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mInterstitialAd != null) {
            // mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }
    }


    // =============================================================================================
    // AD
    // =============================================================================================

    public void loadADFullScreen() {
        Log.d(TAG, "[AD] loadADFullScreen()");

        if(mInterstitialAd != null) {
            Log.d(TAG, "[AD] loadADFullVideo() mInterstitialAd is Not Null");
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.Admob_FULLID), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                Log.i(TAG, "[AD] onAdLoaded");
                mInterstitialAd = interstitialAd; // The mInterstitialAd reference will be null until an ad is loaded.

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "[AD] Ad dismissed fullscreen content.");
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        mInterstitialAd = null;
                        // m_callback.CB_ADOpenOverlayMenu();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        Log.e(TAG, "[AD] Ad failed to show fullscreen content.");
                        // Called when ad fails to show.
                        mInterstitialAd = null;
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d(TAG, loadAdError.toString());
                mInterstitialAd = null;
            }
        });

    }

    public void ADVideoPlay() {
        Log.d(TAG, "[AD] Play()");
        if(mInterstitialAd != null) {
            Log.d(TAG, "[AD] mInterstitialAd.show()");
            mInterstitialAd.show(this);
        } else {
            Log.d(TAG, "[AD] The interstitial wasn't loaded yet.");
            // m_callback.CB_ADOpenOverlayMenu();
        }
    }


    // =============================================================================================
    // Service
    // =============================================================================================

    private void checkAppPermission() {
        Log.d(TAG, "[Activity] checkAppPermission()");

        boolean StatusAccess = checkStatusAccessibilityPermission();

        Log.d(TAG, "[Activity] checkAppPermission Access:" + StatusAccess);

        Button BtnAccess = (Button) findViewById(R.id.BtnAccess);
        Button BtnAD = (Button) findViewById(R.id.BtnAD);
        TextView TextClick = (TextView) findViewById(R.id.TxtViewClick4);



        if(StatusAccess) {
            Log.d(TAG, "[Activity] All Status Done");
            BtnAccess.setVisibility(View.GONE);
            TextClick.setVisibility(View.GONE);
            BtnAD.setVisibility(View.VISIBLE);
        } else {
            BtnAccess.setVisibility(View.VISIBLE);
            TextClick.setVisibility(View.VISIBLE);
            BtnAD.setVisibility(View.GONE);
        }
    }












    public void requestAccessPermission() {
        Log.d(TAG, "[Activity] requestAccessPermission()");
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        mLauncherAccess.launch(intent);
    }

    public boolean checkStatusAccessibilityPermission() {
        Log.d(TAG, "[Activity] checkStatusAccessibilityPermission()");
        String className = ATService.class.getName();
        return isServiceRunning(className);
    }

    public Boolean isServiceRunning(String class_name) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(class_name.equals(serviceInfo.service.getClassName())) {
                if(getPackageName().equals(serviceInfo.service.getPackageName())) {
                    Log.w(TAG, "[Activity] class_name : [" + class_name + "] is Alive!");
                    return true;
                }
            }
        }
        return false;
    }
}