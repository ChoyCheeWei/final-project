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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageView;

public class Login extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference seller;

    GifImageView GifImageView;
    private Button login,Back;
    private TextView textView;
    private EditText name,pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        setTitle("Seller Login Page");

        textView = (TextView) findViewById(R.id.textView);
        textView.setSelected(true);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.seller);

        GifImageView = (GifImageView) findViewById(R.id.gifimageview);

        GifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });


        login = (Button) findViewById(R.id.Login);
        Back = (Button) findViewById(R.id.back);
        name = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.tvpass);;

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,MainActivity.class);
                startActivity(intent);

            }
        });

        database = FirebaseDatabase.getInstance();
        seller = database.getReference("Seller");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(name.getText().toString(),
                        pass.getText().toString());
            }
        });



    }

    private void Login(final String username, final String password){
        final String Username = name.getText().toString();
        final String Password = pass.getText().toString();

        seller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(username).exists()){
                    if (TextUtils.isEmpty(Username)){
                        name.setError("Username is required");
                    }
                    if (TextUtils.isEmpty(Password)){
                        pass.setError("Password is required");
                    }
                    if (!username.isEmpty()){
                        Seller login = dataSnapshot.child(username).getValue(Seller.class);

                        if (login.getPass().equals(password)){
                            Toast.makeText(Login.this,"Login Success",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,SellerHomePage.class);
                            intent.putExtra("Storename", name.getText().toString());
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(Login.this,"Invalid username or password ",Toast.LENGTH_SHORT).show();

                    }
                    else
                        Toast.makeText(Login.this,"User is not registered",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(Login.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(Login.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.admin:
                            Intent intent3 = new Intent(Login.this,Admin_Login.class);
                            startActivity(intent3);
                            break;
                    }



                    return true;
                }

            };
}
