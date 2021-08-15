package com.sjsu.parknow;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.sjsu.parknow.databinding.FragmentNearbyBinding;
import com.sjsu.parknow.model.AdapterDataItem;
import com.sjsu.parknow.model.GoogleResponse;
import com.sjsu.parknow.model.Result;
import com.sjsu.parknow.model.SpotResult;
import com.sjsu.parknow.model.SpotsResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyFragment extends Fragment {
    private FragmentNearbyBinding binding;
    GoogleResponse googleResponse;
    SpotsResponse spotsResponse;
    String userLocation;
    String userCurAddress;
//    ArrayList<Result> data = new ArrayList<>();
    ArrayList<AdapterDataItem> adapterData = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    public NearbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearbyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyFragment newInstance(String param1, String param2, String param3) {
        NearbyFragment fragment = new NearbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM2, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
        googleResponse = NearbyFragmentArgs.fromBundle(getArguments()).getGoogleResponse();
        spotsResponse = NearbyFragmentArgs.fromBundle(getArguments()).getParkingSpotResponse();
        userLocation = NearbyFragmentArgs.fromBundle(getArguments()).getUserLocation();
        String[] locs = userLocation.split(",");
        Double latitude = Double.valueOf(locs[0]);
        Double longitude = Double.valueOf(locs[1]);
        LatLng userLatLng = new LatLng(latitude, longitude);
        userCurAddress = getAddressFromLatLngCord(userLatLng).getAddressLine(0);

        if(spotsResponse != null){

            for(SpotResult res : spotsResponse.getResults()){
                AdapterDataItem item = new AdapterDataItem();
                item.setDistance(res.getDistance());
                item.setLatitude(res.getLatitude());
                item.setLongitude(res.getLongitude());
                item.setName(res.getName());
                item.setOpenNow(null);
                item.setRating(null);
                LatLng latLng = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
                String curAddress = getAddressFromLatLngCord(latLng).getAddressLine(0);
                item.setAddress(curAddress);
                item.setSource("PARKNOW");
                adapterData.add(item);
            }
        }
        if(googleResponse != null) {
            for(Result res : googleResponse.getResults()){
                AdapterDataItem item = new AdapterDataItem();
                item.setDistance("N.A.");
                item.setLatitude(res.getGeometry().getLocation().getLat().toString());
                item.setLongitude(res.getGeometry().getLocation().getLng().toString());
                item.setName(res.getName());
                if(res.getOpeningHours() != null){
                    item.setOpenNow(res.getOpeningHours().getOpenNow());
                }else{
                    item.setOpenNow(null);
                }
                item.setRating(res.getRating());
                LatLng latLng = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
                String curAddress = getAddressFromLatLngCord(latLng).getAddressLine(0);
                item.setAddress(curAddress);
                item.setSource("GOOGLE");
                adapterData.add(item);
            }
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNearbyBinding.inflate(inflater, container, false);
        RecyclerView recyclerCard = binding.cardRecyclerView;
       recyclerCard.setAdapter(new MainCardAdapter(getActivity().getApplicationContext(), adapterData));
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_nearby, container, false);
        return binding.getRoot();
    }

    private Address getAddressFromLatLngCord(LatLng latLng) {
        Geocoder geoCoder = new Geocoder(requireActivity().getApplicationContext());
        List<android.location.Address> addressesList;
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

    public class MainCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleView;
        public TextView distanceView;
        public TextView timeView;
        public  TextView eta;
        public View item;
        public TextView openNowView;
        public RatingBar ratingBarView;
        public ImageView imageView;
        public TextView addressView;

        public MainCardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            timeView = itemView.findViewById(R.id.time);
//            eta = itemView.findViewById(R.id.eta);
            distanceView = itemView.findViewById(R.id.distance);
            item = itemView;
            openNowView = itemView.findViewById(R.id.open);
            ratingBarView = itemView.findViewById(R.id.ratingBar);
            imageView = itemView.findViewById(R.id.imageView);
            addressView = itemView.findViewById(R.id.parking_address);

        }

        @Override
        public void onClick(View v) {

            //

        }
    }

    public class MainCardAdapter extends RecyclerView.Adapter<NearbyFragment.MainCardViewHolder> {
        private ArrayList<AdapterDataItem> dataList;
        private Context context;

        public MainCardAdapter(Context context, ArrayList<AdapterDataItem> dataList) {
            this.context = context;
            this.dataList = dataList;
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

        @NonNull
        @Override
        public MainCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView itemView = (CardView) LayoutInflater.from(context).inflate(R.layout.cardview_item, parent, false);

            return new MainCardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MainCardViewHolder holder, int position) {
            AdapterDataItem item = dataList.get(position);
//            Calendar cal = Calendar.getInstance();
//            TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
//            String durationValue = item.getDurationValue();
//            holder.timeView.setText("Time: " + item.getDurationText());
//            holder.timeView.setText("Time: 5 mins (ETA: 14:35:00)");
//            cal.add(Calendar.SECOND, Integer.parseInt(durationValue));
//            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//            dateFormat.setTimeZone(tz);
//            String time = dateFormat.format(cal.getTime());
//            holder.timeView.setText("ETA: " + time);
//            holder.timeView.setText("ETA: 14:35:00");
            holder.titleView.setText(item.getName());
//            holder.distanceView.setText("Distance: " + item.getDistanceText());
            holder.distanceView.setText("Distance: " + item.getDistance() + "meters");
            if(item.getOpenNow() != null){
                Boolean isOpen = item.getOpenNow();
                holder.openNowView.setText(isOpen ? "Open" : "Closed");
                if (isOpen){
                    holder.openNowView.setText("Open");
                    holder.openNowView.setTextColor(ContextCompat.getColor(context, R.color.green));
                }else{
                    holder.openNowView.setText("Closed");
                    holder.openNowView.setTextColor(ContextCompat.getColor(context, R.color.red));
                }
            }else{
                holder.openNowView.setText("Open/Closed N.A.");
            }

            holder.addressView.setText("Address: " + item.getAddress());
            if(item.getSource().equals("GOOGLE")){
                holder.imageView.setImageResource(R.drawable.park_grey_jpg);
            }else{
                holder.imageView.setImageResource(R.drawable.park_green_jpg);
            }
            if (item.getRating() != null) {
//                holder.ratingBarView.setRating(Float.parseFloat(item.getRating()));
                holder.ratingBarView.setRating(item.getRating());
            }else{
                holder.ratingBarView.setVisibility(View.GONE);
            }
            //open google maps
            holder.titleView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String directionAddressString = encodeAddressString(item.getAddress());
                    String mapsURL = "https://www.google.com/maps/dir/" + userCurAddress +  "/" + directionAddressString;   //used for opening google maps;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsURL));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }
}