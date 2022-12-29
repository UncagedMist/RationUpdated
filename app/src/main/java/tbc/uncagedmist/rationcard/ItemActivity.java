package tbc.uncagedmist.rationcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.rvadapter.AdmobNativeAdAdapter;

import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tbc.uncagedmist.rationcard.Adapter.ItemAdapter;
import tbc.uncagedmist.rationcard.Adapter.StateAdapter;
import tbc.uncagedmist.rationcard.Common.Common;
import tbc.uncagedmist.rationcard.Common.MyApplicationClass;
import tbc.uncagedmist.rationcard.Model.Item;
import tbc.uncagedmist.rationcard.Model.State;
import tbc.uncagedmist.rationcard.Remote.IMyAPI;

public class ItemActivity extends AppCompatActivity {

    private FrameLayout adContainerView;
    private AdView adView;

    RecyclerView recyclerView;

    IMyAPI iMyAPI;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextView txtName;

    NoInternetDialog noInternetDialog;

    ProgressDialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_item);

        dialog = new ProgressDialog(ItemActivity.this);

       noInternetDialog = new NoInternetDialog.Builder(this).build();


        iMyAPI = Common.getAPI();


        adContainerView = findViewById(R.id.bannerContainer);
        recyclerView = findViewById(R.id.recyclerView);
        txtName = findViewById(R.id.txtName);

        txtName.setText(Common.CURRENT_STATE.name);

        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.ADMOB_BANNER));
        adContainerView.addView(adView);

        if (MyApplicationClass.getInstance().isShowAds())   {
            loadBanner();
        }

        findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.shareApp(ItemActivity.this);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadItems();
    }

    private void loadItems() {
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        compositeDisposable.add(
                iMyAPI.getItems(Common.CURRENT_STATE.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Item>>() {
                                       @Override
                                       public void accept(List<Item> items) throws Exception {
                                           displayItems(items);
                                           dialog.dismiss();
                                       }
                                   }
                        )
        );
    }

    private void displayItems(List<Item> items) {
        ItemAdapter adapter = new ItemAdapter(this,items);

        if (MyApplicationClass.getInstance().isShowAds())    {
            AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                            getString(R.string.ADMOB_NATIVE),
                            adapter,
                            "small")
                    .adItemInterval(2)
                    .build();

            recyclerView.setAdapter(admobNativeAdAdapter);
        }
        else {
            recyclerView.setAdapter(adapter);
        }
    }

    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder()
                        .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}