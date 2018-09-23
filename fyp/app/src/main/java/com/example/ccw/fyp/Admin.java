package com.example.ccw.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity implements SellerAdapter.OnItemClickListener,SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private SellerAdapter sellerAdapter;

    private FirebaseStorage firebaseStorage;

    private ValueEventListener listener;
    private DatabaseReference databaseReference;
    private List<Seller> mseller;

    android.support.v7.widget.SearchView searchView;

    ArrayList<Seller> newlist;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        setTitle("Seller Info");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.admin);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mseller = new ArrayList<>();

        sellerAdapter = new SellerAdapter(Admin.this, mseller);
        recyclerView.setAdapter(sellerAdapter);

        sellerAdapter.setOnItemClickListener(Admin.this);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Seller");

        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mseller.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Seller seller = postSnapshot.getValue(Seller.class);
                    seller.setEmail(postSnapshot.getKey());
                    mseller.add(seller);


                }

                sellerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Admin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onDeleteClick(final int position)
    {
        Seller selectedItem;

        if(!searchView.getQuery().toString().isEmpty())
        {
            selectedItem = newlist.get(position);
        }
        else
        {
            selectedItem = mseller.get(position);
        }

        final String selectedKey = selectedItem.getUsername();

        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(Admin.this, "Seller deleted", Toast.LENGTH_SHORT).show();
                mseller.remove(position);
                sellerAdapter.notifyDataSetChanged();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
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
        for (Seller seller : mseller){

            String name = seller.getUsername().toLowerCase();
            if (name.contains(newText))
                newlist.add(seller);
        }

        sellerAdapter.setFilter(newlist);

        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(Admin.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(Admin.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.seller:
                            Intent intent2 = new Intent(Admin.this,Login.class);
                            startActivity(intent2);
                            break;

                    }



                    return true;
                }

            };
}

