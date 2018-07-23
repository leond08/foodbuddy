package ph.foodbuddy.falcon.com.foodbuddy.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import ph.foodbuddy.falcon.com.foodbuddy.MainActivity;
import ph.foodbuddy.falcon.com.foodbuddy.R;
import ph.foodbuddy.falcon.com.foodbuddy.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";
    private String authProvider;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private GoogleSignInClient mGoogleSignInClient;

    private EditText loginEmail, loginPassword;
    private Button loginButton, loginGoogleButton, loginFacebookButton;
    private ProgressBar loginProgressBar;
    private TextView loginSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuthStateListener.onAuthStateChanged(mAuth);

    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    redirectToMain();
                }

            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        loginEmail = findViewById(R.id.loginEmailField);
        loginPassword = findViewById(R.id.loginPasswordField);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        loginGoogleButton = findViewById(R.id.loginGoogleButton);
        loginGoogleButton.setOnClickListener(this);
        loginFacebookButton = findViewById(R.id.loginFacebookButton);
        loginFacebookButton.setOnClickListener(this);

        loginProgressBar = findViewById(R.id.loginProgressBar);

        loginSignUp = findViewById(R.id.loginSignUpLabel);
        loginSignUp.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.loginButton:

                final String email = loginEmail.getText().toString();
                final String password = loginPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    loginProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    loginProgressBar.setVisibility(View.INVISIBLE);
                                    currentUser = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Login Successfully",
                                            Toast.LENGTH_SHORT).show();
                                    redirectToMain();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    loginProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.w("error", task.getException().toString());
                                    Log.w("error", email + " " + password);
                                }
                            }
                        });
                }
                else {
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Please provide email and password",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.loginFacebookButton:

                break;

            case R.id.loginGoogleButton:
                loginProgressBar.setVisibility(View.VISIBLE);
                googleSignIn();

                break;

            case R.id.loginSignUpLabel:

                Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registrationIntent);
                finish();
                break;

        }

    }

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // Google Sign In was successful, authenticate with Firebase
            Log.d("tag", "request Code" + requestCode);
            handleSignInResult(task);
        }
        else {
            Log.d("tag", "Error request Code");
        }
    }

    /**
     * Redirect to main page if user is signed in
     *
     */
    private void redirectToMain() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            loginProgressBar.setVisibility(View.INVISIBLE);
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getMessage().toString());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        loginProgressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "signInWithCredential:success");
                        redirectToMain();
                    } else {
                        loginProgressBar.setVisibility(View.INVISIBLE);
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, task.getException().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
