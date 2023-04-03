package com.example.qrchive.Activities;

import androidx.annotation.NonNull;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.Fragments.CodesFragment;
import com.example.qrchive.Fragments.FriendsFragment;
import com.example.qrchive.Fragments.HomeFragment;
import com.example.qrchive.Fragments.LoginDialogFragment;
import com.example.qrchive.Fragments.MapsFragment;
import com.example.qrchive.Fragments.ProfileFragment;
import com.example.qrchive.Fragments.SearchResultFragment;
import com.example.qrchive.R;
import com.example.qrchive.Fragments.ScanFragment;
import com.example.qrchive.Fragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoginDialogFragment.OnLoginSuccessListener{

    FirebaseWrapper fbw;
    SharedPreferences preferences; //IMP: This will work as a 'singleton pattern'/'a global struct' to save all (mostly static) required preferences
    private static final int REQUEST_CODE_FINE_LOCATION = 200;
    private MainActivity mainActivity = this;

    private static MainActivity instance;

    public static MainActivity getInstance(){
        return instance;
    }

    @Override
    public void onLoginSuccess(String userDID, String userName) {
        fbw = new FirebaseWrapper(userDID, userName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        // Firestore setup
        FirebaseFirestore db =  FirebaseFirestore.getInstance();

        // preferences setup
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.clear().apply(); // clear any previous preferences


        // Check for Device ID and then set up the firebase wrapper object
        String android_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("Users").whereEqualTo("deviceID", android_device_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // User already exists in database
                        List<DocumentSnapshot> resultantDocuments = task.getResult().getDocuments();

                        if (resultantDocuments.size() == 0) {
                            // Make a dialog box to take user input
                            // TODO: Make sure unique username
                            Pair<ArrayList<String>, ArrayList<String>> usernameAndEmailList = new Pair<>(new ArrayList<>(), new ArrayList<>());
                            db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<DocumentSnapshot> resultantDocuments = task.getResult().getDocuments();
                                    for (DocumentSnapshot doc : resultantDocuments) {
                                        usernameAndEmailList.first.add(doc.get("userName").toString());
                                        usernameAndEmailList.second.add(doc.get("emailID").toString());
                                    }
                                }
                            });
                            LoginDialogFragment fragment = new LoginDialogFragment(db, preferences, android_device_id, usernameAndEmailList);
                            fragment.setCancelable(false); // disables back button
                            fragment.show(getSupportFragmentManager(), "Login Dialog");
                        }
                        else {
                            DocumentSnapshot userDoc = resultantDocuments.get(0);
                            prefEditor.putString("userName", userDoc.getData().get("userName").toString());
                            prefEditor.putString("emailID", userDoc.getData().get("emailID").toString());
                            prefEditor.putString("deviceID", android_device_id);
                            prefEditor.putString("userDID", userDoc.getId());
                            prefEditor.apply();
                            onLoginSuccess(preferences.getString("userDID", ""),
                                    preferences.getString("userName", ""));
                        }
                    }
                });

        //Dropdown Nav handler
        BottomNavigationView dropdownNav = findViewById(R.id.dropdown_navigation);
        LinearLayout dropdownNavWrapper = (LinearLayout) findViewById(R.id.dropdown_navigation_wrapper);
        dropdownNavWrapper.setVisibility(View.GONE);
        dropdownNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){ //check id
                    case R.id.menu_dropdown_profile:
                        transactFragment(new ProfileFragment(
                                new Player(
                                        preferences.getString("userName", ""),
                                        preferences.getString("emailID", ""),
                                        preferences.getString("deviceID", ""),
                                        preferences.getString("userDID", "")
                                ), fbw));
                        break;
                    case R.id.menu_dropdown_map:
                        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // Request permission before launching fragment.
                            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
                        } else {
                            // Permission already granted
                            transactFragment(new MapsFragment());
                        }
                        break;
                    case R.id.menu_dropdown_settings:
                        transactFragment(new SettingsFragment());
                        break;
                }
                dropdownNavWrapper.setVisibility(View.GONE);
                return true;
            }
        });

        //Set onclick listener on the Fragment container to close the dropdown when clicked.
        FrameLayout fragmentContianer = findViewById(R.id.fragment_container);
        fragmentContianer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dropdownNavWrapper.getVisibility() == View.VISIBLE) {
                    dropdownNavWrapper.setVisibility(View.GONE);
                }
            }
        });

        //Bottom Nav Handler
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){ //check id
                    case R.id.menu_item_home:
                        transactFragment(new HomeFragment());
                        break;
                    case R.id.menu_item_codes:
                        transactFragment(new CodesFragment(fbw));
                        break;
                    case R.id.menu_item_friends:
                        transactFragment(new FriendsFragment(fbw));
                        break;
                    case R.id.menu_item_scan:
                        transactFragment(new ScanFragment(fbw));
                        break;
                }
                dropdownNavWrapper.setVisibility(View.GONE);
                return true;
            }
        });

        //get the toolbar and menu
        Toolbar topBar = (Toolbar) findViewById(R.id.app_bar);
        Menu topBarMenu = topBar.getMenu();
        onCreateOptionsMenu(topBarMenu);
        handleDropdownMenuWrapper(topBarMenu, dropdownNavWrapper);

        // Make app default load the home fragment
        transactFragment(new HomeFragment());
    }

    /**
     * @method:
     * render the maps fragment based on whether the user allows or denys the
     * current location.
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_FINE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted, launch fragment
            transactFragment(new MapsFragment());
        } else {
            // permission not granted, show error message or take appropriate action
            Toast.makeText(this, "Without location permissions maps features are limited.", Toast.LENGTH_SHORT).show();
            transactFragment(new MapsFragment());
        }
    }

    /** @method:
     * Inflate the XML for the Toolbar in order to define search bar functionality.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("========================== HER==============", "onCreateOptionsMenu: ");
        //load the menu XML into memory.
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        //hide the geo_search icon since we are not in maps fragment
        MenuItem geo_search = menu.findItem(R.id.menu_geo_search);
        geo_search.setVisible(false);

        //listener for text search.
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search for users . . .");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Handle search query submission
                transactFragment(new SearchResultFragment(query, fbw));
                Log.d("onSumbit", "onQueryTextSubmit: ");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Handle search query text change (offer suggestion for partial input)
                transactFragment(new SearchResultFragment(newText, fbw));
                Log.d("onChange", "onQueryTextSubmit: ");
                return true;
            }
        });

        return true;
    }

    /** When a menu item on the dropdown is clicked, we like to hide the visibility of
     * the dropdown when the new fragment is rendered.
     * */
    private void handleDropdownMenuWrapper(Menu topBarMenu, LinearLayout dropdownNavWrapper) {

        //grab menu items
        MenuItem itemDropdown = topBarMenu.findItem(R.id.menu_item_dropdown);

        //handle dropdown menu clicked
        itemDropdown.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Log.d("clicked the dropdown icon: ", "onMenuItemClick: ");
                int viewState = dropdownNavWrapper.getVisibility();

                if (viewState == View.GONE) {
                    dropdownNavWrapper.setVisibility(View.VISIBLE);
                } else if (viewState == View.VISIBLE) {
                    dropdownNavWrapper.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void transactFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();
        return;
    }

    /**@method:
     * Utility method so we don't have to pass instance of firebase to every new Fragment
     * through the constructor
     * */
    public FirebaseWrapper getFirebaseWrapper() {
        return fbw;
    }

    /** @method: Utility class to convert Filename to Resource ID
     * Non-android classes which don't have access to activity context, such as ScannedCode or MyScannedCodeCardRecyclerViewAdapter
     * need to retrieve the Resource ID from the monster filename at runtime. This method will return the resource id using the
     * getResource method on Activity given the filename as a parameter.
     */
    public int getDrawableResourceIdFromString(String filename) {
        return getResources().getIdentifier(filename, "drawable", getPackageName());
    }
}
