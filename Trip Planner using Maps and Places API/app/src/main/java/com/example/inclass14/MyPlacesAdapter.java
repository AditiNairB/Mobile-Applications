package com.example.inclass14;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyPlacesAdapter extends RecyclerView.Adapter<MyPlacesAdapter.MyViewHolder> {


    static AddPlacesActivity ctx;
    private OnItemClickListener newOnItemClickListener;
    ArrayList<AddPlacesActivity.result> resultArrayList = null;

    public MyPlacesAdapter(ArrayList<AddPlacesActivity.result> resultArrayList, AddPlacesActivity addPlacesActivity) {
        this.resultArrayList = resultArrayList;
        this.ctx = addPlacesActivity;
        this.newOnItemClickListener = addPlacesActivity;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_placeName;
        ImageView iv_placesIcon;
        ImageView iv_addPlacesIcon;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_placeName = itemView.findViewById(R.id.tv_place);
            iv_placesIcon = itemView.findViewById(R.id.iv_placesIcon);
            iv_addPlacesIcon = itemView.findViewById(R.id.iv_add);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyPlacesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.places_recycler_layout, parent, false);
        MyPlacesAdapter.MyViewHolder viewHolder = new MyPlacesAdapter.MyViewHolder(rv_layout,  newOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPlacesAdapter.MyViewHolder holder, final int position) {

        holder.tv_placeName.setText(resultArrayList.get(position).name);
        Picasso.get().load(resultArrayList.get(position).icon).into(holder.iv_placesIcon);
        holder.iv_addPlacesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newOnItemClickListener.onItemClick(position);
            }
        });

    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }
}
