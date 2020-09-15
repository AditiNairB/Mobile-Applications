package com.example.inclass11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {

    EditText et_title;
    EditText et_cost;
    Spinner spinner;
    Button btn_addexpense;
    Button btn_cancel;

    String s = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);


        setTitle("Add Expense");
        et_title = findViewById(R.id.et_title);
        et_cost = findViewById(R.id.et_cost);
        spinner = findViewById(R.id.spinner);
        btn_addexpense = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.category_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s = getResources().getStringArray(R.array.category_list)[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_addexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_title.getText().toString().length()==0){
                    et_title.setError("Please enter title");
                    Toast.makeText(AddExpenseActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }if(et_cost.getText().toString().length()==0){
                    et_cost.setError("Please enter cost");
                    Toast.makeText(AddExpenseActivity.this, "Please enter cost", Toast.LENGTH_SHORT).show();
                    return;
                }
                Expense expense = new Expense();
                expense.setTitle(et_title.getText().toString());
                expense.setCost(Double.parseDouble(et_cost.getText().toString()));
                expense.setCategory(s);
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date date = new Date();
                String d = dateFormat.format(date);
                expense.setDate(dateFormat.format(date));
                Intent intent = new Intent();
                intent.putExtra("100", expense);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


    }
}
