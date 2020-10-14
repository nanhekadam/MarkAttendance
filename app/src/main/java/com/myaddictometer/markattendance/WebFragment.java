package com.myaddictometer.markattendance;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class WebFragment extends Fragment {

    TinyDB quickDatabase;
    private WebView main;
    private ProgressBar progressBar;

    public static Fragment getFragmentInstance(WebActivity webActivity) {
        WebFragment fragment = new WebFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quickDatabase = new TinyDB(getActivity());
        quickDatabase.putInt(getString(R.string.key_login_attempt), 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = view.findViewById(R.id.adView_web);
        AdRequest adRequest = new AdRequest.Builder().build();
//                .addTestDevice("31CEEF2EE3DB8042E939736CA6E38142")
//                .build();
        mAdView.loadAd(adRequest);

        String url = "http://reppi-puc2.rites.com:55100/irj/portal";
        main = view.findViewById(R.id.web_main);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setMax(100);

        main.getSettings().setJavaScriptEnabled(true);
        main.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        main.getSettings().setDomStorageEnabled(true);
        main.getSettings().setSupportZoom(true);
        main.getSettings().setSupportMultipleWindows(true);
        main.loadUrl(url);

        main.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                main.scrollTo(0, 0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                final String password = quickDatabase.getString(getString(R.string.key_password));
                final String username = quickDatabase.getString(getString(R.string.key_username));

                if (!(username.equals("") || password.equals(""))) {
                    final String js = "javascript:" +
                            "document.getElementById('logonpassfield').value = '" + password + "';" +
                            "document.getElementById('logonuidfield').value = '" + username + "';" +
                            "document.getElementsByName('uidPasswordLogon')[0].click()";

                    if (quickDatabase.getInt(getString(R.string.key_login_attempt)) == 1) {
//                        if (Build.VERSION.SDK_INT >= 19) {
//                            view.evaluateJavascript(js, new ValueCallback<String>() {
//                                @Override
//                                public void onReceiveValue(String s) {
//
//                                }
//                            });
//                        } else {
                        view.loadUrl(js);
                        main.scrollTo(0, 0);
                        quickDatabase.putInt(getString(R.string.key_login_attempt), 2);
//                        }
                    }
                } else {
                    Snackbar.make(getView(), R.string.login_details_incomplete_toast_alert, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                main.scrollTo(0, 0);
                view.loadUrl(url);
                return true;
            }
        });

//        main.setWebViewClient(new WebViewClient());

        main.setWebChromeClient(new WebChromeClient() {


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {

                WebView newWebView = new WebView(getActivity());
                newWebView.getSettings().setJavaScriptEnabled(true);
//                newWebView.getSettings().setSupportZoom(true);
//                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                newWebView.getSettings().setSupportMultipleWindows(true);
                newWebView.getSettings().setLoadWithOverviewMode(true);
//                newWebView.getSettings().setUseWideViewPort(true);

                main.scrollTo(0, 0);
                view.addView(newWebView);

                ViewGroup.LayoutParams params = newWebView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                newWebView.setLayoutParams(params);

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                return true;
            }
        });



        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        main.clearCache(true);
    }
}
