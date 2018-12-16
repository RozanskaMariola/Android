package com.example.mrozansk.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static String OPENWEATHER_WEATHER_QUERY = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&mode=html&appid=4526d487f12ef78b82b7a7d113faea64";

    TextView latTextView, lonTextView, cityTextView;
    WebView weatherWebView;
    
    private LocationProvider locationProvider;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherWebView = (WebView) findViewById(R.id.weatherWebView);

       latTextView = (TextView) findViewById(R.id.latTextView);
       lonTextView = (TextView) findViewById(R.id.lonTextView);

       cityTextView = (TextView) findViewById(R.id.cityTextView);



    }

    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            String lat = msg.getData().getString("lat");
            String lon = msg.getData().getString("lon");
         //   String woeid = msg.getData().getString("woeid");
            String web = msg.getData().getString("web");
            String city = msg.getData().getString("city");
//referencje pobrane wcześniej w metodzie onCreate(...)
            latTextView.setText(lat);
            lonTextView.setText(lon);
        //    woeidTextView.setText(woeid);
            cityTextView.setText(city);
            weatherWebView.loadDataWithBaseURL(null, web, "text/html", "utf-8", null);
        }
    };


    public void onLocationChanged(Location location) {
        final double lat = (location.getLatitude());
        final double lon = location.getLongitude();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather(lat,lon);
            }
        }).start();
    };


    private void updateWeather(double lat, double lon){
       // String weather = getContentFromUrl( String.format(OPENWEATHER_WEATHER_QUERY, lat,lon) );
       String addr = String.format(OPENWEATHER_WEATHER_QUERY, lat,lon);

               Log.d("link", addr);

        String weather = getContentFromUrl(addr);

        Message m = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("lat", String.valueOf(lat));
        b.putString("lon", String.valueOf(lon));
        b.putString("web", weather);
        m.setData(b);
        myHandler.sendMessage(m);
    }

    // usage String.format(OPENWEATHER_WEATHER_QUERY, lat,lon)

    public String getContentFromUrl(String addr) {
        String content = null;

        Log.v("[GEO WEATHER ACTIVITY]", addr);
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(addr);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            content = stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection!= null) urlConnection.disconnect();
        }
        return content;
    }

     double lat;
     double lon;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
              lat = (location.getLatitude());
              lon = location.getLongitude();

          //  System.out.println("LAT : "+ lat + " LON "+ lon);
            Log.d("tag", "LAT : "+ lat + " LON "+ lon);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateWeather(lat,lon);
                }
            }).start();



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
    };



    @Override
    protected void onStop() {
        super.onStop();

        if (locationProvider != null) {
            Toast.makeText(this, "Location listener unregistered!", Toast.LENGTH_SHORT).show();
            try {
                this.locationManager.removeUpdates(this.locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Location Provider is not avilable at the moment!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    accessLocation();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
// MY_PERMISSIONS_REQUEST_LOCATION is an
// app-defined int constant. The callback method gets the
// result of the request.
        } else{
            accessLocation();
        }
    }

    private void accessLocation(){
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.locationProvider = this.locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if (locationProvider != null) {
            Toast.makeText(this, "Location listener registered!", Toast.LENGTH_SHORT).show();
            try {
                this.locationManager.requestLocationUpdates(locationProvider.getName(), 0, 0,
                        this.locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this,
                    "Location Provider is not avilable at the moment!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showMap(View v){

        Intent myIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:"+lat+","+lon+"?z=14"));
        Log.d("map", "LAT : "+ lat + " LON "+ lon);
        startActivity(myIntent);

    }

}
