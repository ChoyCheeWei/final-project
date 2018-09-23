package com.example.ccw.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity implements MenuAdapter.OnItemClickListener,SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    private ProgressBar progressBar;

    private FirebaseStorage firebaseStorage;

    private ValueEventListener listener;
    private DatabaseReference databaseReference;
    private List<FoodInfo> mfoodinfo;

    SearchView searchView;
    ArrayList<FoodInfo> newlist;

    FloatingActionButton fab;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermenu);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.user);



        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progress_circle);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        mfoodinfo = new ArrayList<>();

        menuAdapter = new MenuAdapter(Menu.this, mfoodinfo);
        recyclerView.setAdapter(menuAdapter);


        menuAdapter.setOnItemClickListener(Menu.this);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodInfo");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Menu.this,Orders.class);
                startActivity(intent);

            }
        });


        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mfoodinfo.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FoodInfo foodInfo = postSnapshot.getValue(FoodInfo.class);
                    foodInfo.setFoodname(postSnapshot.getKey());
                    mfoodinfo.add(foodInfo);
                }

                menuAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Menu.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }



    @Override
    public void onAddClick(int position)
    {
        final FoodInfo selectedItem = mfoodinfo.get(position);
        final String selectedKey = selectedItem.getFoodname();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait......");
        progressDialog.show();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.child(selectedKey).setValue(selectedItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(Menu.this, "Order Successful!!", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(Menu.this, "Not ordered" + task.getException(), Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(listener);
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

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

        menuAdapter.setFilter(newlist);


        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(Menu.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.seller:
                            Intent intent1 = new Intent(Menu.this,Login.class);
                            startActivity(intent1);
                            break;
                        case R.id.admin:
                            Intent intent3 = new Intent(Menu.this,Admin_Login.class);
                            startActivity(intent3);
                            break;
                    }



                    return true;
                }

            };
}