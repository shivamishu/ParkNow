package com.sjsu.parknow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sjsu.parknow.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;

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
                }
                //maintain Navigation view standard behavior
                NavigationUI.onNavDestinationSelected(item, navController);
                //close drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
//        FloatingActionButton fab = findViewById(R.id.add_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Parking Location Stored", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build()
//        );
//        if (savedInstanceState == null) {
//            showSignInOptions();
//        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.myNavHostFragment);
        return NavigationUI.navigateUp(navController, drawerLayout);
    }
}