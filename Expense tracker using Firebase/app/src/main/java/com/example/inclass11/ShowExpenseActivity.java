package com.example.inclass11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowExpenseActivity extends AppCompatActivity {

    TextView tv_name;
    TextView tv_category;
    TextView tv_amount;
    TextView tv_date;

    Button btn_editExpense;
    Button btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expense);

        setTitle("Show Expense");

        tv_name = findViewById(R.id.tv_name);
        tv_category = findViewById(R.id.tv_category);
        tv_amount = findViewById(R.id.tv_amount);
        tv_date = findViewById(R.id.tv_date);
        btn_editExpense = findViewById(R.id.btn_editExpense);
        btn_close = findViewById(R.id.btn_close);

        final Expense expense = (Expense) getIntent().getExtras().get("Data");

        tv_name.setText(expense.getTitle());
        tv_category.setText(expense.getCategory());
        tv_amount.setText("$"+expense.getCost().toString());
        tv_date.setText(expense.getDate());

        btn_editExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowExpenseActivity.this, EditExpenseActivity.class);
                intent.putExtra("Expense", expense);
                startActivity(intent);
                finish();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
