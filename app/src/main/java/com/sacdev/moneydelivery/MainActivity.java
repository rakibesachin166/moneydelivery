package com.sacdev.moneydelivery;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomnav;
    Fragment currentfragment = new HomeFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomnav = findViewById(R.id.bottomnavigationbar_id);
        getSupportFragmentManager().beginTransaction().replace(R.id.framentholder_id, new HomeFragment()).commit();

        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navProfile:
                        currentfragment = new SettingFragment();
                        fragmentchanger(currentfragment);
                        break;
                    case R.id.orderitem:
                        currentfragment = new OrdersFragment();
                        fragmentchanger(currentfragment);
                        break;
                    default:
                        currentfragment = new HomeFragment();
                        fragmentchanger(currentfragment);
                        break;
                }
                return true;

            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                      setbottomview();    }
                });
        new Thread(new Runnable() {
            public void run(){
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    starterclass.locationpermission = true;
                }else{

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},1);
                }
            }
        }).start();
    }

    public void fragmentchanger(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enterlefttoright,R.anim.outlefttoright
                        ,R.anim.enterrighttoleft,R.anim.outrighttoleft).replace(R.id.framentholder_id, fragment)
                .addToBackStack("My")
                .commit();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            starterclass.locationpermission = true;
        } else {
            starterclass.locationpermission = false;
        }
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public  void setbottomview()
    {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.framentholder_id);
        if(f instanceof HomeFragment){
            MenuItem item = bottomnav.getMenu().findItem(R.id.navMain);
            item.setChecked(true);
        }else if (f instanceof  OrdersFragment){
            MenuItem item = bottomnav.getMenu().findItem(R.id.orderitem);
            item.setChecked(true);
        }else if (f instanceof SettingFragment){
            MenuItem item = bottomnav.getMenu().findItem(R.id.navProfile);
            item.setChecked(true);
        }

    }


}