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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class AddTripActivity extends AppCompatActivity implements MyCitiesAdapter.OnItemClickListener{

    TextView tv_tripName;
    TextView tv_searchCity;
    Button btn_search;
    Button btn_addTrip;

    RecyclerView citiesRecyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    ArrayList<Cities> mainCityList;
    int currPosition;
    int flag = 0;

    String citiesApiCall;
    String geoApiCall;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        setTitle("Add Trip");

        tv_tripName = findViewById(R.id.tv_tripName);
        tv_searchCity = findViewById(R.id.tv_searchCity);
        btn_search = findViewById(R.id.btn_search);
        citiesRecyclerView = findViewById(R.id.citiesRecyclerView);
        btn_addTrip = findViewById(R.id.btn_addTrip);

        citiesRecyclerView.setHasFixedSize(true);
        rv_layoutManager = new LinearLayoutManager(AddTripActivity.this);
        citiesRecyclerView.setLayoutManager(rv_layoutManager);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    if (tv_searchCity.getText().toString().length() > 0) {
                        citiesApiCall = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=**&types=(cities)&input=$$";
                        citiesApiCall = citiesApiCall.replace("**", getResources().getString(R.string.API_Key));
                        citiesApiCall = citiesApiCall.replace("$$", tv_searchCity.getText().toString());
                        new GetCities().execute();
                    } else {
                        tv_searchCity.setError("Please input city");
                    }
                }else{
                    Toast.makeText(AddTripActivity.this, "No network connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    if (tv_searchCity.getText().toString().length() > 0) {
                        if (tv_tripName.getText().toString().length() > 0) {
                            if (flag == 1) {
                                geoApiCall = "https://maps.googleapis.com/maps/api/place/details/json?key=**&placeid=$$";
                                geoApiCall = geoApiCall.replace("**", getResources().getString(R.string.API_Key));
                                geoApiCall = geoApiCall.replace("$$", mainCityList.get(currPosition).place_id);
                                new GetGeoLocation().execute();
                            } else {
                                Toast.makeText(AddTripActivity.this, "Please select one city", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            tv_tripName.setError("Please input trip name");
                        }

                    } else {
                        tv_searchCity.setError("Please input city");
                    }
                }else{
                    Toast.makeText(AddTripActivity.this, "No network connection.", Toast.LENGTH_SHORT).show();
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
        }
        return true;
    }

    @Override
    public void onItemClick(int position) {
        tv_searchCity.setText(mainCityList.get(position).getDescription());
        currPosition = position;
        flag = 1;
    }

    public class GetGeoLocation extends AsyncTask<Void, Void, result> {

        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected result doInBackground(Void... voids) {

            Request request = new Request.Builder()
                    .url(geoApiCall)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    return null;
                }else {
                    String responseString = response.body().string();
                    JSONObject obj = new JSONObject(responseString);
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<result>(){}.getType();
                    result result = gson.fromJson(obj.getString("result"), userListType);
                    Log.d("demo", result.toString());
                    return result;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(result result) {
            super.onPostExecute(result);
            Map<String, Object> user = new HashMap<>();
            user.put("trip", tv_tripName.getText().toString());
            user.put("city", tv_searchCity.getText().toString());
            user.put("lat", result.getGeometry().getLocation().getLat());
            user.put("lng", result.getGeometry().getLocation().getLng());
            db.collection("Trip 1")
                    .add(user)
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
            Intent intent = new Intent(AddTripActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public class GetCities extends AsyncTask<Void, Void, ArrayList<Cities>> {

        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected ArrayList<Cities> doInBackground(Void... voids) {

            Request request = new Request.Builder()
                    .url(citiesApiCall)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    Toast.makeText(AddTripActivity.this, "Connection Unsuccessful", Toast.LENGTH_SHORT).show();
                    return null;
                }else {
                    String responseString = response.body().string();
                    JSONObject obj = new JSONObject(responseString);
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<ArrayList<Cities>>(){}.getType();
                    ArrayList<Cities> citiesList = gson.fromJson(obj.getString("predictions"), userListType);
                    Log.d("demo", citiesList.toString());
                    mainCityList = new ArrayList<>();
                    return citiesList;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Cities> cities) {
            super.onPostExecute(cities);
            if(null!=cities && cities.size()>0) {
                mainCityList = cities;
                rv_adapter = new MyCitiesAdapter(cities, AddTripActivity.this);
                citiesRecyclerView.setAdapter(rv_adapter);
            }else{
                Toast.makeText(AddTripActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class result{
        geometry geometry = new geometry();

        public AddTripActivity.geometry getGeometry() {
            return geometry;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class geometry{
        location location = new location();

        public AddTripActivity.location getLocation() {
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
