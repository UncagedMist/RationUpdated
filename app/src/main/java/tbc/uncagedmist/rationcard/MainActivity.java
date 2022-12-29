package tbc.uncagedmist.rationcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.rvadapter.AdmobNativeAdAdapter;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tbc.uncagedmist.rationcard.Adapter.StateAdapter;
import tbc.uncagedmist.rationcard.Common.Common;
import tbc.uncagedmist.rationcard.Common.MyApplicationClass;
import tbc.uncagedmist.rationcard.Model.State;
import tbc.uncagedmist.rationcard.Remote.IMyAPI;

public class MainActivity extends AppCompatActivity {

    private FrameLayout adContainerView;
    private AdView adView;

    RecyclerView recyclerView;
    MaterialSearchBar searchBar;

    IMyAPI iMyAPI;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    NoInternetDialog noInternetDialog;

    List<State> localDataSource = new ArrayList<>();

    StateAdapter searchAdapter, adapter;

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

        setContentView(R.layout.activity_main);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        dialog = new ProgressDialog(this);

        iMyAPI = Common.getAPI();

        adContainerView = findViewById(R.id.bannerContainer);

        recyclerView = findViewById(R.id.recyclerState);
        searchBar = findViewById(R.id.edtState);

        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.ADMOB_BANNER));
        adContainerView.addView(adView);

        if (MyApplicationClass.getInstance().isShowAds())   {
            loadBanner();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.shareApp(MainActivity.this);
            }
        });

        loadStates();

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                startSearch(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        List<State> result = new ArrayList<>();

        String name = text.toString();

        for (State state : localDataSource)   {
            if (state.name.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))    {
                result.add(state);
            }
        }
        searchAdapter = new StateAdapter(this, result);
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadStates() {
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        compositeDisposable.add(
                iMyAPI.getStates()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<State>>() {
                                       @Override
                                       public void accept(List<State> stateList) throws Exception {
                                           displayStates(stateList);
                                           dialog.dismiss();
                                       }
                                   }
                        )
        );
    }


    private void displayStates(List<State> stateList) {
        localDataSource = stateList;
        adapter = new StateAdapter(this,stateList);

        if (MyApplicationClass.getInstance().isShowAds())    {
            AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                            getString(R.string.ADMOB_NATIVE),
                            adapter,
                            "small")
                    .adItemInterval(3)
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
    public void onBackPressed() {
        new FancyAlertDialog.Builder(MainActivity.this)
                .setTitle("Exit this App?")
                .setBackgroundColor(Color.parseColor("#303F9F"))  //Don't pass R.color.colorvalue
                .setMessage("You're exiting this App!")
                .setNegativeBtnText("Exit")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Rate")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                .OnPositiveClicked(() ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName()))))
                .OnNegativeClicked(() -> {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                })
                .build();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}