package com.example.qrchive.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;
import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.Classes.GeoSearchArrayAdapter;
import com.example.qrchive.Classes.MapModel;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.Classes.GeoQueryListener;
import com.example.qrchive.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements GeoQueryListener {

    private String TAG = "=================== HERE ====================";
    private MapsFragment self;
    private GoogleMap mMap;
    private MainActivity mainActivity;
    private LocationManager locationManager;
    private Location currentLocation;
    private MapModel mapModel;
    private MenuItem geoSearchItem;
    private MenuItem textSearchItem;
    private Map<Marker, ScannedCode> markerCodeMap = new HashMap<>();
    private ArrayList<ScannedCode> geoQueryList;
    private GeoSearchArrayAdapter geoQueryAdapter;
    private View mapsView;
    //GeoSearch views
    private LinearLayout geoSearchLayout;
    private ScrollView scrollView;
    //Geo Search Inputs
    private EditText latitudeText;
    private EditText longitudeText;
    private EditText searchRadiusText;
    // static
    private static final int REQUEST_CODE_FINE_LOCATION = 200;
    private final static int ZOOM_LEVEL = 16;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;
            mMap.getUiSettings().setMapToolbarEnabled(true);

            try {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json ));
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find the style json", e);
            }

            // Permission Check;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
                return;
            }

            // Get device location
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (currentLocation == null) {
                Toast.makeText(getActivity(), "Navigate to settings to enable location services", Toast.LENGTH_SHORT).show();
                return;
            }

            handleMarkerClickDialogue();

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    Log.d(TAG, "onMapClick: ");
                    geoSearchLayout.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(View.INVISIBLE);
                }
            });

            moveCameraToLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), ZOOM_LEVEL);
            scatterQRLocations();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mapsView = inflater.inflate(R.layout.fragment_maps, container, false);

        //Set some Fragment class members that don't depend on XML inflation.
        this.mainActivity = (MainActivity) getActivity();
        self = this;
        geoQueryList = new ArrayList<>();
        geoQueryAdapter = new GeoSearchArrayAdapter(mainActivity, R.layout.item_geo_search, geoQueryList);
        mapModel = new MapModel();

        return mapsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        // Set class members
        Toolbar topBar = (Toolbar) mainActivity.findViewById(R.id.app_bar);
        Menu topBarMenu = topBar.getMenu();
        geoSearchLayout = mainActivity.findViewById(R.id.maps_geo_search_wrapper);
        scrollView = mainActivity.findViewById(R.id.geo_search_scroll_view);
        scrollView.setVisibility(View.INVISIBLE);
        geoSearchItem = topBarMenu.findItem(R.id.menu_geo_search);
        textSearchItem = topBarMenu.findItem(R.id.menu_search);
        latitudeText = mainActivity.findViewById(R.id.latitude_input);
        longitudeText = mainActivity.findViewById(R.id.longitude_input);
        searchRadiusText = mainActivity.findViewById(R.id.search_radius_input);

        // Set initial visibilities
        geoSearchItem.setVisible(true);
        textSearchItem.setVisible(false);
        geoSearchLayout.setVisibility(View.INVISIBLE);

        // Handle requests
        handleGeoSearchMenuItem();
        handleGeoSearchCurrentLocationButton();
        handleGeoSearchSubmit();
        handleGeoSearchListView();
    }

    public void handleMarkerClickDialogue() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                ScannedCode markerCode = markerCodeMap.get(marker);
                if (markerCode != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(markerCode.getName())
                            .setMessage("Show the QR code?")
                            .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OnClickCodeFragment(markerCode, mainActivity.getFirebaseWrapper()))
                                            .commit();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return false;
            }
        });
    }

    public void handleGeoSearchCurrentLocationButton() {
        Button currentLocationButton = mainActivity.findViewById(R.id.button_geo_search_use_current_loc);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentLocation == null) {
                    Toast.makeText(mainActivity, "Current Location Unavailable", Toast.LENGTH_SHORT).show();
                    return;
                }

                double currentLatitude = currentLocation.getLatitude();
                double currentLongitude = currentLocation.getLongitude();

                // cast the latitude and longitude into strings
                String latitudeStr = String.format("%.4f", currentLatitude);
                String longitudeStr = String.format("%.4f", currentLongitude);

                // set the strings to the EditText fields
                latitudeText.setText(latitudeStr);
                longitudeText.setText(longitudeStr);
            }
        });
    }

    public void handleGeoSearchMenuItem() {
        LinearLayout dropdownNavWrapper = (LinearLayout) mainActivity.findViewById(R.id.dropdown_navigation_wrapper);
        geoSearchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                scrollView.setVisibility(View.INVISIBLE);
                dropdownNavWrapper.setVisibility(View.GONE);
                int searchVisibility = geoSearchLayout.getVisibility();
                if (searchVisibility == View.VISIBLE) {
                    geoSearchLayout.setVisibility(View.INVISIBLE);
                } else {
                    geoSearchLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    public void handleGeoSearchSubmit() {

        Button submitButton = mainActivity.findViewById(R.id.button_geo_search_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float longitude = 0.0f;
                float latitude = 0.0f;
                float radius = 0.0f;

                geoQueryAdapter.clear();

                try {
                    longitude = Float.parseFloat(longitudeText.getText().toString());
                    if (latitude < -180 || latitude > 180) {
                        Toast.makeText(mainActivity, "Invalid latitude value", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(mainActivity, "Invalid longitude value", Toast.LENGTH_SHORT).show();
                    return;
                } try {
                    latitude = Float.parseFloat(latitudeText.getText().toString());
                    if (latitude < -90 || latitude > 90) {
                        Toast.makeText(mainActivity, "Invalid latitude value", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(mainActivity, "Invalid latitude value", Toast.LENGTH_SHORT).show();
                    return;
                } try {
                    radius = Float.parseFloat(searchRadiusText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(mainActivity, "Invalid radius value", Toast.LENGTH_SHORT).show();
                    return;
                }

                // flush the previous list
                mapModel.searchGeoQuery(latitude, longitude, radius, self);
                scrollView.setVisibility(View.VISIBLE);
                geoSearchLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void handleGeoSearchListView() {
        ListView listView = mainActivity.findViewById(R.id.geo_search_list_view);
        listView.setAdapter(geoQueryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ScannedCode code = (ScannedCode) listView.getAdapter().getItem(i);
                Log.d(TAG, code.getName());

                GeoPoint location = code.getLocation();
                if (location != null) {
                    moveCameraToLocation(location.getLatitude(), location.getLongitude(), ZOOM_LEVEL + 3);
                    scrollView.setVisibility(View.INVISIBLE);
                    geoSearchLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /** @method: update current location.
     * */
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    /** @method: Pan the Camera to the current location of the user.
     * */
    private void moveCameraToLocation(double latitude, double longitude, int zoom) {

        // Website: JavaPoint
        // Answer: https://www.javatpoint.com/android-google-map-displaying-current-location
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
    }

    /** @method: Draw a code marker on the map with the name of the QR and
     * the points associated.
     * */
    private void drawCodeMarker(ScannedCode code) {
        GeoPoint location = code.getLocation();
        String name = code.getName();
        String points = String.valueOf(code.getPoints());

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_qr_marker));

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(name)
                .snippet(points + " pts")
                .flat(true)
                .icon(icon));

        markerCodeMap.put(marker, code);
        Log.d(TAG, "ADDED CODE TO MAP ");
    }

    /**
     * @method: scatter some QR codes around the current location of the user.
     * */
    private void scatterQRLocations() {
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();
        mapModel.setMapGeoQuery(latitude, longitude, 100, self);
    }

    /** @method: Draw a code marker on the map with the name of the QR and
     * the points associated.
     * */
    @Override
    public void onCodeGeoQueried(ScannedCode code) {
        Log.d(TAG, "onCodeGeoQueried: ");
        GeoPoint location = code.getLocation();
        String name = code.getName();
        String points = String.valueOf(code.getPoints());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(name)
                .snippet(points + " pts")
                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        Log.d(TAG, code.getName());
        markerCodeMap.put(marker, code);
        Log.d(TAG, String.valueOf(markerCodeMap.size()));
    }

    /** @method: Draw a code marker on the map with the name of the QR and
     * the points associated.
     * */
    @Override
    public void onCodeGeoSearched(ScannedCode code) {
        // Add queried code to geoQueryList
        if (code != null) {
            Log.d(TAG, code.getName());
            geoQueryAdapter.add(code);
        }
    }

    /** @method: Swap the search menu item back to the basic text search.
     * */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        geoSearchItem.setVisible(false);
        textSearchItem.setVisible(true);
    }
}