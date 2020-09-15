package com.example.inclass11;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private final MainActivity ctx;
    private OnItemClickListener newOnItemClickListener;
    SelectItemListener selectItemListener;
    ArrayList<Expense> expenseArrayList;

    public MyAdapter(ArrayList<Expense> expenseArrayList, MainActivity mainActivity) {
        this.expenseArrayList = expenseArrayList;
        this.ctx = mainActivity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_expense;
        TextView tv_cost;

        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_expense = itemView.findViewById(R.id.tv_expense);
            tv_cost = itemView.findViewById(R.id.tv_cost);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rv_layout, newOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, final int position) {
        newOnItemClickListener = (OnItemClickListener) ctx;
        selectItemListener = (SelectItemListener) ctx;
        holder.tv_expense.setText(expenseArrayList.get(position).getTitle());
        holder.tv_cost.setText("$"+expenseArrayList.get(position).getCost().toString());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                newOnItemClickListener.onItemClick(position);
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItemListener.onItemSelect(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseArrayList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface SelectItemListener{
        void onItemSelect(int position);
    }
}
