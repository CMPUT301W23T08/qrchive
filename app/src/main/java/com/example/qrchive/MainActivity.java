package com.example.qrchive;

import androidx.annotation.NonNull;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/* For BottomNavItemListener: https://stackoverflow.com/questions/68021770/setonnavigationitemselectedlistener-deprecated
*  For BottomNavImpl: https://www.geeksforgeeks.org/bottomnavigationview-inandroid/
* */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        //transactFragment();
                        break;
                }
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
                return false;
            }
        });

        // Make app default load the home fragment (we will add a conditional here later to test if the user has already created
        // an account before, If not then we show the create account fragment by default.)
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment())
                .commit();

    }

    /**
     * Inflate the XML for the Toolbar in order to define search bar functionality.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                Log.d("onSumbit", "onQueryTextSubmit: ");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change
                Log.d("onchange", "onQueryTextSubmit: ");
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