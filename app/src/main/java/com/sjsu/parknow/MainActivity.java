package com.sjsu.parknow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sjsu.parknow.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private String mainCurKnownAddress;
    private Location mainLastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        drawerLayout = binding.drawerLayout;
        navController = Navigation.findNavController(this, R.id.myNavHostFragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.selection) {
                    Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG).show();
//                } else if (id == R.id.logoutFragment) {
//                    Toast.makeText(getApplicationContext(), "Logging out..", Toast.LENGTH_SHORT).show();
//                    showLogoutOptions();
//                }
                }else if (id == R.id.getMyCar) {
                    try {
                        LatLng latLng = getSavedLocationFromDevice();
                        if(latLng != null){
                            String currAddr = getAddressFromLatLngCord(latLng).getAddressLine(0);
                            String directionAddressString = encodeAddressString(currAddr);
                            if(mainCurKnownAddress == null){
                                LatLng latLngTemp = new LatLng(37.00, -122.00);
                                mainCurKnownAddress = getAddressFromLatLngCord(latLng).getAddressLine(0);
                            }
//                        String mapsURL = "https://www.google.com/maps/dir/" + mainCurKnownAddress + "/" + directionAddressString;   //used for opening google maps;
//                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsURL));
//                        startActivity(intent);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+directionAddressString+"&mode=w");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Saved location not found", Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Saved location not found", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                //maintain Navigation view standard behavior
                NavigationUI.onNavDestinationSelected(item, navController);
                //close drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.myNavHostFragment);
        return NavigationUI.navigateUp(navController, drawerLayout);
    }

    private LatLng setLatLngCordFromAddress(String address) {

        Geocoder geoCoder = new Geocoder(getApplicationContext());
        List<Address> addressesList;

        try {
            addressesList = geoCoder.getFromLocationName(address, 1);
            if (addressesList != null) {
                Address addressItem = addressesList.get(0);
                LatLng latLng = new LatLng(addressItem.getLatitude(), addressItem.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }

    private LatLng getSavedLocationFromDevice() throws IOException, JSONException {
        JSONObject jsonObj = new JSONObject();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream
                    (new File(getCacheDir() + File.separator + "cacheFile.srl")));
            jsonObj = new JSONObject((String) in.readObject());
            in.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("My App", jsonObj.toString());
        LatLng latLng;

        if (jsonObj.has("lat") && !jsonObj.isNull("lat") && jsonObj.has("lng") && !jsonObj.isNull("lng") ) {
            // Do something with object.
            Log.d("lat value inside ", String.valueOf(jsonObj.get("lat")));
            Log.d("lng value inside", String.valueOf(jsonObj.get("lng")));
            latLng = new LatLng(Double.parseDouble(jsonObj.getString("lat")), Double.parseDouble(jsonObj.getString("lng"))); // from local file
        } else {
            Log.d("Saved Spot Not Found","Location Not Found");
            latLng = null;
        }

        //LatLng latLng = new LatLng(37.337105, -122.0379474); //sample location
        return latLng;
    }
    public String encodeAddressString(String address) {
        String currAddressString = address;
        try {
            currAddressString = currAddressString.replaceAll("\\/", "");
            currAddressString = currAddressString.replaceAll("\\,", ",+");
            currAddressString = currAddressString.replaceAll("\\ ", "+");
            currAddressString = currAddressString.replaceAll("\\++", "+");
            currAddressString = URLEncoder.encode(currAddressString, "utf-8");
            currAddressString = currAddressString.replaceAll("\\%2C", ",");
            currAddressString = currAddressString.replaceAll("\\%2B", "+");
        } catch (
                UnsupportedEncodingException err) {
            err.printStackTrace();
        }
        return currAddressString;
    }
    private Address getAddressFromLatLngCord(LatLng latLng) {
        Geocoder geoCoder = new Geocoder(getApplicationContext());
        List<Address> addressesList;
        try {
            addressesList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if (addressesList != null) {
                Address addressLine = addressesList.get(0);
                return addressLine;
            } else {
                return null;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }
    public void callFusedLocationProviderClientMain() {
        try {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mainLastKnownLocation = task.getResult();
                            LatLng latLng = new LatLng(mainLastKnownLocation.getLatitude(),
                                    mainLastKnownLocation.getLongitude());
                            mainCurKnownAddress = getAddressFromLatLngCord(latLng).getAddressLine(0);
                        }
                    }
                });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private LatLng getLatLngCordFromAddress(String address) {

        Geocoder geoCoder = new Geocoder(getApplicationContext());
        List<Address> addressesList;

        try {
            addressesList = geoCoder.getFromLocationName(address, 1);
            if (addressesList != null) {
                Address addressItem = addressesList.get(0);
                LatLng latLng = new LatLng(addressItem.getLatitude(), addressItem.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }
}