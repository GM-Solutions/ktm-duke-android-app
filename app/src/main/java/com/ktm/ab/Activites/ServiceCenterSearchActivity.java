package com.ktm.ab.Activites;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.ktm.ab.R;
import com.ktm.ab.Util.MapUtil;
import com.ktm.ab.Util.SharedDataUtils;
import com.ktm.ab.Util.UIUtil;
import com.ktm.ab.databaseModel.Dealer;
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceCenterSearchActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String LAST_SYNC = "last_sync";
    private GoogleMap mMap;
    private AutoCompleteTextView searchPlace;
    private ArrayList<String> addresses;
    private ArrayAdapter<String> adpater;
    private String searchText = "";
    private LinearLayout llImage, llMap, btnNearBy;
    private TextView btnSearch;
    private ArrayList<Dealer> dealerArrayList;
    private boolean isLocalSearch, isCameraMove, firstTimeZoom = true;
    private double longitude = 0, latitude = 0;
    private static int KM_FOR = 20;
    private List<Dealer> dealers;
    private ProgressBar pbProcessing;
    private boolean isLocationGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center_search);
        SugarContext.init(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pbProcessing = (ProgressBar) findViewById(R.id.pbProcessing);
        pbProcessing.setVisibility(View.GONE);

//        ((ImageView) findViewById(R.id.btnAllDealer)).setOnClickListener(this);

        llImage = (LinearLayout) findViewById(R.id.llImage);
        llMap = (LinearLayout) findViewById(R.id.llMap);
        btnSearch = (TextView) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        btnNearBy = (LinearLayout) findViewById(R.id.btnNearBy);
        btnNearBy.setOnClickListener(this);

        llImage.setVisibility(View.VISIBLE);
        llMap.setVisibility(View.GONE);


        dealerArrayList = new ArrayList<>();
        dealers = new ArrayList<>();

        searchPlace = (AutoCompleteTextView) findViewById(R.id.search_place);
        addresses = new ArrayList<String>();
        adpater = new ArrayAdapter<String>(this, R.layout.autocomplete_text_view, addresses);
        searchPlace.setAdapter(adpater);

        searchPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    llImage.setVisibility(View.VISIBLE);
                    llMap.setVisibility(View.GONE);
                } else {
                    llMap.setVisibility(View.VISIBLE);
                    llImage.setVisibility(View.GONE);
                    searchText = s.toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getAddressAutocompleteFromGoogleAPI();
                        }
                    }, 1000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getLatLongFromAddress(addresses.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        if (dealers.size() == 0) {
        getAllDealer();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLocationEnable(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(12.47843, 72.87684);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Demo")).setTag(1);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        addMapMoveListener();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                firstTimeZoom = true;
                if (TextUtils.isEmpty(searchPlace.getText().toString())) {
                    searchPlace.setError("Please enter address");
                } else {
                    if (isLocalSearch) {
                        getDealerFromLocal();
                    } else {
                        pbProcessing.setVisibility(View.VISIBLE);
                        getLatLngFromAddressGoogleAPI();
                    }
                }
                break;

            case R.id.btnNearBy:
                pbProcessing.setVisibility(View.VISIBLE);
                llImage.setVisibility(View.GONE);
                llMap.setVisibility(View.VISIBLE);
                firstTimeZoom = true;
                requestCurrentLocation();
                break;

//            case R.id.btnAllDealer:
//
//                break;
        }
    }


    private void getAddressAutocompleteFromGoogleAPI() {

        if (!UIUtil.isInternetAvailable(this)) {
            isLocalSearch = true;
            return;
        }

        String JSON_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + searchText.replaceAll(" ", "%20") +
                "&radius=500000&key=AIzaSyC1DGCkLALon-namvjpAu3AiZuD4nXHZFQ";

        StringRequest stringRequest = new StringRequest(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Result: ", "Test ::: = " + response);
                        if (TextUtils.isEmpty(response)) {
                            pbProcessing.setVisibility(View.GONE);
                            return;
                        }
                        parseJsonAndUpdate(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pbProcessing.setVisibility(View.GONE);
//                        Toast.makeText(ServiceCenterSearchActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        getDealerFromLocal();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getDealerFromLocal() {
        addresses.clear();
        isLocalSearch = true;
        dealerArrayList.clear();
        dealerArrayList.addAll(Dealer.find(Dealer.class, " address like '%?%' or city like '%?%' ",
                searchText, searchText));

        addDealerOnMap();

    }

    private void addDealerOnMap() {
        try {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            MarkerOptions mp = new MarkerOptions();
//            mp.position(new LatLng(latitude, longitude));
//            Marker m = mMap.addMarker(mp);
//            m.setTag(-1);

            for (int i = 0; i < dealerArrayList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(dealerArrayList.get(i).getLat(), dealerArrayList.get(i).getLng()));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ktm_map_icon));
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(i);
                builder.include(marker.getPosition());
            }
            pbProcessing.setVisibility(View.GONE);

            isCameraMove = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            if (firstTimeZoom)
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            firstTimeZoom = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isCameraMove = true;
                }
            }, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void parseJsonAndUpdate(String response) {
        try {
            addresses.clear();
            isLocalSearch = false;
            JSONObject jsonObject = new JSONObject(response);
            JSONArray predictions = jsonObject.getJSONArray("predictions");
            for (int i = 0; i < predictions.length(); i++) {
                addresses.add(predictions.getJSONObject(i).getString("description"));
            }
            adpater = new ArrayAdapter<String>(this, R.layout.autocomplete_text_view, addresses);
            searchPlace.setThreshold(2);
            searchPlace.setAdapter(adpater);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getLatLongFromAddress(String address) {

        if (TextUtils.isEmpty(address)) {
            pbProcessing.setVisibility(View.GONE);
            return;
        }

        latitude = 0;
        longitude = 0;

        Geocoder coder = new Geocoder(this);
        try {

            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(address, 1);
            for (Address add : adresses) {
                longitude = add.getLongitude();
                latitude = add.getLatitude();
            }
            if (latitude == 0 || longitude == 0) {
                pbProcessing.setVisibility(View.GONE);
                return;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
//            getNearestDealer();
            Log.e("LatLong : ", "Lat ;  " + latitude + "  Long : " + longitude);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNearestDealer() {

        if (dealers.size() == 0)
            dealers.addAll(Dealer.listAll(Dealer.class));

        dealerArrayList.clear();
        double d = MapUtil.getCurrentRadius(mMap);
        for (int i = 0; i < dealers.size(); i++) {
            if (!(dealers.get(i).getLat() == null || dealers.get(i).getLng() == null
                    || latitude == 0 || longitude == 0)) {

                double dist = MapUtil.distance(latitude, longitude, dealers.get(i).getLat(), dealers.get(i).getLng());
                Log.e("LatLong : ", "  Distance  : ----------  " + dist);

                if (dist < d) {
                    dealerArrayList.add(dealers.get(i));
                    Log.e("LatLong : ", "  in IF Lat ;  " + dealers.get(i).getLat() + "  Long : " + dealers.get(i).getLng());
                } else {
                    Log.e("LatLong : ", " In ELSE Lat ;  " + dealers.get(i).getLat() + "  Long : " + dealers.get(i).getLng());
                }
            }
        }

        addDealerOnMap();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
//        Toast.makeText(this, "On Marker click", Toast.LENGTH_LONG).show();
        isCameraMove = false;
        if (marker != null && (int) marker.getTag() != -1)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLocationInfo(marker);
                    isCameraMove = true;
                }
            }, 1000);

        return false;
    }

    private void showLocationInfo(Marker marker) {

        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_marker_label);
            final Dealer dealer = dealerArrayList.get((int) marker.getTag());

            TextView tvName = (TextView) dialog.findViewById(R.id.tvName);
            TextView tvAddress = (TextView) dialog.findViewById(R.id.tvAddress);
            TextView tvMore = (TextView) dialog.findViewById(R.id.tvMoreDetails);

            tvName.setText(dealer.getName());
            String s = Html.fromHtml(dealer.getAddress()).toString();

            tvAddress.setText(s.trim());


            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(getApplicationContext(), DealerDetailsActivity.class);
                    myIntent.putExtra("dealer", dealer);
                    startActivity(myIntent);
                    dialog.cancel();
                }
            });

            //dialog.getWindow().setLayout(430, 350);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getAllDealer() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting dealer data....");
        progressDialog.show();
        String JSON_URL = "http://bajajpb.gladminds.co/rest/v1/dealers-list?last_sync="
                + SharedDataUtils.getStringFields(this, LAST_SYNC);

        Log.e("URL", "URL :: " + JSON_URL);
        StringRequest stringRequest = new StringRequest(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.e("Result: ", "Test ::: = " + response);
                            if (TextUtils.isEmpty(response)) {
                                progressDialog.dismiss();
                                return;
                            }

                            JSONObject jsonObject = new JSONObject(response);

                            Gson gson = new Gson();
                            Dealer[] d = gson.fromJson(jsonObject.getJSONArray("dealers").toString(), Dealer[].class);
                            Dealer.saveInTx(d);
                            dealers.clear();
                            dealers.addAll(Dealer.listAll(Dealer.class));
                            SharedDataUtils.addStringFields(getApplicationContext(), LAST_SYNC, jsonObject.getString("last_sync"));

                        } catch (Exception e) {
                            e.printStackTrace();
                            dealers.clear();
                            dealers.addAll(Dealer.listAll(Dealer.class));
                            progressDialog.dismiss();

                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
//https://api.myjson.com/bins/s4bzn


    private boolean checkForLocationEnable(final Context context) {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Alert");
            dialog.setMessage("Please enable GPS.");
            dialog.setPositiveButton("GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            checkForLocationPermission();
        }
        return gps_enabled;
    }


    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestCurrentLocation();
        }
    }

    private void requestCurrentLocation() {
        isLocationGet = true;
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("onLocationChanged", " Location onLocationChanged :  lat : " + latitude + "  Long : " + longitude);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.e("onLocationChanged", "onLocationChanged :  lat : " + latitude + "  Long : " + longitude);
                    isLocationGet = false;
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
                    locationManager.removeUpdates(this);
                    isCameraMove = true;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                }
                return;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.e("onLocationChanged", " befor onLocationChanged :  lat : " + latitude + "  Long : " + longitude);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    isLocationGet = false;
                    Log.e("onLocationChanged", "onLocationChanged :  lat : " + latitude + "  Long : " + longitude);
                    isCameraMove = true;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                }

            }
        }, 5000);

    }

    private void addMapMoveListener() {

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                Log.e("setOnCameraChangeListe", "onCameraChange :  lat : " + isCameraMove);
                if (isCameraMove) {
                    if (mMap != null) {
                        mMap.clear();
                    }
                    latitude = cameraPosition.target.latitude;
                    longitude = cameraPosition.target.longitude;
                    Log.e("setOnCameraIdleListener", "onCameraChange :  lat : " + latitude + "  Long : " + longitude);
                    getNearestDealer();
                }
            }
        });
    }

    private void getLatLngFromAddressGoogleAPI() {

        if (!UIUtil.isInternetAvailable(this)) {
            return;
        }

        String JSON_URL = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + searchText.replaceAll(" ", "%20") + "&key=AIzaSyC1DGCkLALon-namvjpAu3AiZuD4nXHZFQ";
        Log.e("URL", "Url : " + JSON_URL);

        StringRequest stringRequest = new StringRequest(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("URL", "response : " + response);
                        Log.e("Result: ", "Test ::: = " + response);
                        if (TextUtils.isEmpty(response)) {
                            pbProcessing.setVisibility(View.GONE);
                            return;
                        }
                        parseJsonForLatLng(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pbProcessing.setVisibility(View.GONE);
                        getDealerFromLocal();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void parseJsonForLatLng(String response) {

        try {

            JSONObject parent = new JSONObject(response);
            JSONObject obj = parent.getJSONArray("results").getJSONObject(0);
            JSONObject location = obj.getJSONObject("geometry").getJSONObject("location");
            String lat = location.getString("lat");
            String lng = location.getString("lng");
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            pbProcessing.setVisibility(View.GONE);
            Log.e("URL", "response parseJsonForLatLng : " + response);
        } catch (Exception e) {
            pbProcessing.setVisibility(View.GONE);
            Log.e("URL", "Exception : " + e.toString());
            e.printStackTrace();
        }
    }

    //https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
}