package com.myaddictometer.markattendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class WebActivity extends AppCompatActivity {

    WebView myWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        myWebView = findViewById(R.id.web_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_web);
        if (fragment == null){
            fragment = WebFragment.getFragmentInstance(WebActivity.this);
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_web, fragment)
                    .commit();
        }
    }

    public static Intent start(Context context){
        return new Intent(context, WebActivity.class);
    }

//    @Override
//    public void onBackPressed() {
//        if (myWebView.canGoBack()) {
//            myWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }
}
