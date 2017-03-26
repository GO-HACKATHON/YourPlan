package com.example.user.myapplication;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;


public class PlanDetail extends AppCompatActivity implements OnMapReadyCallback
{
    String plan_id = "";
    String plan_name = "";
    String plan_address = "";
    public static String plan_lat = "";
    public static String plan_lng = "";
    String plan_datetime = "";
    String plan_description = "";
    ProgressDialog pDialog;
    private GoogleMap mMap;
    private SupportMapFragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        plan_id = this.getIntent().getStringExtra("_id");
        plan_name = this.getIntent().getStringExtra("_name");
        plan_address = this.getIntent().getStringExtra("_address");
        plan_lat = this.getIntent().getStringExtra("_lat");
        plan_lng = this.getIntent().getStringExtra("_lng");
        plan_datetime = this.getIntent().getStringExtra("_datetime");
        plan_description = this.getIntent().getStringExtra("_description");
        actionBar.setTitle(plan_name);

        ((TextView) findViewById(R.id.dPlan_address)).setText(plan_address);
        ((TextView) findViewById(R.id.dPlan_datetime)).setText(plan_datetime);
        ((TextView) findViewById(R.id.dPlan_description)).setText(plan_description);

        mFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFrag.getMapAsync(PlanDetail.this);

        Pusher pusher = new Pusher("1001f425ab2bb745b1d0");
        Channel channel = pusher.subscribe("your-plan");
        channel.bind("MeasureTime", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(String channelName, String eventName, final String data)
            {
//                Log.d("THIS IS DATA", data);
                Log.d("pusher jalan", "pusher jalan");
            }
        });
        pusher.connect();

        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("plan_id", plan_id);
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        try
        {
            LatLng sydney = new LatLng(Double.valueOf(plan_lat), Double.valueOf(plan_lng));
            mMap.addMarker(new MarkerOptions()
                    .position(sydney));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, Float.valueOf("16")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }
}
