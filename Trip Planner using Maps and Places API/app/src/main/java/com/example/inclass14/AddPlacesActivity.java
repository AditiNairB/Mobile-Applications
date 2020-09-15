package com.example.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddPlacesActivity extends AppCompatActivity implements MyPlacesAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    String citiesApiCall;
    String documentID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<result> resultList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        setTitle("Add Places");

        double lat = getIntent().getDoubleExtra("Lat", 0);
        double lng = getIntent().getDoubleExtra("Lng", 0);
        documentID = getIntent().getStringExtra("DocumentID");

        citiesApiCall = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=**&location=$$,##&radius=1000";
        citiesApiCall = citiesApiCall.replace("**", getResources().getString(R.string.API_Key));
        citiesApiCall = citiesApiCall.replace("$$", Double.toString(lat));
        citiesApiCall = citiesApiCall.replace("##", Double.toString(lng));

        if(isConnected()) {
            new GetPlaces().execute(lat, lng);
        }else{
            Toast.makeText(AddPlacesActivity.this, "No network connection.", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onItemClick(int position) {

        Map<String, Object> details = new HashMap<>();
        details.put("icon", resultList.get(position).icon);
        details.put("name", resultList.get(position).name);
        details.put("lat", resultList.get(position).getGeometry().getLocation().getLat());
        details.put("lng", resultList.get(position).getGeometry().getLocation().getLng());
        db.collection("Trip 1")
                .document(documentID)
                .collection("Places")
                .add(details)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
        Intent intent = new Intent(AddPlacesActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public class GetPlaces extends AsyncTask<Double, Void, ArrayList<result>>{

        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected ArrayList<result> doInBackground(Double... doubles) {

            Request request = new Request.Builder()
                    .url(citiesApiCall)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    Toast.makeText(AddPlacesActivity.this, "Connection Unsuccessful", Toast.LENGTH_SHORT).show();
                    return null;
                }else {
                    resultList = new ArrayList<>();
                    String responseString = response.body().string();
                    JSONObject obj = new JSONObject(responseString);
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<ArrayList<result>>(){}.getType();
                    resultList = gson.fromJson(obj.getString("results"), userListType);
                    Log.d("demo", resultList.toString());
                    return resultList;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<result> result) {
            super.onPostExecute(result);

            if(result != null && result.size() > 0) {
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                rv_layoutManager = new LinearLayoutManager(AddPlacesActivity.this);
                recyclerView.setLayoutManager(rv_layoutManager);
                rv_adapter = new MyPlacesAdapter(result, AddPlacesActivity.this);
                recyclerView.setAdapter(rv_adapter);
            }else{
                Log.d("demo", "ERROR!");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    class result{
        geometry geometry = new geometry();
        String name;
        String icon;

        public geometry getGeometry() {
            return geometry;
        }
    }

    class geometry{
        location location = new location();

        public location getLocation() {
            return location;
        }
    }

    class location{
        double lat;
        double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}
