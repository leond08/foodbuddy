package ph.foodbuddy.falcon.com.foodbuddy;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ph.foodbuddy.falcon.com.foodbuddy.fragment.HomeFragment;
import ph.foodbuddy.falcon.com.foodbuddy.fragment.MenuFragment;
import ph.foodbuddy.falcon.com.foodbuddy.fragment.NotificationFragment;
import ph.foodbuddy.falcon.com.foodbuddy.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private BottomNavigationView bottomNavigationView;

    private SearchView searchView;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Log.d("tag", authProvider);
            currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                // redirect to log in page
                redirectToLogin();
            }
    }

    /**
     * Initialize
     *
     */
    private void initialize() {

        mAuth = FirebaseAuth.getInstance();

        searchView = findViewById(R.id.search_main);

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        menuFragment = new MenuFragment();

        replaceFragment(homeFragment);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_home:

                        replaceFragment(homeFragment);
                        return true;

                    case R.id.nav_notification:

                        replaceFragment(notificationFragment);
                        return true;

                    case R.id.nav_menu:

                        replaceFragment(menuFragment);
                        return true;

                    case R.id.nav_buddies:

                        return true;

                    default:

                        return false;
                }
            }
        });
    }

    /**
     * Redirect to Log in page
     *
     */
    private void redirectToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Replace fragment in main
     *
     * @param fragment
     */
    protected void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }
}
