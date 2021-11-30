package com.sjsu.parknow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.sjsu.parknow.databinding.FragmentMapsBinding;
import com.sjsu.parknow.model.Geometry;
import com.sjsu.parknow.model.GoogleResponse;
import com.sjsu.parknow.model.Item;
import com.sjsu.parknow.model.Payload;
import com.sjsu.parknow.model.Post;
import com.sjsu.parknow.model.Result;
import com.sjsu.parknow.model.SpotResult;
import com.sjsu.parknow.model.SpotsResponse;
import com.sjsu.parknow.network.GooglePlacesAPI;
import com.sjsu.parknow.network.IGooglePlaces;
import com.sjsu.parknow.network.IParkingSpots;
import com.sjsu.parknow.network.IPostCallParkingSpots;
import com.sjsu.parknow.network.ParkingSpotsSearchAPI;
import com.sjsu.parknow.network.PostCallAPI;

import com.sjsu.parknow.utils.GeoFenceHelper;
import com.sjsu.parknow.utils.PlaceAutoComplete;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

import static android.content.ContentValues.TAG;

public class MapsFragment extends Fragment {
    // file is used to storing the lat long
    private static final String FILE_NAME = "storeLatLong.txt";
    private static final String TAG = MapsFragment.class.getSimpleName();
    private FragmentMapsBinding binding;
    private GoogleMap map;
    private GoogleResponse googleResults;
    private SpotsResponse parkingSpotsResults;
    private CameraPosition cameraPosition;
    private final LatLng defaultLocation = new LatLng(37.00, -122.00);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private String curKnownAddress = null;
    private String curKnownLocation = null;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private IGooglePlaces iGooglePlaces;
    private IParkingSpots iParkingSpots;
    private Marker savedMarker;
    private IPostCallParkingSpots iPostCallParkingSpots;

    private float GEOFENCE_RADIUS = 100;
    private GeoFenceHelper geofenceHelper;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private GeofencingClient geofencingClient;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            geofencingClient = LocationServices.getGeofencingClient(getContext());
            geofenceHelper = new GeoFenceHelper(getContext());
            LatLng cupertino = new LatLng(37.00, -122.00);
            googleMap.addMarker(new MarkerOptions().position(cupertino).title("Default Location: Cupertino"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cupertino, DEFAULT_ZOOM));
            // Use a custom info window adapter to handle multiple lines of text in the
            // info window contents.
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                // Return null here, so that getInfoContents() is called next.
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    // Inflate the layouts for the info window, title and snippet.
                    View infoWindow = getLayoutInflater().inflate(R.layout.info_window, (FrameLayout) requireActivity().findViewById(R.id.map), false);

                    LatLng latLng = marker.getPosition();

                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                        addGeofence(latLng, GEOFENCE_RADIUS);
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    }
                    addGeofence(latLng,GEOFENCE_RADIUS);
                    TextView title = infoWindow.findViewById(R.id.title);
                    title.setText(marker.getTitle());

                    TextView snippet = infoWindow.findViewById(R.id.snippet);
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }
            });

            //put back the stored car parking location from data store
            //setSavedMarkerPosition();
            try {
                setSavedMarkerPosition();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Prompt the user for permission.
            getLocationPermission();

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }
    };
    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        // PendingIntent pendingYesIntent = geofenceHelper.getYesPendingIntent(GEOFENCE_ID, latLng, radius);
        geofenceHelper.setLatLng(latLng);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent( latLng);

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }

    private void setSavedMarkerPosition() throws IOException, JSONException {
        LatLng savedMarkerLatLng = getSavedLocationFromDevice();
        if(savedMarkerLatLng != null){
            addParkedCarMarker(savedMarkerLatLng);
        }

    }

    private LatLng getSavedLocationFromDevice() throws IOException, JSONException {
        JSONObject jsonObj = new JSONObject();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream
                    (new File(getActivity().getCacheDir() + File.separator + "cacheFile.srl")));
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
        LatLng latLng = null;

        if (jsonObj.has("lat") && !jsonObj.isNull("lat") && jsonObj.has("lng") && !jsonObj.isNull("lng") ) {
            // Do something with object.
            Log.d("lat value inside ", String.valueOf(jsonObj.get("lat")));
            Log.d("lng value inside", String.valueOf(jsonObj.get("lng")));
            latLng = new LatLng(Double.parseDouble(jsonObj.getString("lat")), Double.parseDouble(jsonObj.getString("lng"))); // from local file
        } else {
            Log.d("saved spot not found!!","Saved Spot NOT FOUND");
            // latLng = new LatLng(37.337105, -122.0379474); //sample location
        }

        // LatLng latLng = new LatLng(37.337105, -122.0379474); //sample location
        return latLng;
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            //add location button click listener
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (lastKnownLocation == null) {
                        Snackbar.make(binding.mapRelLayout, getString(R.string.loc_error), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    // Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.loc_error), Toast.LENGTH_LONG).show();
                        return true;
                    } else {

                        callFusedLocationProviderClient(true);
                        return false;

                    }

                }
            });

            callFusedLocationProviderClient(false);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void callFusedLocationProviderClient(Boolean showSnackBar) {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.

//
                            lastKnownLocation = task.getResult();
                            LatLng latLng = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());
                            curKnownAddress = getAddressFromLatLngCord(latLng).getAddressLine(0);
                            curKnownLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

                            //TODO: pass radius as well
                            callAPI(curKnownLocation, "");
                            map.clear();
                            if (savedMarker != null) {
                                addParkedCarMarker(latLng);
                            }
//                            marker to be added only when user presses save or automatically by app predictions
//                            map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).position(latLng).title(curKnownAddress));
//                            map.addMarker(new MarkerOptions().position(latLng).title(curKnownAddress));
                            binding.inputSearch.setText(curKnownAddress);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
//
                        } else {
                            Snackbar.make(binding.mapRelLayout, getString(R.string.loc_error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
//                            Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.loc_error), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    // [END maps_current_place_get_device_location]
    //Display SnackBar for dev purpose only. Remove it later!
    public void showSnackBar(String location, String address) {

        Snackbar snackbar = Snackbar.make(binding.mapRelLayout, "Coordinates: " + location, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                lastKnownLocation = null;
                curKnownAddress = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void callAPI(String location, String radius) {
        callGoogleNearbyAPI(location, radius);
        callParkingSpotsAPI(location, radius);
    }

    private void callGoogleNearbyAPI(String location, String radius) {
        iGooglePlaces = GooglePlacesAPI.getAPIService();
        iGooglePlaces.getParkingPlaces(BuildConfig.MAPS_API_KEY, "parking", "1000", curKnownLocation).enqueue(new Callback<GoogleResponse>() {
            @Override
            public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {

                if (response.isSuccessful()) {
                    setGoogleResults(response.body());
                    handleResponse(response.body().getResults());
//                    binding.progressBar.setVisibility(View.GONE);
                    Log.i("RESPONSE", "got results from API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<GoogleResponse> call, Throwable t) {
//                binding.progressBar.setVisibility(View.GONE);
                Log.e("ERROR", "Unable to call GET places API.");
            }
        });
    }

    private void callParkingSpotsAPI(String location, String radius) {
        iParkingSpots = ParkingSpotsSearchAPI.getAPIService();
        iParkingSpots.getParkingSpots("1000", curKnownLocation).enqueue(new Callback<SpotsResponse>() {
            @Override
            public void onResponse(Call<SpotsResponse> call, Response<SpotsResponse> response) {

                if (response.isSuccessful()) {
                    setSpotsResults(response.body());
                    handleSpotsResponse(response.body().getResults());
//                    binding.progressBar.setVisibility(View.GONE);
                    Log.i("RESPONSE", "got results from API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<SpotsResponse> call, Throwable t) {
//                binding.progressBar.setVisibility(View.GONE);
                Log.e("ERROR", "Unable to call GET Parking spots API.");
            }
        });
    }

    public void handleResponse(ArrayList<Result> results) {
        if (results != null) {
            setResultMarkers(results);
//
        }
//
    }

    public void handleSpotsResponse(ArrayList<SpotResult> results) {
        if (results != null) {
            setSpotResultMarkers(results);
//
        }
//
    }

    private void setSpotResultMarkers(ArrayList<SpotResult> results) {
        for (SpotResult res : results) {
//            Geometry geo = res.getGeometry();
            LatLng latLng = new LatLng(Double.parseDouble(res.getLatitude()), Double.parseDouble(res.getLongitude()));
            map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.park_marker_spot_green)).position(latLng).title(getAddressFromLatLngCord(latLng).getAddressLine(0)));
        }
    }

    private void setResultMarkers(ArrayList<Result> results) {
        for (Result res : results) {
            Geometry geo = res.getGeometry();
            LatLng latLng = new LatLng(geo.getLocation().getLat(), geo.getLocation().getLng());
            map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.park_marker)).position(latLng).title(res.getName()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        binding.inputSearch.setAdapter(new PlaceAutoComplete(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1));
        binding.inputSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LatLng latLngCoordinates = getLatLngCordFromAddress(binding.inputSearch.getText().toString());
                if(latLngCoordinates == null) {
                    callFusedLocationProviderClient(false);
                    latLngCoordinates = getLatLngCordFromAddress(binding.inputSearch.getText().toString());
                }

                curKnownLocation = latLngCoordinates.latitude + "," + latLngCoordinates.longitude;
                curKnownAddress = parent.getItemAtPosition(position).toString();
                map.clear();
                if (savedMarker != null) {
                    addParkedCarMarker(latLngCoordinates);
                }
//                map.addMarker(new MarkerOptions().position(latLngCoordinates).title(curKnownAddress));
//                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).position(latLngCoordinates).title(curKnownAddress));
                //TODO: pass radius as well
                callAPI(curKnownLocation, "");
                binding.inputSearch.setText(curKnownAddress);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCoordinates, DEFAULT_ZOOM));
                Log.i("Address : ", String.valueOf(latLngCoordinates));
                InputMethodManager in = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
        //list view button
        binding.openList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.openList), Toast.LENGTH_LONG).show();
                openNearbyList(view);
            }
        });
        //save parking spot button
        binding.saveSpot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LatLng latLngCoordinates = getLatLngCordFromAddress(binding.inputSearch.getText().toString());
                if(latLngCoordinates == null) {
                    callFusedLocationProviderClient(false);
                    latLngCoordinates = getLatLngCordFromAddress(binding.inputSearch.getText().toString());
                }
                curKnownLocation = latLngCoordinates.latitude + "," + latLngCoordinates.longitude;

//                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://qlml5plj9k.execute-api.us-west-2.amazonaws.com")
//                        .addConverterFactory(GsonConverterFactory.create()).build();
//                iPostCallParkingSpots = retrofit.create(IPostCallParkingSpots.class);
                updateSpotStatus(latLngCoordinates.latitude, latLngCoordinates.longitude, "create",getContext());
                Snackbar.make(binding.mapRelLayout, getString(R.string.save_spot_saved), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (savedMarker != null) {
                    savedMarker.remove();
                    savedMarker = null;
                }
                addParkedCarMarker(null);
                callAPI(curKnownLocation, "");
//                callFusedLocationProviderClient(false);
                //saving lat and lon into file for current parking spot.

//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("lat", latLngCoordinates.latitude);
//                    jsonObject.put("lng", latLngCoordinates.longitude);
//                    String userString = jsonObject.toString();
//
//                    File file = new File(getContext().getFilesDir(),FILE_NAME);
//                    FileWriter fileWriter = new FileWriter(file);
//                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//                    bufferedWriter.write(userString);
//                    bufferedWriter.close();
//                    //Log.d("saving lat n long into file","");
//                } catch (JSONException | IOException e) {
//                    e.printStackTrace();
//                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("lat", latLngCoordinates.latitude);
                    jsonObject.put("lng", latLngCoordinates.longitude);
                    String userString = jsonObject.toString();
                    ObjectOutput out = new ObjectOutputStream(new FileOutputStream
                            (new File(getActivity().getCacheDir(), "") + File.separator + "cacheFile.srl"));
                    out.writeObject(userString);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO: add function call to save parking spot in device
                //TODO: add function call mark position status in DB
            }
        });

        //remove parking spot button
        binding.removeSpot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LatLng latLngCoordinates = getLatLngCordFromAddress(binding.inputSearch.getText().toString());
                if(latLngCoordinates == null) {
                    callFusedLocationProviderClient(false);
                    latLngCoordinates = getLatLngCordFromAddress(binding.inputSearch.getText().toString());
                }
                curKnownLocation = latLngCoordinates.latitude + "," + latLngCoordinates.longitude;

//                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://qlml5plj9k.execute-api.us-west-2.amazonaws.com")
//                        .addConverterFactory(GsonConverterFactory.create()).build();
                //iPostCallParkingSpots = retrofit.create(IPostCallParkingSpots.class);
                updateSpotStatus(latLngCoordinates.latitude, latLngCoordinates.longitude, "update",getContext());
                Snackbar.make(binding.mapRelLayout, getString(R.string.parking_spot_removed), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                savedMarker.remove();
                savedMarker = null;
                binding.cardRemoveSpot.setVisibility(View.GONE);
                callAPI(curKnownLocation, "");
//                callFusedLocationProviderClient(false);
                //REMOVE saved spot on device
                JSONObject jsonObj = new JSONObject();
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream
                            (new File(getActivity().getCacheDir() + File.separator + "cacheFile.srl")));
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
                jsonObj.remove("lat");
                jsonObj.remove("lng");
                try {
                    String userString = jsonObj.toString();
                    ObjectOutput out = new ObjectOutputStream(new FileOutputStream
                            (new File(getActivity().getCacheDir(), "") + File.separator + "cacheFile.srl"));
                    out.writeObject(userString);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        return inflater.inflate(R.layout.fragment_maps, container, false);
        return binding.getRoot();
    }

    private void addParkedCarMarker(LatLng latLng) {
        if (latLng == null) {
            latLng = new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude());
        }
        savedMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).position(latLng).title(curKnownAddress));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        binding.cardRemoveSpot.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            curKnownAddress = getAddressFromLatLngCord(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())).getAddressLine(0);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().getApplicationContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void openNearbyList(View view) {
        GoogleResponse response = getGoogleResults();
        SpotsResponse spotsResponse = getSpotsResults();
        MapsFragmentDirections.ActionMapsFragmentToNearbyFragment action = MapsFragmentDirections.actionMapsFragmentToNearbyFragment(curKnownLocation, response, spotsResponse);
        action.setUserLocation(curKnownLocation);
        action.setGoogleResponse(response);
        action.setParkingSpotResponse(spotsResponse);
        Navigation.findNavController(view).navigate(action);
    }

    private void setGoogleResults(GoogleResponse results) {
        this.googleResults = results;
    }

    private void setSpotsResults(SpotsResponse results) {
        this.parkingSpotsResults = results;
    }

    private GoogleResponse getGoogleResults() {
        return googleResults;
    }

    private SpotsResponse getSpotsResults() {
        return parkingSpotsResults;
    }

    private LatLng getLatLngCordFromAddress(String address) {

        Geocoder geoCoder = new Geocoder(requireActivity().getApplicationContext());
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

    private Address getAddressFromLatLngCord(LatLng latLng) {
        Geocoder geoCoder = new Geocoder(requireActivity().getApplicationContext());
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



    public void updateSpotStatus(double latitude, double longitude, String transaction,Context context) {

        String lat = Double.toString(latitude);
        String lng = Double.toString(longitude);
        StringBuilder sb = new StringBuilder();
        sb.append(lat);
        sb.append("|");
        sb.append(lng);
        Item item = new Item();
        Payload payload = new Payload();
        item.setId(sb.toString());
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        if (transaction.equals("create")) {
            item.setParkingstatus("parked");
        } else if (transaction.equals("update")) {
            item.setParkingstatus("available");
        }

        String android_id = Settings.Secure.getString(context.getContentResolver(),

                Settings.Secure.ANDROID_ID);
        item.setUserid(android_id);
        payload.setItem(item);
        Post post = new Post(transaction, "parknow", payload);
        iPostCallParkingSpots = PostCallAPI.getAPIService();
        Call<POST> call = iPostCallParkingSpots.createPost(post);
        call.enqueue(new Callback<POST>() {
            @Override
            public void onResponse(Call<POST> call, Response<POST> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(binding.mapRelLayout, getString(R.string.save_spot_saved), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<POST> call, Throwable throwable) {
                Log.e("ERROR", "Unable to call POST call API.");

            }
        });
    }
}