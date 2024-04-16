package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Adapters.ViewPagerMessengerAdapter;
import com.example.farmconnect.utils.AndroidUtil;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    TabLayout tabLayout;
    ViewPager viewPager;
    MenuItem weather,weatherIcon;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Elements
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission granted, get current location
            getLocation();
        }

        ViewPagerMessengerAdapter adapter = new ViewPagerMessengerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Set custom tab views with icons
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }

        // Set the default tab color
        updateTabIconColors(viewPager.getCurrentItem());

        // Set a listener for tab selection to update icon colors
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabIconColors(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for this example
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //Toolbar methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        // Get the weather menu item
        weather = menu.findItem(R.id.weather);
        weatherIcon = menu.findItem(R.id.weather_icon);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.profile) {
            //goto profile
            AndroidUtil.showToast(getApplicationContext(),"Your Profile");
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        } else if(id == R.id.search) {
            //goto search option
            AndroidUtil.showToast(getApplicationContext(),"Search Users");
            Intent intent = new Intent(getApplicationContext(),SearchUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            //goto settings
            AndroidUtil.showToast(getApplicationContext(),"Settings Option");
        }
        return true;
    }

    // Method to update icon colors when a tab is selected
    private void updateTabIconColors(int selectedPosition) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View customView = tab.getCustomView();
                ImageView icon = customView.findViewById(R.id.tab_icon);

                // Set the color filter for the active tab
                int tabColor = (i == selectedPosition) ? ContextCompat.getColor(this, R.color.agri) : Color.GRAY;
                icon.setColorFilter(tabColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }
    private void getLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission granted, get last known location
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                fetchCityName(lastKnownLocation);
            } else {
                // Show toast to turn on device location
                Toast.makeText(getApplicationContext(), "Please turn on device location and restart app.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // Location updated, fetch city name
            fetchCityName(location);

            // Remove location updates
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void fetchCityName(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                if (cityName != null) {
                    fetchWeatherData(cityName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchWeatherData(String cityName) {
        new FetchWeatherTask().execute(cityName);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String cityName = params[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=99d842f8f1eda59296ae0f333585814e")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String location = jsonObject.getString("name");
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");

                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String description = weatherObject.getString("description");
                    String iconCode = weatherObject.getString("icon");

                    weather.setTitle(temperature+" K");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        weather.setTooltipText(description+", " + temperature+" Kelvin"+" in "+location);
                    }

                    // Load weather icon
                    String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";
                    new LoadWeatherIconTask().execute(iconUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class LoadWeatherIconTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String iconUrl = params[0];
            try {
                URL url = new URL(iconUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                // Convert Bitmap to Drawable
                Drawable iconDrawable = new BitmapDrawable(getResources(), bitmap);
                weatherIcon.setIcon(iconDrawable);
            }
        }
    }

}