package tbc.uncagedmist.rationcard.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import tbc.uncagedmist.rationcard.Model.Item;
import tbc.uncagedmist.rationcard.Model.State;
import tbc.uncagedmist.rationcard.R;
import tbc.uncagedmist.rationcard.Remote.IMyAPI;
import tbc.uncagedmist.rationcard.Remote.RetrofitClient;
import tbc.uncagedmist.rationcard.SplashActivity;

public class Common {
    public static final String WIN_URL = "https://894.win.qureka.com";

    public static final String BASE_URL = "https://biharpratidin.com/AndroidApp/Admin/";

    public static State CURRENT_STATE;
    public static Item CURRENT_ITEM;

    public static final String PRIVACY_URL = "https://docs.google.com/document/d/1N9dzhvQlIAV3v3357SOrFBwZ3Y3k_A_K_mFbkEoQOqA/edit?usp=sharing";

    public static IMyAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IMyAPI.class);
    }


    public static boolean isConnectedToInternet(Context context)    {

        ConnectivityManager connectivityManager = (
                ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null)    {

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null)   {

                for (int i = 0; i <info.length;i++)   {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)  {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void shareApp(Context context)    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String message = "Never Miss A Thing About Ration Card. Install "+context.getString(R.string.app_name)+" App and Stay Updated! \n " +
                "https://play.google.com/store/apps/details?id="+context.getPackageName();
        intent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(intent, "Share "+context.getString(R.string.app_name)+" App Using"));
    }

    public static void checkAppUpdate(Context context) {
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))    {

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                result,
                                AppUpdateType.IMMEDIATE,
                                (Activity) context,
                                512
                        );
                    }
                    catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
