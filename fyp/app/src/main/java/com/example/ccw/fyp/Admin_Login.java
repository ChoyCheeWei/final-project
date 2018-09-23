package com.example.ccw.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Admin_Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    TextView email,pass;
    Button login,Back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        setTitle("Admin Login Page");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.admin);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
                // ...
            }
        };


        Back = (Button) findViewById(R.id.back);
        email = (TextView) findViewById(R.id.tvmail);
        pass = (TextView) findViewById(R.id.tvpassword);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Login.this,MainActivity.class);
                startActivity(intent);

            }
        });

    }

    public void loginUser(View v){
        String Email = email.getText().toString();
        String password = pass.getText().toString();

        if (TextUtils.isEmpty(Email)){
            email.setError("Email is required");
        }

        if (TextUtils.isEmpty(password)){
            pass.setError("password is required");
        }
        if ((!TextUtils.isEmpty(Email)) && (!TextUtils.isEmpty(password))) {
            mAuth.signInWithEmailAndPassword(Email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Admin_Login.this, "Login Failed",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Admin_Login.this, "Login Success",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Admin_Login.this, Admin.class);
                                startActivity(intent);
                            }

                        }
                    });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(Admin_Login.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(Admin_Login.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.seller:
                            Intent intent2 = new Intent(Admin_Login.this,Login.class);
                            startActivity(intent2);
                            break;

                    }



                    return true;
                }

            };
}
