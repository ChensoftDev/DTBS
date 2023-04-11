package com.example.dtbs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ListElement> mData;
    private final LayoutInflater mInflater;
    private final Context context;




    public ListAdapter(List<ListElement> itemList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }

    @Override
    public int getItemCount() { return mData.size(); }


    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_element, null);
        return new ListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.bindData(mData.get(position));
    }


    public void setItems(List<ListElement> items) { mData = items; }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView name, city, status, slotno;
        EditText date;
        CardView cv;



        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            name = itemView.findViewById(R.id.nameTextView);
            city = itemView.findViewById(R.id.cityTextView);
            status = itemView.findViewById(R.id.statusTextView);
            cv = itemView.findViewById(R.id.cv);
            slotno = itemView.findViewById(R.id.slotno);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!status.getText().equals("Not Available")) {
                        Intent intent = new Intent(view.getContext(), ConfirmActivity.class);
                        //intent.putExtra(key:,value:);
                        intent.putExtra("slotno",slotno.getText());
                        intent.putExtra("date", User.DateBooking);
                        intent.putExtra("time",name.getText());
                        //intent.putExtra("password",password.getText().toString());
                        context.startActivity(intent);
                    }


                   // Toast.makeText(view.getContext(), "Clicked Laugh Vote" + slotno.getText(), Toast.LENGTH_SHORT).show();
                }
            });

        }


        void bindData(final ListElement item) {
            iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            name.setText(item.getName());
            city.setText(item.getCity());
            status.setText(item.getStatus());
            slotno.setText(item.getSlotname());

        }
    }
}
