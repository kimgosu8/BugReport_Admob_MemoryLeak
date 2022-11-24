# BugReport_Admob_MemoryLeak

* com.google.android.gms:play-services-ads:21.3.0
* Android OS level : Android 12
* Device : Samsung Galaxy S10 5g / Samsung Galaxy S20Fe
* App Package Name : Attached Sample App


There is a memory leak problem when using the latest version of Admob SDK.

The attached sample code has one each of Activity and Accessibility Service.

The problem is to enable Accessibility Service and a memory leak occurs when you close an activity after viewing an advertisement in it.

The latest version of Leakcanary was used to check for memory leaks.

Admob SDK 19.8.0 has no problem.
A memory leak occurs from Admob SDK 20.5.0 to 21.3.0.


I want to use 19.8.0 to avoid a memory leak, but the app update does not work in "Google Console".

Create an app with 19.8.0
When I try to update the app in "Google Console", the problem occurs in "App Content" in "Google Console".

It seems that you should use 20.5.0 or higher as per the recent Admob policy.


My app does not use AD_ID.

However, even if I delete the com.google.android.gms.permission.AD_ID permission,
Even using the old SDK 19.8.0,
the app won't update.


I made a sample app and attached it in here.
You can check for memory leaks in the Android Studio "Run" window.

The order is as follows.

1. Activate the accessibility service by pressing the Accessibility button.
2. If you press the back button to return to the app screen, the "Show AD" button appears.
3. Watch the ad, close the ad, and exit the app (press the latest apps button on your device and then close all).
4. You can check the memory leak on the log screen.


The devices tested are Galaxy S10, S20Fe and it happens on Android 12.

If you don't use the accessibility service, of course there won't be a memory leak, but
A memory leak occurs if only Activity is closed after accessibility service is activated.

In order to eliminate the memory leak by using SDK 20.5.0, many efforts were made, such as using weakreference or getApplicationContext.

The problem is that 19.8.0 supports the code below, but from 20.5.0 it does not support the code below.
mInterstitialAd.setAdListener(null);
It looks like there is a memory leak happening here.
