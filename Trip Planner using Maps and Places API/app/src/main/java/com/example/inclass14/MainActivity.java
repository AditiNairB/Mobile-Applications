/**
 * Assignment: InClass14
 * Group No: 9
 * Members: Aditi Balachandran and Luckose Manuel
 */

package com.example.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MySubAdapter.OnItemLongClickListener{

    ImageView iv_addTrip;

    RecyclerView mainRecycler;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;
    ArrayList<TripDetails> tripDetailsList = new ArrayList<>();
    ArrayList<Places> placesArrayList = new ArrayList<>();
    TripDetails details;
    Places places;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Trips");

        iv_addTrip = findViewById(R.id.iv_addTrip);
        mainRecycler = findViewById(R.id.mainRecycler);

        if(isConnected()) {
            Map<String, Object> user = new HashMap<>();

            insertIntoRecycler();
        }else{
            Toast.makeText(MainActivity.this, "No network connection.", Toast.LENGTH_SHORT).show();
        }

        iv_addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTripActivity.class);
                startActivity(intent);
            }
        });

    }

    private void insertIntoRecycler() {
        db.collection("Trip 1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            tripDetailsList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAGTrips", document.getId() + " => " + document.getData());
                                details = document.toObject(TripDetails.class);
                                details.setDocumentID(document.getId());
                                tripDetailsList.add(details);
                            }
                            for(int i =0; i<tripDetailsList.size(); i++) {
                                final int finalI = i;
                                db.collection("Trip 1")
                                        .document(tripDetailsList.get(finalI).documentID)
                                        .collection("Places")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    placesArrayList = new ArrayList<>();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d("TAGPlaces", document.getId() + " => " + document.getData());
                                                        places = document.toObject(Places.class);
                                                        places.setDocumentID(document.getId());
                                                        placesArrayList.add(places);
                                                    }
                                                    if(null!=placesArrayList && placesArrayList.size()>0)
                                                        tripDetailsList.get(finalI).setPlacesArrayList(placesArrayList);
                                                    mainRecycler.setHasFixedSize(true);
                                                    rv_layoutManager = new LinearLayoutManager(MainActivity.this);
                                                    mainRecycler.setLayoutManager(rv_layoutManager);
                                                    Log.d("TAGNEW", tripDetailsList.toString());
                                                    rv_adapter = new MyAdapter(tripDetailsList, MainActivity.this);
                                                    mainRecycler.setAdapter(rv_adapter);
                                                } else {
                                                    Log.w("TAG", "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }        return true;
    }

    @Override
    public boolean onItemLongClick(String placesID, String tripID) {
        Log.w("TAGDelete", "Places ID " + placesID + " Trip ID: " + tripID);
        db.collection("Trip 1")
                .document(tripID)
                .collection("Places")
                .document(placesID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            insertIntoRecycler();
                    }
                });
        return true;
    }
}
