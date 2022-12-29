package tbc.uncagedmist.rationcard.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import tbc.uncagedmist.rationcard.Utility.AppOpenManager;
import tbc.uncagedmist.rationcard.Utility.MyNetworkReceiver;

public class MyApplicationClass extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Activity mActivity;
    MyNetworkReceiver mNetworkReceiver;

    @SuppressLint("StaticFieldLeak")
    private static AppOpenManager appOpenManager;

    private static MyApplicationClass instance;

    private boolean showAds = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppLovinSdk.getInstance(instance).setMediationProvider("max");
        AppLovinSdk.initializeSdk(instance, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                // AppLovin SDK is initialized, start loading ads
            }
        } );

        AudienceNetworkAds.initialize(instance);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        if (showAds)    {
            appOpenManager = new AppOpenManager(instance);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                mNetworkReceiver = new MyNetworkReceiver();
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivity = activity;
                registerNetworkBroadcastForLollipop();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mActivity = null;
                unregisterReceiver(mNetworkReceiver);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    private void registerNetworkBroadcastForLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public static MyApplicationClass getInstance() {
        return instance;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }
}
