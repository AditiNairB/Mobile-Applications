package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewContacts extends AppCompatActivity implements MyAdapter.OnItemLongClickListener{

    private static final String TAG = "demo";
    public static final String CONTACT_INFO = "contactinfo";
    public static final int REQ_CODE_ADDCONTACT = 100;
    private FirebaseFirestore db;
    String userid = null;
    Button button_createnewcontact;
    ImageButton button_logout;
    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;
    ArrayList<ContactInfo> contactInfoArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        setTitle("Contacts");

        db = FirebaseFirestore.getInstance();

        button_createnewcontact = findViewById(R.id.button_createnewcontact);
        button_logout = findViewById(R.id.button_logout);
        recyclerView = findViewById(R.id.recyclerView);


        if(getIntent()!=null && getIntent().getExtras() != null) {
            userid = getIntent().getExtras().getString(MainActivity.UID);
        }
        contactInfoArrayList = new ArrayList<>();
        db.collection("users")
                .document(userid).collection("contacts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ContactInfo contactInfo = new ContactInfo(document.getString("imageurl"),
                            document.getString("name"), document.getString("email"),
                            document.getString("number"));
                    contactInfo.setDocumentID(document.getId());
                    contactInfoArrayList.add(contactInfo);
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                recyclerView.setHasFixedSize(true);

                rv_layoutManager = new LinearLayoutManager(ViewContacts.this);

                recyclerView.setLayoutManager(rv_layoutManager);

                rv_adapter = new MyAdapter(contactInfoArrayList, ViewContacts.this);


                if (contactInfoArrayList.size() > 0){
                    recyclerView.setAdapter(rv_adapter);
                } else {
                    recyclerView.setAdapter(null);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });

        button_createnewcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewContacts.this,CreateNewContact.class);
                startActivityForResult(intent,REQ_CODE_ADDCONTACT);
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ViewContacts.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onItemLongClick(int position) {

        if(isConnected()) {
            db.collection("users")
                    .document(userid).collection("contacts")
                    .document(contactInfoArrayList.get(position).getDocumentID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("test", "Delete succeeded!");
                            db.collection("users")
                                    .document(userid).collection("contacts")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            contactInfoArrayList = new ArrayList<>();
                                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                                ContactInfo contactInfo = new ContactInfo(document.getString("imageurl"),
                                                        document.getString("name"), document.getString("email"),
                                                        document.getString("number"));
                                                contactInfo.setDocumentID(document.getId());
                                                contactInfoArrayList.add(contactInfo);
                                                Log.d(TAG, "Document: " + document.getId() + " " + document.get("title"));
                                            }

                                            Toast.makeText(ViewContacts.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                                            recyclerView.setHasFixedSize(true);

                                            rv_layoutManager = new LinearLayoutManager(ViewContacts.this);

                                            recyclerView.setLayoutManager(rv_layoutManager);

                                            rv_adapter = new MyAdapter(contactInfoArrayList, ViewContacts.this);


                                            if (contactInfoArrayList.size() > 0) {
                                                recyclerView.setAdapter(rv_adapter);
                                            } else {
                                                recyclerView.setAdapter(null);
                                            }
                                        }
                                    });
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("test", "Delete unsuccessful: " + e.getMessage());
                }
            });
        }else{
            Toast.makeText(ViewContacts.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_ADDCONTACT && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            ContactInfo contactInfo = (ContactInfo) bundle.getSerializable(CONTACT_INFO);

            db.collection("users")
                    .document(userid).collection("contacts")
                    .add(contactInfo.toHashMap())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("test", "onSuccess: Successful!");

                            db.collection("users")
                                    .document(userid).collection("contacts")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            contactInfoArrayList= new ArrayList<>();
                                            for (DocumentSnapshot document:queryDocumentSnapshots){
                                                ContactInfo contactInfo = new ContactInfo(document.getString("imageurl"),
                                                        document.getString("name"), document.getString("email"),
                                                        document.getString("number"));
                                                contactInfo.setDocumentID(document.getId());
                                                contactInfoArrayList.add(contactInfo);
                                                Log.d(TAG, "Document: "+document.getId()+" "+document.get("title"));
                                            }

                                            Toast.makeText(ViewContacts.this, "Contact Added!", Toast.LENGTH_SHORT).show();
                                            recyclerView.setHasFixedSize(true);

                                            rv_layoutManager = new LinearLayoutManager(ViewContacts.this);

                                            recyclerView.setLayoutManager(rv_layoutManager);

                                            rv_adapter = new MyAdapter(contactInfoArrayList, ViewContacts.this);


                                            if (contactInfoArrayList.size() > 0){
                                                recyclerView.setAdapter(rv_adapter);
                                            } else {
                                                recyclerView.setAdapter(null);
                                            }
                                        }
                                    });
                        }
                    });
        }
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
