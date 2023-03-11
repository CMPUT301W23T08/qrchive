package com.example.qrchive.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.qrchive.Fragments.CodesFragment;
import com.example.qrchive.Fragments.FriendsFragment;
import com.example.qrchive.Fragments.HomeFragment;
import com.example.qrchive.Fragments.LoginDialogFragment;
import com.example.qrchive.Fragments.ProfileFragment;
import com.example.qrchive.Fragments.ScanFragment;
import com.example.qrchive.Fragments.SettingsFragment;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/* For BottomNavItemListener: https://stackoverflow.com/questions/68021770/setonnavigationitemselectedlistener-deprecated
 *  For BottomNavImpl: https://www.geeksforgeeks.org/bottomnavigationview-inandroid/
 * */


public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    SharedPreferences preferences; //IMP: This will work as a 'singleton pattern'/'a global struct' to save all (mostly static) required preferences
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firestore setup
        db =  FirebaseFirestore.getInstance();

        // preferences setup
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        preferences.edit().clear().apply(); // clear any previous preferences

        // Check for Device ID
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
                            new LoginDialogFragment(db, preferences, android_device_id).show(getSupportFragmentManager(), "Login Dialog");
                        }
                        else {
                            DocumentSnapshot userDoc = resultantDocuments.get(0);
                            SharedPreferences.Editor prefEditor = preferences.edit();
                            prefEditor.putString("userName", userDoc.getData().get("userName").toString());
                            prefEditor.putString("emailID", userDoc.getData().get("emailID").toString());
                            prefEditor.putString("deviceID", android_device_id);
                            prefEditor.putString("userDID", userDoc.getId());
                            prefEditor.apply();
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
                        transactFragment(new ProfileFragment());
                        break;
                    case R.id.menu_dropdown_map:
                        Intent showMap = new Intent(MainActivity.this, MapsActivity.class);
                        showMap.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(showMap);
                        break;

                    case R.id.menu_dropdown_settings:
                        //todo
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
                        transactFragment(new CodesFragment());
                        break;
                    case R.id.menu_item_friends:
                        //todo
                        transactFragment(new FriendsFragment());
                        break;
                    case R.id.menu_item_scan:
                        //todo
                        transactFragment(new ScanFragment());
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

        // Make app default load the home fragment (we will add a conditional here later to test if the user has already created
        // an account before, If not then we show the create account fragment by default.)
        transactFragment(new HomeFragment());
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment())
//                .commit();

    }

    /**
     * Inflate the XML for the Toolbar in order to define search bar functionality.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search for QR . . .");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Handle search query submission
                Log.d("onSumbit", "onQueryTextSubmit: ");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Handle search query text change (offer suggestion for partial input)
                Log.d("onChange", "onQueryTextSubmit: ");
                return true;
            }
        });


        return true;
    }

    private void transactFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();
        return;
    }
}