package com.newspaper.allbangla;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ViewData extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Bundle bundle;
    private String name,link;

    ProgressBar pbar;
    SwipeRefreshLayout swipe;
    WebView webView;
    String currentURL;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_view_data);
        bundle= getIntent().getExtras();
        if (bundle!= null){

            name= bundle.getString("namenews");
            link = bundle.getString("linknews");
            currentURL = link;

            setTitle(""+name);
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pbar =  findViewById(R.id.pB1);
        swipe =  findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        webView= findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.setInitialScale(1);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setWebViewClient(new MyWebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && pbar.getVisibility() == ProgressBar.GONE){
                    pbar.setVisibility(ProgressBar.VISIBLE);
//                    txtview.setVisibility(View.VISIBLE);
                }

                pbar.setProgress(progress);
                if(progress == 100) {
                    pbar.setVisibility(ProgressBar.GONE);
                    if (swipe.isRefreshing()) {
                        swipe.setRefreshing(false);
                    }
//                    txtview.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewData.this);
                builder.setMessage("Reload!!!")
                        .setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onBackPressed();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                dialog.cancel();
                                checkNetwork();
                            }
                        });
                final AlertDialog alert = builder.create();


                alert.show();
            }
        });


        checkNetwork();
    }


    @Override
    public void onRefresh() {
        swipe.setRefreshing(true);
        ReLoadWebView(currentURL);
    }
    private void ReLoadWebView(String currentURL) {
        webView.loadUrl(currentURL);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            Toast.makeText(getApplicationContext(), "swipe: "+swipe.isRefreshing(),Toast.LENGTH_LONG).show();

            if (swipe.isRefreshing()) {
                swipe.setRefreshing(false);
            }
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            currentURL = url;
//            if (Uri.parse(url).getHost().equals(""+ linksRes[restypepos])) {
//                // This is my web site, so do not override; let my WebView load the page
//                return false;
//            }
//             Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void checkNetwork(){
        if (isNetworkAvailable()) {
            webView.loadUrl(""+currentURL);
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewData.this);
            builder.setMessage("No Wifi/Data Connection!!!")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onBackPressed();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            dialog.cancel();
                            checkNetwork();
                        }
                    });
            final AlertDialog alert = builder.create();


            alert.show();


        }
    }


    public boolean checkForBackAction(){
        if(webView.canGoBack()){
            // If web view have back history, then go to the web view back history
            webView.goBack();
//            Snackbar.make(mCLayout,"Go to back history",Snackbar.LENGTH_LONG).show();
            return false;
        }
        else {
            return true;
        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
