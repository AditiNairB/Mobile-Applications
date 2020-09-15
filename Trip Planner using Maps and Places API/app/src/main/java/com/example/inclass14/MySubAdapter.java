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
import java.util.Collection;
import java.util.Collections;

public class MySubAdapter extends RecyclerView.Adapter<MySubAdapter.MyViewHolder> {


    static MainActivity ctx;
    private OnItemLongClickListener newOnItemLongClickListener;
    ArrayList<Places> placesArrayList = null;
    TripDetails tripDetails = new TripDetails();

    public MySubAdapter(ArrayList<Places> placesDetails, MainActivity mainActivity, TripDetails tripDetails) {
        this.placesArrayList = placesDetails;
        this.ctx = mainActivity;
//        Collections.reverse(placesArrayList);
        newOnItemLongClickListener = (OnItemLongClickListener) ctx;
        this.tripDetails = tripDetails;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {//implements View.OnLongClickListener

        ImageView iv_delete;
        ImageView iv_icon;
        TextView tv_place;
        TextView placesID;
        TextView tripID;
//        OnItemLongClickListener onItemLongClickListener;

        public MyViewHolder(@NonNull View itemView) { //, OnItemLongClickListener onItemLongClickListener
            super(itemView);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_place = itemView.findViewById(R.id.tv_place);
            placesID = itemView.findViewById(R.id.placesID);
            tripID = itemView.findViewById(R.id.tripID);
//            this.onItemLongClickListener =  onItemLongClickListener;
        }

//        @Override
//        public boolean onLongClick(View view) {
//            return onItemLongClickListener.onItemLongClick(getAdapterPosition());
//        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_recycler_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rv_layout);//(rv_layout, newOnItemLongClickListener)
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

//        newOnItemLongClickListener = (OnItemLongClickListener) ctx;
//        Picasso.get().load(this.contactInfoArrayList.get(position).getContactImageUrl()).into(holder.imageView_contactpic);
//        holder.tv_contactname.setText(this.contactInfoArrayList.get(position).getContactName());
//        holder.tv_contactphone.setText(this.contactInfoArrayList.get(position).getContactNumber());
//        holder.tv_contactemail.setText(this.contactInfoArrayList.get(position).getContactEmail());


        holder.tv_place.setText(placesArrayList.get(position).getName());
        Picasso.get().load(placesArrayList.get(position).icon).into(holder.iv_icon);
        holder.placesID.setText(placesArrayList.get(position).getDocumentID());
        holder.tripID.setText(tripDetails.getDocumentID());
//        holder.tv_tripNo.setText("Trip 1");
//        Places place = new Places();
//        place.setCity("Charlotte");
//        place.setTrip("trip 1");
//        tripDetails.add(place);
//        place.setCity("Bombay");
//        place.setTrip("trip 2");
//        tripDetails.add(place);

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                newOnItemLongClickListener.onItemLongClick(position);
//                return true;
//            }
//        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newOnItemLongClickListener.onItemLongClick(holder.placesID.getText().toString(), holder.tripID.getText().toString());
            }
        });

    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(String placesID, String tripID);
    }
    @Override
    public int getItemCount() {
        return placesArrayList.size();
    }
}
