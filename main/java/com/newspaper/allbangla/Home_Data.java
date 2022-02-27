package com.newspaper.allbangla;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.newspaper.allbangla.volley.AppController;
import com.newspaper.allbangla.volley.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Home_Data extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    RelativeLayout errorFounder;
    GridView listView;
    SearchableAdapter searchableAdapter;
    private String TAG= "uuuuuuuuuuuuuuuuuuuu";
    private LinkedList<String> nameList= new LinkedList<String>();
    private LinkedList<String> linkList= new LinkedList<String>();
    private LinkedList<String> imageUrlList= new LinkedList<String>();
    private LinkedList<String> viewsList= new LinkedList<String>();

    private ProgressBar simpleProgressBar;
    private SwipeRefreshLayout sw_refresh;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_data, container, false);
        // Inflate the layout for this fragment
        readyTask(v);

        return v;
    }



    public void readyTask(final View view) {

        listView= view.findViewById(R.id.list_view);
        simpleProgressBar= view.findViewById(R.id.simpleProgressBar);

        sw_refresh = view.findViewById(R.id.sw_refresh);

        errorFounder = view.findViewById(R.id.error_founder);
        errorFounder.setVisibility(View.GONE);
        errorFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorFounder.setVisibility(View.GONE);
                new InternetCheck().execute();
            }
        });


        new InternetCheck().execute();

        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//here i am adding just one more element to display while refershing
// in your case you can request wether now item is there.
//or check sqlite for new data if data is from sqlite database
//when you get the new data you can add it to arraylist and notify adapter as below


                new InternetCheck().execute();
//following line is important to stop animation for refreshing
                sw_refresh.setRefreshing(false);
            }
        });
    }


    private void filterTutors(final String name, final String searchtext) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        showDialog();

        nameList.clear();
        linkList.clear();
        viewsList.clear();
        imageUrlList.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.URL_DATA_LOADER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String name = jsonobject.getString("name");
                        String links = jsonobject.getString("links");
                        String views = jsonobject.getString("views");
                        String imageUrl = jsonobject.getString("image_url");
                        String created_at = jsonobject
                                .getString("created_at");
                        nameList.add(name);
                        linkList.add(links);
                        viewsList.add(views);
                        if (imageUrl==null){

                            imageUrlList.add("none");
                        }else {
                            imageUrlList.add(imageUrl);
                        }

                        Log.e("yyyyyyyyyyyyyyyy", "it"+name);
                        if (i==0){
                            searchableAdapter = new SearchableAdapter(getContext(), nameList,linkList,imageUrlList, viewsList);
                            listView.setAdapter(searchableAdapter);


                        }
                        else {
                            searchableAdapter.notifyDataSetChanged();
                        }



                    }
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            if (i!=1) {
//                                gotoNextDestinstion(i);
//
//                            }
//                        }
//                    });
//                    see_more.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (nameList.size()==0){

                    errorFounder.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                errorFounder.setVisibility(View.VISIBLE);

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Log.e("ttttttttttt","he");
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "loadata");
                params.put("name", name);
                params.put("type", "global");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    boolean stopReloading= false;
    private void showDialog() {
        simpleProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        simpleProgressBar.setVisibility(View.GONE);
    }



    public void checkNetwork( boolean isNetworkThere){
        if (isNetworkThere) {

            filterTutors("Latest", "");
        }else {


            errorFounder.setVisibility(View.VISIBLE);


        }
    }


    class InternetCheck extends AsyncTask<Void,Void,Boolean> {


        @Override protected Boolean doInBackground(Void... voids) { try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) { return false; } }

        @Override protected void onPostExecute(Boolean internet) {

            hideDialog();
            checkNetwork(internet);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }
    }


}
