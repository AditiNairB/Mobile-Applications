package com.example.inclass08;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    OnDeleteClickListener onDeleteClickListener;
    ArrayList<EmailContent> emailList = new ArrayList<EmailContent>();
    private OnItemClickListener newOnItemClickListener;
    Context ctx;
    final String authKey = MainActivity.sharedPref.getString("token","");

    public MyAdapter(ArrayList<EmailContent> emails, OnItemClickListener newOnItemClickListener, Inbox inbox) {
        this.emailList = emails;
        this.newOnItemClickListener = newOnItemClickListener;
        this.ctx = inbox;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.email_row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(rv_layout, newOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.ViewHolder holder, final int position) {

        String pattern1 = "MMM dd, yyyy";
        String pattern2 = "yyyy-mm-dd";
        String dateNew = "";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern2);
        String dateSet = emailList.get(position).getCreated_at().split(" ")[0].trim();
        try {
            Date date = simpleDateFormat.parse(dateSet);
            dateNew = simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        onDeleteClickListener = (OnDeleteClickListener) ctx;
        holder.tv_emailsubject.setText(emailList.get(position).getSubject());
        holder.tv_date.setText(dateNew);

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new DeleteAPi().execute(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_emailsubject;
        TextView tv_date;
        ImageButton btn_delete;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull final View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_emailsubject = itemView.findViewById(R.id.tv_emailsubject);
            tv_date = itemView.findViewById(R.id.tv_date);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnDeleteClickListener {
        void onItemDelete(int position);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public class DeleteAPi extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {


            final OkHttpClient client = new OkHttpClient();

            int index= integers[0];
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+emailList.get(index).getId())
                    .header("Authorization","Bearer "+ authKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Couldnt DELETE!!!");
                }else{


                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return index;
        }

        @Override
        protected void onPostExecute(Integer index) {
            super.onPostExecute(index);

            onDeleteClickListener.onItemDelete(index);
        }

    }

}
