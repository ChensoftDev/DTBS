package com.example.dtbs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class sumDateAdapter extends RecyclerView.Adapter<sumDateAdapter.MyViewHolder> {
private ArrayList<clsSumDate> dateList;

public sumDateAdapter(ArrayList<clsSumDate> dateList){
    this.dateList = dateList;
}

public class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView txtDate;
    private ProgressBar proc;
    private TextView txtNum;

    public MyViewHolder(final View view){
        super(view);
        txtDate = view.findViewById(R.id.nameTextView);
        proc = view.findViewById(R.id.progressBar2);
        txtNum = view.findViewById(R.id.textView24);
    }
}
    @NonNull
    @Override
    public sumDateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sumdate_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull sumDateAdapter.MyViewHolder holder, int position) {
        String txtDate = dateList.get(position).getTxtDate();
        int proc = dateList.get(position).getPercent();
        int num = dateList.get(position).getNum();
        holder.txtDate.setText(txtDate);
        holder.proc.setProgress(proc);
        holder.txtNum.setText(""+ num);
    }

    @Override
    public int getItemCount() {
       return dateList.size();
    }
}
