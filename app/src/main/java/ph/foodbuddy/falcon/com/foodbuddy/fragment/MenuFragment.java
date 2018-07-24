package ph.foodbuddy.falcon.com.foodbuddy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import ph.foodbuddy.falcon.com.foodbuddy.R;
import ph.foodbuddy.falcon.com.foodbuddy.login.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private Button logoutButton;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(container.getContext(), gso);

        logoutButton = view.findViewById(R.id.mainLogout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                mAuth.signOut();
                redirectToLogin(view);
            }
        });

        return view;
    }

    private void redirectToLogin(View view) {

        Intent loginIntent = new Intent(view.getContext(), LoginActivity.class);
        startActivity(loginIntent);

    }

}
