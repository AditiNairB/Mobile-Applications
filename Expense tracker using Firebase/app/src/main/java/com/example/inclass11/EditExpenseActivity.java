package com.example.inclass11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditExpenseActivity extends AppCompatActivity {

    EditText et_title;
    EditText et_cost;
    Spinner spinner;
    Button btn_save;
    Button btn_cancel;

    String s="";

    Expense expense;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        setTitle("Edit Expense");

        expense = (Expense) getIntent().getExtras().get("Expense");

        db = FirebaseFirestore.getInstance();

        et_title = findViewById(R.id.et_title);
        et_cost = findViewById(R.id.et_cost);
        spinner = findViewById(R.id.spinner);
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.category_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        et_title.setText(expense.getTitle());
        String[] stringArray = getResources().getStringArray(R.array.category_list);
        int i = 0;
        for(String cat: stringArray){
                if(cat.equals(expense.getCategory())){
                    break;
                }else{
                    i++;
                }
        }
        spinner.setSelection(i);
        et_cost.setText(expense.getCost().toString());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s = getResources().getStringArray(R.array.category_list)[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_title.getText().toString().length()==0){
                    et_title.setError("Please enter title");
                    Toast.makeText(EditExpenseActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }if(et_cost.getText().toString().length()==0){
                    et_cost.setError("Please enter cost");
                    Toast.makeText(EditExpenseActivity.this, "Please enter cost", Toast.LENGTH_SHORT).show();
                    return;
                }
                Expense editExpense = new Expense();
                editExpense.setTitle(et_title.getText().toString());
                editExpense.setCost(Double.parseDouble(et_cost.getText().toString()));
                editExpense.setCategory(s);
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date date = new Date();
                editExpense.setDate(dateFormat.format(date));
                db.collection("expenses")
                        .document(expense.getId())
                        .update("cost",editExpense.getCost(),"title", editExpense.getTitle(),"category", editExpense.getCategory())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                Intent intent = new Intent(EditExpenseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
