package com.sjsu.parknow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.sjsu.parknow.databinding.FragmentMapsBinding;
import com.sjsu.parknow.model.Geometry;
import com.sjsu.parknow.model.GoogleResponse;
import com.sjsu.parknow.model.Result;
import com.sjsu.parknow.network.GooglePlacesAPI;
import com.sjsu.parknow.network.IGooglePlaces;
import com.sjsu.parknow.utils.PlaceAutoComplete;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {
    private static final String TAG = MapsFragment.class.getSimpleName();
    private FragmentMapsBinding binding;
    private GoogleMap map;
    private GoogleResponse googleResults;
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

                    TextView title = infoWindow.findViewById(R.id.title);
                    title.setText(marker.getTitle());

                    TextView snippet = infoWindow.findViewById(R.id.snippet);
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }
            });
            // Prompt the user for permission.
            getLocationPermission();
            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }
    };

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
                    LatLng curLatLng = new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude());
                    curKnownLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                    curKnownAddress = getAddressFromLatLngCord(curLatLng).getAddressLine(0);
                    callAPI(curKnownLocation);
                    map.clear();
                    map.addMarker(new MarkerOptions().position(curLatLng).title(curKnownAddress));
                    binding.inputSearch.setText(curKnownAddress);
                    return false;
                }
            });
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            LatLng latLng = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());
                            curKnownAddress = getAddressFromLatLngCord(latLng).getAddressLine(0);
                            curKnownLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                            if (lastKnownLocation != null) {
                                map.clear();
                                map.addMarker(new MarkerOptions().position(latLng).title(curKnownAddress));
                                binding.inputSearch.setText(curKnownAddress);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

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
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                curKnownAddress = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void callAPI(String location) {
        callGoogleNearbyAPI(location);
        callLambdaAPI(location);
    }
    private void callGoogleNearbyAPI(String location){
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
    private void callLambdaAPI(String location){
        //Call Lambda API to fetch the nearby parking locations stored in our database.
        //It need not be a Lambda API, we could even use Firebase directly to get the required results
    }

    public void handleResponse(ArrayList<Result> results) {
        if (results != null) {
            setResultMarkers(results);
//            binding.cardRecyclerView.setAdapter(new MainCardAdapter(requireActivity().getApplicationContext(), getData()));
        }
//        binding.resultImageView.setImageBitmap(getBitMapImage());
    }
    private void setResultMarkers(ArrayList<Result> results) {
        for(Result res : results){
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
                curKnownLocation = latLngCoordinates.latitude + "," + latLngCoordinates.longitude;
                curKnownAddress = parent.getItemAtPosition(position).toString();
                map.clear();
                map.addMarker(new MarkerOptions().position(latLngCoordinates).title(curKnownAddress));
                callAPI(curKnownLocation);
                binding.inputSearch.setText(curKnownAddress);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCoordinates, DEFAULT_ZOOM));
                Log.i("Address : ", String.valueOf(latLngCoordinates));
                InputMethodManager in = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
        binding.openList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.openList), Toast.LENGTH_LONG).show();
                openNearbyList(view);
            }});
//        return inflater.inflate(R.layout.fragment_maps, container, false);
        return binding.getRoot();
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

    private void openNearbyList(View view){
       GoogleResponse response = getGoogleResults();
        MapsFragmentDirections.ActionMapsFragmentToNearbyFragment action = MapsFragmentDirections.actionMapsFragmentToNearbyFragment(curKnownLocation, response);
        action.setUserLocation(curKnownLocation);
        action.setGooglePlacesResults(response);
        Navigation.findNavController(view).navigate(action);
    }
    private void setGoogleResults(GoogleResponse results){
        this.googleResults = results;
    }
    private GoogleResponse getGoogleResults(){
        return googleResults;
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
}