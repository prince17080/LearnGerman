package com.example.finalapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    static final int GOOGLE_SIGNIN = 7;
    FirebaseAuth mAuth;
    SignInButton log_in_btn;
    Button log_out_btn, start_btn;
    TextView text;
    ImageView image;
    GoogleSignInClient mgoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log_in_btn = (SignInButton) findViewById(R.id.sign_in_button);
        log_out_btn = (Button) findViewById(R.id.sign_out_button);
        start_btn = (Button) findViewById(R.id.start_button);
        text = (TextView) findViewById(R.id.vm);
        image = (ImageView) findViewById(R.id.homeImg);

        mAuth = FirebaseAuth.getInstance();
        start_btn.setVisibility(View.INVISIBLE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        log_in_btn.setOnClickListener(v -> SignInGoogle());
        log_out_btn.setOnClickListener(v -> Logout());
        start_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
        });

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }
    }

    void SignInGoogle() {
        Intent signIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGNIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("TAG", "signin success");

                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w("TAG", "signin failure", task.getException());

                    Toast.makeText(this, "SignIn Failed!", Toast.LENGTH_SHORT);
                    updateUI(null);
                }

            });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());

            text.setText("Welcome\n" + email);

            Picasso.get().load(photo).into(image);
            log_in_btn.setVisibility(View.INVISIBLE);
            log_out_btn.setVisibility(View.VISIBLE);
            start_btn.setVisibility(View.VISIBLE);

        } else {
            text.setText("Fulfill your dreams by signing in \\n:) ");
            Picasso.get().load(R.drawable.googleg_standard_color_18).into(image);
            log_out_btn.setVisibility(View.INVISIBLE);
            log_in_btn.setVisibility(View.VISIBLE);
            start_btn.setVisibility(View.INVISIBLE);
        }
    }

    void Logout() {
        FirebaseAuth.getInstance().signOut();
        mgoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }
}
