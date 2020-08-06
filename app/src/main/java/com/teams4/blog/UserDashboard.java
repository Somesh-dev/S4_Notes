package com.teams4.blog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.teams4.blog.fragments.DashboardFragment;
import com.teams4.blog.fragments.ManageFragment;
import com.teams4.blog.fragments.ProfileFragment;

public class UserDashboard extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    public static String PHONENO, NAME;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_user_dashboard);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();

        //get data from intent
        PHONENO = getIntent().getStringExtra("phoneNo");
        NAME = getIntent().getStringExtra("Name");
//        Toast.makeText(this, PHONENO, Toast.LENGTH_SHORT).show();

        bottomMenu();
    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;

                switch (i) {
                    case R.id.bottom_nav_dashboard:
                        fragment = new DashboardFragment();
                        break;

                    case R.id.bottom_nav_manage:
                        fragment = new ManageFragment();
                        break;

                    case R.id.bottom_nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });


    }
}
