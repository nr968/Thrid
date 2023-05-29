package com.example.thrid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Sos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    Button getLocationBtn;
    TextView latitude,longitude;
    String message,safemsg;
    LocationManager locationManager;
    ImageView torch, siren, safe;
    Boolean istorchon, issoundon, sosclicked, safeclicked;
    String Latitude = "0", Longitude = "0";
    String[] valfromcname,valfromcnumber;
    private CameraManager mCameraManager;
    private String mCameraId;
    private MediaPlayer mp,smp;

    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        getLocationBtn = (Button) findViewById(R.id.button);
        latitude = (TextView) findViewById(R.id.textView);
        longitude = (TextView) findViewById(R.id.textView1);
        torch = (ImageView) findViewById(R.id.torch);
        siren = (ImageView) findViewById(R.id.siren);
        safe = (ImageView) findViewById(R.id.safe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        takeKeyEvents(true);
        istorchon = true;
        issoundon = true;
        sosclicked = false;
        safeclicked = false;
        valfromcname=new String[5];
        valfromcnumber=new String[5];
        smp = MediaPlayer.create(this, R.raw.siren_sound);

        // initialize the necessary libraries
        init();

        restoreValuesFromBundle(savedInstanceState);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosclicked = true;
                startLocationUpdates();
            }
        });

        torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (istorchon) {
                    turnOnFlashLight();
                    istorchon = false;
                } else {
                    turnOffFlashLight();
                    istorchon = true;
                }


            }
        });

        siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (issoundon) {
                    smp.start();
                    issoundon = false;
                    smp.setLooping(true);
                } else {
                    smp.pause();
                    issoundon = true;
                }

            }
        });

        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safeclicked = true;
                startLocationUpdates();
            }
        });



    }

    public void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Toast.makeText(Sos.this,"Success",Toast.LENGTH_SHORT).show();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Toast.makeText(Sos.this,"GPS is not ON",Toast.LENGTH_SHORT).show();
                            try {

                                status.startResolutionForResult(Sos.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(Sos.this,"Settings not allowed",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    }

    public void turnOnFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
                playOnOffSound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
                playOnOffSound();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //torchlight sound
    private void playOnOffSound() {

        mp = MediaPlayer.create(Sos.this, R.raw.flash_sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();

    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }
    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    public void updateLocationUI() {
        EnableGPSAutoMatically();
        if (mCurrentLocation != null) {
            Double finalLatitude =  mCurrentLocation.getLatitude();
            Double finalLongitude = mCurrentLocation.getLongitude();
            latitude.setText("Latitude: " + mCurrentLocation.getLatitude());
            longitude.setText("Longitude: " + mCurrentLocation.getLongitude());
            /*Intent intent = new Intent(Sos.this, gps.class);

            intent.putExtra("latitude",""+ finalLatitude);
            intent.putExtra("longitude", "" + finalLongitude);
            startActivity(intent);*/
            // giving a blink animation on TextView
            latitude.setAlpha(0);
            longitude.setAlpha(0);
            latitude.animate().alpha(1).setDuration(300);
            longitude.animate().alpha(1).setDuration(300);

            if(sosclicked) {
                Bundle bundle = getIntent().getExtras();
                //valfromcname[0] = bundle.getString("name1");
                valfromcnumber[0] = bundle.getString("number1");
                //valfromcname[1] = bundle.getString("name2");
                valfromcnumber[1] = bundle.getString("number2");
                //valfromcname[2] = bundle.getString("name3");
                valfromcnumber[2] = bundle.getString("number3");
                message = "http://maps.google.com/maps?daddr=" + finalLatitude + "," + finalLongitude + "\nI AM IN DANGER!!!NEED HELP\n SEE MY LOCATION";
                SmsManager smsManager = SmsManager.getDefault();
                StringBuffer smsBody = new StringBuffer();
                smsBody.append(Uri.parse(message));
                for(int i=0;i<=2;i++) {
                    smsManager.sendTextMessage(valfromcnumber[i], null, smsBody.toString(), null, null);
                }
            }

            if(safeclicked){
                Bundle bundle = getIntent().getExtras();
                //valfromcname[0] = bundle.getString("name1");
                valfromcnumber[0] = bundle.getString("number1");
                //valfromcname[1] = bundle.getString("name2");
                valfromcnumber[1] = bundle.getString("number2");
                //valfromcname[2] = bundle.getString("name3");
                valfromcnumber[2] = bundle.getString("number3");
                safemsg = "http://maps.google.com/maps?daddr=" + finalLatitude + "," + finalLongitude + "\nI AM SAFE NOW\n AT THIS LOCATION";
                SmsManager smsManager = SmsManager.getDefault();
                StringBuffer smsBody = new StringBuffer();
                smsBody.append(Uri.parse(safemsg));
                for(int i=0;i<=2;i++) {
                    smsManager.sendTextMessage(valfromcnumber[i], null, smsBody.toString(), null, null);
                }
            }

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                latitude.setText(Latitude);
                longitude.setText(Longitude);
                //locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+ addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2)+"\n" +latitude+","+longitude);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Toast.makeText(Sos.this, "All location settings are satisfied.",Toast.LENGTH_SHORT).show();

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Toast.makeText(Sos.this, "Location settings are not satisfied. Attempting to upgrade location settings ",Toast.LENGTH_SHORT).show();
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Sos.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Toast.makeText(Sos.this, "PendingIntent unable to execute request.",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Toast.makeText(Sos.this, errorMessage,Toast.LENGTH_SHORT).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    //Navigation drawer item click events
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent a = new Intent(Sos.this,Sos.class);
            startActivity(a);
        } else if (id == R.id.nav_police) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=police");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } else if (id == R.id.nav_hospital) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=hospitals");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } else if (id == R.id.nav_complaint) {
            Intent a  = new Intent(Sos.this, Complaint.class);
            startActivity(a);

        } else if (id == R.id.nav_counsil) {

        } else if (id == R.id.nav_numbers) {

        } else if (id == R.id.nav_Self){

        } else if (id == R.id.nav_edit){

        } else if (id == R.id.nav_logout){

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

