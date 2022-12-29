package tbc.uncagedmist.rationcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import am.appwise.components.ni.NoInternetDialog;
import tbc.uncagedmist.rationcard.Common.Common;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 5152;

    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        checkAppUpdate();

        setContentView(R.layout.activity_splash);

        noInternetDialog = new NoInternetDialog.Builder(this).build();


        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(SplashActivity.this))  {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
                else    {
                    Toast.makeText(SplashActivity.this, "Please Check your Internet Connection...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkAppUpdate() {
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
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
                                SplashActivity.this,
                                REQUEST_CODE
                        );
                    }
                    catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}