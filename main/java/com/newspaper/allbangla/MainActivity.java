package com.newspaper.allbangla;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.newspaper.allbangla.volley.AppController;
import com.newspaper.allbangla.volley.Config_URL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean activityStatus = true;

    long waitingTime= 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Home_Data fragobj = new Home_Data();
        Bundle bundle = new Bundle();
        bundle.putString("from_come", "homedata");
        fragobj.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragobj, "homedata");
        transaction.addToBackStack(null);
        transaction.commit();
        setTitle(getString(R.string.home));



        getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (f != null) {
                    updateActionBarTitle(f);
                }

            }
        });

   }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Home_Data myFragment = (Home_Data) getSupportFragmentManager().findFragmentByTag("homedata");

            if (myFragment != null && myFragment.isVisible()) {
                // add your code here
                if (doubleBackToExitPressedOnce) {
                    Intent intent2 = new Intent(Intent.ACTION_MAIN);
                    intent2.addCategory(Intent.CATEGORY_HOME);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);
//            Process.killProcess(Process.myPid());
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }else {
                super.onBackPressed();
            }
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String newToken = instanceIdResult.getToken();
                if (newToken == null) {

                } else {
                    sendRegistrationToServer(newToken);
                }
                Log.e("newToken", newToken);

            }
        });

        MobileAds.initialize(this, ""+getString(R.string.app_id_admob));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_app_id));

        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mInterstitialAd.isLoaded() && activityStatus){
                    mInterstitialAd.show();
                    waitingTime= waitingTime*2;
                }else {
                    requestNewInterstitial();
                }

            }
        },waitingTime);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);



        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }


    }

    private void requestPermissionsHere() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED ) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.




        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Home_Data fragobj = new Home_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "homedata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "homedata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.home));


            // Handle the camera action
        }else if (id == R.id.nav_khela) {
            Khela_Data fragobj = new Khela_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "kheladata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "kheladata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.khela_dhula));


            // Handle the camera action
        }else if (id == R.id.nav_shikkha) {
            Shikkha_Data fragobj = new Shikkha_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "kheladata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "shikkhadata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.khela_dhula));


            // Handle the camera action
        }else if (id == R.id.nav_banijjo) {
            Banijjo_Data fragobj = new Banijjo_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "kheladata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "banijjodata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.khela_dhula));


            // Handle the camera action
        }else if (id == R.id.nav_chakri) {
            Chakri_Data fragobj = new Chakri_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "kheladata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "chakridata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.khela_dhula));


            // Handle the camera action
        }else if (id == R.id.nav_binodon) {
            binodon_Data fragobj = new binodon_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "binodondata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "binodondata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.binodon));


            // Handle the camera action
        }else if (id == R.id.nav_bideshe) {
            Bidesh_Data fragobj = new Bidesh_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "bideshdata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "bideshdata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.bidesh));


            // Handle the camera action
        }else if (id == R.id.nav_online) {
            Online_Data fragobj = new Online_Data();
            Bundle bundle = new Bundle();
            bundle.putString("from_come", "onlinedata");
            fragobj.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, fragobj, "onlinedata");
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(getString(R.string.bidesh));


            // Handle the camera action
        }else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, ""+getString(R.string.app_name));
                String shareMessage= "\nRecommending you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        } else if (id == R.id.nav_send) {
            final String myappPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + myappPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + myappPackageName)));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateActionBarTitle(Fragment fragment) {
        String fragClassName = fragment.getClass().getName();

        if (fragClassName.equals(Home_Data.class.getName())) {
            setTitle(getString(R.string.home));
        }else if (fragClassName.equals(Khela_Data.class.getName())) {
            setTitle(getString(R.string.khela_dhula));
        }else if (fragClassName.equals(binodon_Data.class.getName())) {
            setTitle(getString(R.string.binodon));
        }else if (fragClassName.equals(Bidesh_Data.class.getName())) {
            setTitle(getString(R.string.bidesh));
        }else if (fragClassName.equals(Banijjo_Data.class.getName())) {
            setTitle(getString(R.string.banijjo));
        }else if (fragClassName.equals(Chakri_Data.class.getName())) {
            setTitle(getString(R.string.chakri));
        }else if (fragClassName.equals(Shikkha_Data.class.getName())) {
            setTitle(getString(R.string.shikkha));
        }else if (fragClassName.equals(Online_Data.class.getName())) {
            setTitle(getString(R.string.online));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        activityStatus=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityStatus= true;
    }

    private void requestNewInterstitial() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            mInterstitialAd.loadAd(adRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean PermissionChecker(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermissions()){
                return true;
            } else {
                requestPermissionsHere();
            }
        } else {

            return true;
        }
        return false;
    }




    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e("tttttttttttttt", "sendRegistrationToServer: " + token);
        String tag_string_req = "save_token";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.URL_DATA_LOADER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("tttttttttttt", "Login Response: " + response);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "tokenSaver");
                params.put("token", token);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


}
