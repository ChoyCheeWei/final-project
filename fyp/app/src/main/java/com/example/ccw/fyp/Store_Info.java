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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Store_Info extends AppCompatActivity {


    private StorageReference storageReference;
    private DatabaseReference FoodInfo;
    List<FoodInfo> foods;

    private Button add, menu;
    private EditText tvname, tvprice, tvdesc;
    private ImageView image;
    private ProgressBar progressBar;
    private TextView storename;

    private Uri imageUri;

    private final static int PICK_IMAGE_REQUEST = 1;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_info);
        setTitle("Add Menu");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.seller);

        add = (Button) findViewById(R.id.Addfood);
        menu = (Button) findViewById(R.id.foodmenu);

        tvname = (EditText) findViewById(R.id.editText1);
        tvprice = (EditText) findViewById(R.id.editText2);
        tvdesc = (EditText) findViewById(R.id.editText3);
        storename = (TextView) findViewById(R.id.username);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        image = (ImageView) findViewById(R.id.imageView);


        storageReference = FirebaseStorage.getInstance().getReference("FoodInfo");

        FoodInfo = FirebaseDatabase.getInstance().getReference("FoodInfo");
        foods = new ArrayList<>();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(Store_Info.this, "Upload in progress....", Toast.LENGTH_SHORT).show();
                } else {
                    AddFood(v);
                }


            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Intent intent = getIntent();
        String StoreName = intent.getStringExtra("Storename");
        storename.setText(StoreName);


    }


    public void selectImage() {
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

    private void openImagesActivity() {
        Intent intent = new Intent(this, Recycleview.class);
        intent.putExtra("Storename", storename.getText().toString());
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(image);

        }
    }


    public void AddFood(View v) {
        final String name = tvname.getText().toString();
        final String price = tvprice.getText().toString();
        final String desc = tvdesc.getText().toString();


        if ((TextUtils.isEmpty(name))) {
            tvname.setError("Name is required");

        }
        if ((TextUtils.isEmpty(price))) {
            tvprice.setError("Price is required");

        }
        if ((TextUtils.isEmpty(desc))) {
            tvdesc.setError("Food description is required");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to add a new food to your menu?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodInfo").child(name);

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

                                                            FoodInfo foodInfo = new FoodInfo(tvname.getText().toString().trim(),
                                                                 "RM " + tvprice.getText().toString().trim(), tvdesc.getText().toString().trim(),
                                                                    taskSnapshot.getDownloadUrl().toString(), storename.getText().toString());
                                                            databaseReference.setValue(foodInfo);
                                                            Toast.makeText(Store_Info.this, "New Food Add Successful", Toast.LENGTH_LONG).show();


                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Store_Info.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(Store_Info.this, "No image selected", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplication(), "Food name already exist", Toast.LENGTH_LONG).show();
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
                            Intent intent = new Intent(Store_Info.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(Store_Info.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.admin:
                            Intent intent3 = new Intent(Store_Info.this,Admin_Login.class);
                            startActivity(intent3);
                            break;
                    }



                    return true;
                }

            };
}



