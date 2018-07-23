package ph.foodbuddy.falcon.com.foodbuddy.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import ph.foodbuddy.falcon.com.foodbuddy.MainActivity;
import ph.foodbuddy.falcon.com.foodbuddy.R;
import ph.foodbuddy.falcon.com.foodbuddy.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText registerEmail, registerPassword, registerConfirmPassword;
    private Button registerButton;
    private TextView registerLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initialize();
    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();

        // Edit Text
        registerEmail = findViewById(R.id.registerEmailField);
        registerPassword = findViewById(R.id.registerPasswordField);
        registerConfirmPassword = findViewById(R.id.registerConfirmPasswordField);

        // TextView
        registerLogin = findViewById(R.id.registerLoginLabel);

        // Progress bar
        progressBar = findViewById(R.id.registerProgressBar);

        // Buttons
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(this);
        registerLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.registerButton:
                String email, password, confirmPassword;
                email = registerEmail.getText().toString();
                password = registerPassword.getText().toString();
                confirmPassword = registerConfirmPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(confirmPassword)) {

                    if (password.equals(confirmPassword)) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        progressBar.setVisibility(View.INVISIBLE);
                                        redirectToMain();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.w("tag", task.getException().toString());
                                        Toast.makeText(RegistrationActivity.this, "Registration failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                    else {

                        Toast.makeText(RegistrationActivity.this, "Password not match",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    Toast.makeText(RegistrationActivity.this, "Please provide the details...",
                            Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.registerLoginLabel:

                Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();

                break;
        }
    }

    private void redirectToMain() {

        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
