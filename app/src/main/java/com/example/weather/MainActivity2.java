package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback{
    TextView name,temp,wind,min_temp,max_temp,hum,sky;
    ImageView ico;
    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        name =findViewById(R.id.name);
        temp =findViewById(R.id.temp);
        wind =findViewById(R.id.wind);
        min_temp =findViewById(R.id.min_temp);
        max_temp =findViewById(R.id.max_temp);
        hum=findViewById(R.id.hum);
        sky=findViewById(R.id.sky);
        ico=findViewById(R.id.icon);



        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        name.setText(intent.getStringExtra("name"));
        temp.setText(intent.getStringExtra("temp"));
        wind.setText("Wind speed:" + " " + intent.getStringExtra("wind"));
        min_temp.setText("Min Temp:" + " " +intent.getStringExtra("temp_min")+"°C");
        max_temp.setText("Max Temp:" + " " +intent.getStringExtra("temp_max")+"°C");
        hum.setText("Humidity:" + " " +intent.getStringExtra("hum"));
        sky.setText( intent.getStringExtra("des"));

        String x=intent.getStringExtra("icon");
        loadIcon(x);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void loadIcon(String x)
    {
        Ion.with(this)
                .load("http://openweathermap.org/img/w/"+x+".png")
                .intoImageView(ico);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();
        double lon= Double.parseDouble(intent.getStringExtra("lon"));
        double lat = Double.parseDouble(intent.getStringExtra("lat"));
        String name=intent.getStringExtra("name");
        // Add a marker in Sydney and move the camera
        LatLng mark = new LatLng(lat , lon);
        googleMap.addMarker(new MarkerOptions().position(mark).title(name));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mark,7),1000,null);
       /* googleMap.moveCamera(CameraUpdateFactory.newLatLng(mark));*/
    }

    // back button in menu bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if (id== android.R.id.home)
        {this.finish();}
        return super.onOptionsItemSelected(item);
    }


}