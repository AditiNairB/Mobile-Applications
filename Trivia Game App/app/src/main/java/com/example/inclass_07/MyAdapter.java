package com.example.inclass_07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    ArrayList<String> choices = new ArrayList<>();
    private OnItemClickListener newOnItemClickListener;
    Context ctx;

    public MyAdapter(ArrayList<String> choices, OnItemClickListener newOnItemClickListener, TriviaActivity triviaActivity) {
        this.choices = choices;
        this.newOnItemClickListener = newOnItemClickListener;
        this.ctx = triviaActivity;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_text_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(rv_layout, newOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder,final int position) {

        holder.tv_option.setText(choices.get(position));

    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_option;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull final View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_option = itemView.findViewById(R.id.tv_option);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
