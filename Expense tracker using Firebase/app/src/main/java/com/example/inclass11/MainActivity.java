/**
 * Assignment: InClass11
 * Group No: 9
 * Members: Aditi Balachandran and Luckose Manuel
 */

package com.example.inclass11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener, MyAdapter.SelectItemListener{

    ImageView iv_addbtn;
    TextView tv_nodata;

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    ArrayList<Expense> expenses;
    int i;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Expense App");

        iv_addbtn = findViewById(R.id.iv_addbtn);
        tv_nodata = findViewById(R.id.tv_nodata);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        rv_layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_layoutManager);

        i = 0;

        db = FirebaseFirestore.getInstance();

        getData();

        rv_adapter = new MyAdapter(expenses, this);
        recyclerView.setAdapter(rv_adapter);

        if(expenses.size()!=0){
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            tv_nodata.setVisibility(View.INVISIBLE);
        }

        iv_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode==RESULT_OK){
                HashMap<String, Object> toSave = ((Expense) data.getExtras().get("100")).toHashMap();

                db.collection("expenses")
                    .add(toSave)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("demo","Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo","Failure");
                    }
                });
                getData();
            }else{

            }
        }else if(requestCode == 101){

        }
    }

    @Override
    public void onItemClick(int position) {

        db.collection("expenses").document(expenses.get(position).getId())
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("demo","Delete Successful");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("demo","Delete UnSuccessful" + e.getMessage());
            }
        });

        expenses.remove(position);
        rv_adapter.notifyDataSetChanged();
        if(expenses.size()==0){

            recyclerView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);

            tv_nodata.setVisibility(View.VISIBLE);
        }
        Toast.makeText(MainActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
    }

    public void getData(){
        expenses = new ArrayList<>();
        db.collection("expenses")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            Expense expense = new Expense();
                            expense.setTitle(documentSnapshot.get("title").toString());
                            expense.setCost(Double.parseDouble(documentSnapshot.get("cost").toString()));
                            expense.setCategory(documentSnapshot.get("category").toString());
                            expense.setId(documentSnapshot.getId());
                            expense.setDate(documentSnapshot.get("date").toString());
                            expenses.add(expense);
                        }
                        if(expenses.size()!=0){
                            recyclerView.setVisibility(View.VISIBLE);
                            rv_adapter = new MyAdapter(expenses, MainActivity.this);
                            recyclerView.setAdapter(rv_adapter);
                            tv_nodata.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo","Failure documemt");
                    }
                });
    }

    @Override
    public void onItemSelect(int position) {
        Intent intent = new Intent(MainActivity.this, ShowExpenseActivity.class);
        intent.putExtra("Data", expenses.get(position));
        startActivity(intent);
    }
}
