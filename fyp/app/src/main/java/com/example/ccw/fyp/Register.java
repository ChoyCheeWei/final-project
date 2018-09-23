package com.example.ccw.fyp;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Register extends AppCompatActivity {

    private StorageReference storageReference;
    private DatabaseReference databaseSeller;
    Button register,back;
    EditText Email,Username,Password,Phone;
    ImageView add;


    private ProgressBar progressBar;

    private Uri imageUri;
    private StorageTask mUploadTask;

    private final static int  PICK_IMAGE_REQUEST =1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.seller);

        register = (Button) findViewById(R.id.Register);
        back = (Button) findViewById(R.id.Back);

        Email = (EditText) findViewById(R.id.email);
        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.pass);
        Phone = (EditText) findViewById(R.id.phone);

        add = (ImageView)findViewById(R.id.imageView);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        storageReference = FirebaseStorage.getInstance().getReference("Seller");
        databaseSeller = FirebaseDatabase.getInstance().getReference("Seller");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSeller(v);
            }
        });
    }
    public void selectImage(){
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){


            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(add);

        }
    }


    public void AddSeller(View v) {
        final String email = Email.getText().toString();
        final String username = Username.getText().toString();
        final String pass = Password.getText().toString();
        final String phone = Phone.getText().toString();

        if ((TextUtils.isEmpty(email))){
            Email.setError("Email address is required");

        }
        if ((TextUtils.isEmpty(username))){
            Username.setError("Username is required");

        }
        if ((TextUtils.isEmpty(pass))){
            Password.setError("Password is required");

        }

        if ((TextUtils.isEmpty(phone))){
            Phone.setError("Phone number is required");

        }



        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to register as a seller?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Seller").child(Username.getText().toString().trim());

                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        if (imageUri != null) {
                                            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                                    + "." + getFileExtension(imageUri));

                                            mUploadTask = fileReference.putFile(imageUri)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progressBar.setProgress(0);
                                                                }
                                                            }, 500);

                                                            Toast.makeText(Register.this, "Register successful", Toast.LENGTH_LONG).show();
                                                            Seller seller = new Seller(Email.getText().toString().trim(),
                                                                    Username.getText().toString().trim(), Password.getText().toString().trim(),
                                                                    Phone.getText().toString().trim(),
                                                                    taskSnapshot.getDownloadUrl().toString());
                                                            databaseReference.setValue(seller);

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                            progressBar.setProgress((int) progress);
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(Register.this, "No image selected", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplication(), "Seller's name already exist", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            databaseReference.addListenerForSingleValueEvent(eventListener);


                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {


                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
                    .show();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(Register.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(Register.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.admin:
                            Intent intent3 = new Intent(Register.this,Admin_Login.class);
                            startActivity(intent3);
                            break;
                    }



                    return true;
                }

            };

}