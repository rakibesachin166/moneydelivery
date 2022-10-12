package com.sacdev.moneydelivery;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {


    EditText amountvalueedt;
    TextView nametxtid, deliverychargetxt, cashtaxtxt, totalvaluetxt, addresstxt, orderbtn , usethidlocatiobtn;
    ImageButton logoutbtn;
    int deliverycharge = 40;
    FrameLayout mapframly;
    LinearLayout linearLayout;
    SupportMapFragment supportMapFragment;
    GoogleMap gmap;
    LocationSettingsRequest.Builder builder;
    SettingsClient sclient;
    Task<LocationSettingsResponse> task;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    Location currentlocation ;
     Marker marker;
    Geocoder geocoder  ;

    public HomeFragment() {
        // Required empty public constructor
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            gmap = googleMap;
            gmap.getUiSettings().setCompassEnabled(true);
            gmap.getUiSettings().setZoomControlsEnabled(true);
            gmap.getUiSettings().setZoomGesturesEnabled(true);
            gmap.getUiSettings().setMyLocationButtonEnabled(true);
            gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            getlocationpermision(true);
            gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                  setupmarker(latLng);
                  getsetdeliveryAdreess(latLng);
                }
            });

            gmap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    getsetdeliveryAdreess(marker.getPosition());
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });

        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        nametxtid = view.findViewById(R.id.nametextid);
        amountvalueedt = view.findViewById(R.id.amountedittexthome_id);
        deliverychargetxt = view.findViewById(R.id.deliverychargetxt_id);
        cashtaxtxt = view.findViewById(R.id.cashtaxtextview_id);
        totalvaluetxt = view.findViewById(R.id.totalvaluetextview_id);
        orderbtn = view.findViewById(R.id.orderbtn_id);
        addresstxt = view.findViewById(R.id.AddresssetTextview_id);
        logoutbtn = view.findViewById(R.id.logoutbtn);
        mapframly = view.findViewById(R.id.framlayoutmapholder_id);
        linearLayout = view.findViewById(R.id.homerelativelayout2_id);
         usethidlocatiobtn = view.findViewById(R.id.usethislocation_id);
        addresstxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupmap();
            }
        });
       usethidlocatiobtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (marker!=null){
                   if (mapframly.getVisibility() == View.VISIBLE) {
                       mapframly.setVisibility(View.GONE);
                   }

               }else{
                  Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Select Delivery Location on Map", Snackbar.LENGTH_LONG)
                           .setTextColor(Color.RED)
                           .setBackgroundTint(Color.WHITE)
                           .show();
               }
           }
       });
        amountvalueedt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int amt = Integer.parseInt(amountvalueedt.getText().toString());
                    if (amt > 0) {
                        float cashcharge = (float) (0.015 * amt);
                        deliverychargetxt.setText(String.valueOf(deliverycharge));
                        cashtaxtxt.setText(String.valueOf(cashcharge));
                        totalvaluetxt.setText(String.valueOf(amt + cashcharge + deliverycharge));

                    } else {
                        setemptytext();

                    }
                } catch (Exception e) {
                    setemptytext();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starterclass.firebaseAuth.signOut();
                startActivity(starterclass.changeActivity(getContext(), LoginActivity.class));
            }
        });
        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(amountvalueedt.getText())){
                    amountvalueedt.setError("Enter Amount");
                    return;
                }
                if(marker==null){
                    addresstxt.setError("Please Set Location");
                    return;
                }
                int amt = Integer.parseInt(amountvalueedt.getText().toString());
                if(amt==0){
                    amountvalueedt.setError("Enter Amount");
                    return;
                }
                ProgressDialog proDialog = ProgressDialog.show(getContext(), "", "Please Wait");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Longitude", String.valueOf(marker.getPosition().longitude));
                hashMap.put("Latitude", String.valueOf(marker.getPosition().latitude));
                hashMap.put("Address",addresstxt.getText().toString());

                hashMap.put("Amount",String.valueOf(amt));
                hashMap.put("Status","Pending");
                hashMap.put("GrandTotal",totalvaluetxt.getText().toString());

                FirebaseDatabase.getInstance().getReference().child("RequestPending")
                        .child(starterclass.selfuserid).push().setValue(hashMap).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused)
                            {
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Ordered Successfully", Snackbar.LENGTH_LONG)
                                        .setTextColor(Color.BLACK)
                                        .setBackgroundTint(getResources().getColor(R.color.lightgreen))
                                        .show();
                                proDialog.dismiss();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framentholder_id, new HomeFragment()).addToBackStack(null).commit();
                            }
                        });

            }
        });
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        return view;
    }

    private void setupmap()
    {
        if (mapframly.getVisibility() == View.GONE) {
            mapframly.setVisibility(View.VISIBLE);
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(callback);
        }
    }

    private void setemptytext() {
        String temp = "00";
        deliverychargetxt.setText(temp);
        cashtaxtxt.setText(temp);
        totalvaluetxt.setText(temp);
    }
    private void getlocationpermision(boolean f) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        sclient = LocationServices.getSettingsClient(getContext());
        task = sclient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    gmap.setMyLocationEnabled(true);
                    starterclass.positionpermission = true;
                   setuplocation();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        if (f){
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                            getlocationpermision(false);
                        }
                        else
                        {
                              starterclass.positionpermission = false;
                            setuplocation();
                        }

                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }


    private void setuplocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        ((Task) task).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentlocation = location;
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()),15f));
                    setupmarker(new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude()));
                }else
                {
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.001918559789104, 73.79558068462165),5f));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.001918559789104, 73.79558068462165),5f));
            }
        });

    }

    private void setupmarker(LatLng latLng)
    {
        if(marker!=null){
            marker.remove();
        }
        marker = gmap.addMarker(new MarkerOptions()
                .title("Delivery Location")
                .position(latLng)
                .draggable(true));
        getsetdeliveryAdreess(latLng);

    }

    private void getsetdeliveryAdreess(LatLng latLng)
    {
        try {
            List<Address> addressList =   geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList.size()>0){
                Address address = addressList.get(0);
                String stretadress = address.getAddressLine(0);
                 Snackbar.make(getActivity().findViewById(android.R.id.content), ""+stretadress, Snackbar.LENGTH_LONG)
                           .setTextColor(Color.BLACK)
                           .setBackgroundTint(Color.WHITE)
                           .show();

                addresstxt.setText(stretadress);
            }

        } catch (Exception e){

            Log.e("adreeserror",e.getMessage());
        }
    }



    private void emptycode(){

      /*  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        PlacesClient placesClient;
        FusedLocationProviderClient fusedLocationProviderClient;
        Location lastKnownLocation;
        LocationManager locationManager;
        Marker marker;
        Geocoder geocoder;
        Location currentLocation;
        GoogleMap gmap;
        SupportMapFragment mapFragment;




        Places.initialize(getContext(), getString(R.string.Mapapikey));
        geocoder = new Geocoder(getContext());
        placesClient = Places.createClient(getContext());
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_ID);
        mapFragment.getMapAsync(HomeFragment.this::onMapReady);



        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
         @Override
    public void onMapReady(GoogleMap map) {
        gmap = map;
      gmap.getUiSettings().setCompassEnabled(true);
       gmap.getUiSettings().setZoomControlsEnabled(true);
       gmap.getUiSettings().setZoomGesturesEnabled(true);
       gmap.getUiSettings().setMyLocationButtonEnabled(true);
       gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
       if (currentLocation!=null){
                   gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),15f));
       } else{
              //   gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.001918559789104, 73.79558068462165),5f));
       }

       gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
           @Override
           public void onMapClick(@NonNull LatLng latLng) {
               if(marker!=null){
                   marker.remove();
               }
               marker = gmap.addMarker(new MarkerOptions()
                       .title("Choos")
                       .position(latLng)
                       .draggable(true));
              getdeliveryAdreess(latLng);
           }
       });
       gmap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
           @Override
           public void onMarkerDrag(@NonNull Marker marker) {

           }

           @Override
           public void onMarkerDragEnd(@NonNull Marker marker) {
                getdeliveryAdreess(marker.getPosition());
           }

           @Override
           public void onMarkerDragStart(@NonNull Marker marker) {

           }
       });
    }


          public void getdeliveryAdreess(LatLng latLng){
        try {
                   List<Address>  addressList =   geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                   if (addressList.size()>0){
                                    Address address = addressList.get(0);
                       Toast.makeText(getContext(), ""+address, Toast.LENGTH_SHORT).show();
                                    String stretadress = address.getAddressLine(0);
                                    addresstxt.setText(stretadress);
            }

        } catch (Exception e){
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }

        */
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}