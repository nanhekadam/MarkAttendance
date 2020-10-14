package com.myaddictometer.markattendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        TextView privacy_text = findViewById(R.id.tv_privacy_policy);
        Spanned spanned = Html.fromHtml(getString(R.string.privacy_policy_text));
        privacy_text.setText(spanned);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adViewPrivacy);
        AdRequest adRequest = new AdRequest.Builder().build();
//                .addTestDevice("31CEEF2EE3DB8042E939736CA6E38142")
//                .build();
        mAdView.loadAd(adRequest);
    }

    public static Intent start(Context context) {
        return new Intent(context, PrivacyActivity.class);
    }
}
