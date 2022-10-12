package com.sacdev.moneydelivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sacdev.moneydelivery.model.Historymodel;

import java.util.ArrayList;
import java.util.Collections;

public class OrdersFragment extends Fragment {

       RecyclerView recyclerView;
       ArrayList<Historymodel>list = new ArrayList<>();
       reAdapter adapter;
       ProgressDialog progressDialog ;
    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loahistrory();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView =view.findViewById(R.id.orderHistory_id);
        progressDialog = new ProgressDialog(OrdersFragment.this.getContext());
        return  view;

    }
    private void loahistrory()
    {
        progressDialog.setMessage("Loading");
        progressDialog.show();
        try{
        new Thread(new Runnable() {
            public void run(){

                FirebaseDatabase.getInstance().getReference().child("RequestPending").child(starterclass.selfuserid).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(list!=null){
                            list.clear();
                        }
                        if (snapshot.exists()){

                            for(DataSnapshot xy : snapshot.getChildren()){

                                String id = xy.getKey();
                                DataSnapshot snapshot1 = snapshot.child(id);
                                String amt = snapshot1.child("Amount").getValue().toString();
                                String status = snapshot1.child("Status").getValue().toString();
                                String address = snapshot1.child("Address").getValue().toString();

                                Historymodel historymodel = new Historymodel(id,amt,status,address);
                                list.add(historymodel);
                            }
                            Collections.reverse(list);
                            adapter = new reAdapter(list,getContext());
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(adapter);

                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                       progressDialog.dismiss();
                    }
                });
            }
        }).start();
        }catch (Exception e ){

        }
    }

    class reAdapter extends RecyclerView.Adapter<reAdapter.ViewHolder>{
        ArrayList<Historymodel> arrayList ;
        Context context;

        public reAdapter(ArrayList<Historymodel> arrayList , Context context){
            this.arrayList = arrayList;
            this.context = context;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.orderrecyclerview, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position)
        {
            holder.amount.setText(arrayList.get(position).getAmount());
            holder.status.setText(arrayList.get(position).getStatus());
            if(arrayList.get(position).getStatus().equals("Pending")){
                holder.cancleorder.setVisibility(View.VISIBLE);
            }else{
                holder.cancleorder.setVisibility(View.GONE);
                holder.status.setTextColor(getResources().getColor(R.color.greeb));
            }
            holder.addresstxt.setText(arrayList.get(position).getAddress());
            holder.cancleorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                       progressDialog.setMessage("Cancelling Order");
                       progressDialog.show();
                        FirebaseDatabase.getInstance().getReference().child("RequestPending").
                                child(starterclass.selfuserid).child(arrayList.get(position).getId())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        arrayList.remove(position);
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Order cancel successfully", Snackbar.LENGTH_LONG)
                                                .setTextColor(Color.GREEN)
                                                .setBackgroundTint(Color.WHITE)
                                                .show();
                                        //  removeItem(position);
                                        notifyDataSetChanged();
                                        progressDialog.cancel();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Request Failed Try Again", Snackbar.LENGTH_LONG)
                                                .setTextColor(Color.RED)
                                                .setBackgroundTint(Color.WHITE)
                                                .show();
                                        progressDialog.cancel();
                                    }
                                });


                }
            });
        }

        private void removeItem(int position)
        {
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList.size());
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            TextView addresstxt , amount ,status, cancleorder;
            public ViewHolder(View itemView) {
                super(itemView);
                addresstxt = itemView.findViewById(R.id.addresstxt_order_id);
                amount = itemView.findViewById(R.id.amounttxt_order);
                status = itemView.findViewById(R.id.statustxt_id_order);
                cancleorder =itemView.findViewById(R.id.cancleorderbtn_id);
            }
        }

    }

}