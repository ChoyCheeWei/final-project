package com.example.ccw.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class SellerHomePage extends AppCompatActivity {

    GridLayout mainGrid;
    private TextView textView, visibletv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_page);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.seller);

        mainGrid = (GridLayout) findViewById(R.id.mainGrid);
        textView = (TextView) findViewById(R.id.textGrid);
        visibletv = (TextView) findViewById(R.id.visibletv);

        Intent intent = getIntent();
        String StoreName = intent.getStringExtra("Storename");
        textView.setText("Hello "+StoreName);

        String newtext = StoreName;
        visibletv.setText(newtext);
        //Set Event
        setSingleEvent(mainGrid);

    }


    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (finalI==0) {
                        Intent intent = new Intent(SellerHomePage.this, Store_Info.class);
                        intent.putExtra("Storename", visibletv.getText().toString());
                        startActivity(intent);
                    }
                    else if (finalI==1){
                        Intent intent = new Intent(SellerHomePage.this, Recycleview.class);
                        intent.putExtra("Storename", visibletv.getText().toString());
                        startActivity(intent);
                    }
                    else if (finalI==2){
                        Intent intent = new Intent(SellerHomePage.this, ViewOrders.class);
                        intent.putExtra("Storename", visibletv.getText().toString());
                        startActivity(intent);
                    }
                    else if (finalI==3){
                        Intent intent = new Intent(SellerHomePage.this, Login.class);
                        startActivity(intent);
                    }

                }
            });
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.home:
                            Intent intent = new Intent(SellerHomePage.this,MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user:
                            Intent intent1 = new Intent(SellerHomePage.this,User.class);
                            startActivity(intent1);
                            break;
                        case R.id.admin:
                            Intent intent3 = new Intent(SellerHomePage.this,Admin_Login.class);
                            startActivity(intent3);
                            break;
                    }



                    return true;
                }

            };
}
