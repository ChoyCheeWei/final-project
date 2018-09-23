
package com.example.ccw.fyp;


        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.ContentResolver;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.support.design.widget.BottomNavigationView;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.webkit.MimeTypeMap;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.support.v7.widget.SearchView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class Recycleview extends AppCompatActivity implements FoodAdapter.OnItemClickListener,SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;

    private ProgressBar progressBar;

    private FirebaseStorage firebaseStorage;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<FoodInfo> mfoodinfo;

    private ImageView imageView;

    private EditText foodname,foodprice,fooddesc,Storename;
    SearchView searchView;
    ArrayList<FoodInfo> newlist;

    private final static int  PICK_IMAGE_REQUEST =1;
    private Uri imageUri;

    FoodInfo foodInfo;

    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);

        setTitle("Menu");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.seller);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progress_circle);

        mfoodinfo = new ArrayList<>();

        foodAdapter = new FoodAdapter(Recycleview.this, mfoodinfo);
        recyclerView.setAdapter(foodAdapter);

        Intent intent = getIntent();
        String StoreName = intent.getStringExtra("Storename");

        foodAdapter.setOnItemClickListener(Recycleview.this);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodInfo");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating in Progress");
        progressDialog.setCanceledOnTouchOutside(false);


        Query query = databaseReference.orderByChild("storename").equalTo(StoreName);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

                foodAdapter.addFood(dataSnapshot.getValue(FoodInfo.class));
                foodAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void UpdateFood(int position)
    {
        showUpdateDialog(position);
    }

    private void showUpdateDialog(int pos) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        imageView = (ImageView) dialogView.findViewById(R.id.imageView);
        Picasso.with(this).load(mfoodinfo.get(pos).imageUrl).placeholder(R.mipmap.image).resize(150,150).into(imageView);

        foodname = (EditText) dialogView.findViewById(R.id.updfood);
        foodname.setText(mfoodinfo.get(pos).foodname);

        foodprice = (EditText) dialogView.findViewById(R.id.updprice);
        foodprice.setText(mfoodinfo.get(pos).foodprice);

        fooddesc = (EditText) dialogView.findViewById(R.id.upddesc);
        fooddesc.setText(mfoodinfo.get(pos).fooddesc);

        Storename = (EditText) dialogView.findViewById(R.id.storename);
        Storename.setText(mfoodinfo.get(pos).storename);

        final Button update = (Button) dialogView.findViewById(R.id.btnUpdate);
        final Button cancel = (Button) dialogView.findViewById(R.id.btnCancel);

        dialogBuilder.setTitle("Update");
        alertDialog = dialogBuilder.create();
        alertDialog.show();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateFood(v);
                foodAdapter.notifyDataSetChanged();

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    public void selectImage(){
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(imageView);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void UpdateFood(View v) {
        final String name = foodname.getText().toString();
        foodprice.getText().toString();
        fooddesc.getText().toString();
        Storename.getText().toString();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog;
        builder.setMessage("Do you want to update this food info?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(final DialogInterface dialog, int which)
            {

                progressDialog.show();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodInfo").child(name);


                final Map m = new HashMap();
                m.put("foodprice", foodprice.getText().toString().trim());
                m.put("fooddesc",  fooddesc.getText().toString().trim());

                StorageReference sr =  FirebaseStorage.getInstance().getReference();

                StorageReference reference  = sr.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));

                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        m.put("imageUrl", taskSnapshot.getDownloadUrl().toString());
                        databaseReference.updateChildren(m).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    alertDialog.dismiss();
                                    Toast.makeText(Recycleview.this, "Food Updated Successfully", Toast.LENGTH_LONG).show();
                                    foodAdapter.notifyDataSetChanged();
                                }

                                else
                                {
                                    Toast.makeText(Recycleview.this, "Failed to Update" + task.getException(), Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        alertDialog.dismiss();
                        Toast.makeText(Recycleview.this, "Failed to upload Image" + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("Error Image " , e.toString());
                    }
                });



            }
        });
        builder.setNegativeButton("No", null);
        dialog = builder.create();
        dialog.show();

    }


    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "position: " + position, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onDeleteClick(final int position)
    {
        FoodInfo selectedItem;

        if(!searchView.getQuery().toString().isEmpty())
        {
            selectedItem = newlist.get(position);
        }
        else
        {
            selectedItem = mfoodinfo.get(position);
        }

        final String selectedKey = selectedItem.getFoodname();

        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(Recycleview.this, "Item deleted", Toast.LENGTH_SHORT).show();
                mfoodinfo.remove(position);
                foodAdapter.notifyDataSetChanged();

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        newlist = new ArrayList<>();
        for (FoodInfo foodInfo : mfoodinfo){

            String name = foodInfo.getFoodname().toLowerCase();
            if (name.contains(newText))
                newlist.add(foodInfo);
        }

        foodAdapter.setFilter(newlist);


        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(Recycleview.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(Recycleview.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.admin:
                            Intent intent3 = new Intent(Recycleview.this,Admin_Login.class);
                            startActivity(intent3);
                            break;
                    }



                    return true;
                }

            };
}