package com.example.qrchive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){ //check id
                    case R.id.menu_home:
                        transactFragment(new HomeFragment());
                        break;
                    case R.id.menu_codes:
                        transactFragment(new CodesFragment());
                        break;
                    case R.id.menu_friends:
                        //todo
                        transactFragment(new FriendsFragment());
                        break;
                    case R.id.menu_scan:
                        //todo
//                        transactFragment();
                        break;
                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void transactFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();
        return;
    }
}