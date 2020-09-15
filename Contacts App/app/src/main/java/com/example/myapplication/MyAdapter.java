package com.example.myapplication;

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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private final ViewContacts ctx;
    private OnItemLongClickListener newOnItemLongClickListener;


    ArrayList<ContactInfo> contactInfoArrayList;

    public MyAdapter(ArrayList<ContactInfo> contacts, ViewContacts viewContacts) {
        this.contactInfoArrayList = contacts;
        this.ctx = viewContacts;
        newOnItemLongClickListener = (OnItemLongClickListener) ctx;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        ImageView imageView_contactpic;
        TextView tv_contactname;
        TextView tv_contactphone;
        TextView tv_contactemail;
        OnItemLongClickListener onItemLongClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            imageView_contactpic = itemView.findViewById(R.id.imageView_contactpic);
            tv_contactname = itemView.findViewById(R.id.tv_contactname);
            tv_contactphone = itemView.findViewById(R.id.tv_contactphone);
            tv_contactemail = itemView.findViewById(R.id.tv_contactemail);
            this.onItemLongClickListener =  onItemLongClickListener;
        }


        @Override
        public boolean onLongClick(View view) {
            return onItemLongClickListener.onItemLongClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rv_layout, newOnItemLongClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        newOnItemLongClickListener = (OnItemLongClickListener) ctx;
        Picasso.get().load(this.contactInfoArrayList.get(position).getContactImageUrl()).into(holder.imageView_contactpic);
        holder.tv_contactname.setText(this.contactInfoArrayList.get(position).getContactName());
        holder.tv_contactphone.setText(this.contactInfoArrayList.get(position).getContactNumber());
        holder.tv_contactemail.setText(this.contactInfoArrayList.get(position).getContactEmail());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                newOnItemLongClickListener.onItemLongClick(position);
                return true;
            }
        });

    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(int position);
    }
    @Override
    public int getItemCount() {
        return contactInfoArrayList.size();
    }
}
