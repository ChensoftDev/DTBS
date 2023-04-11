package com.example.dtbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
//https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
    private final List<myTimeSlot> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context zcontext;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Instructor");


    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<myTimeSlot> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        zcontext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position).getTimetext();
        holder.myTextView.setText(animal);
        holder.TextBookedName.setText(mData.get(position).getTxtBooked());
        holder.timeSwitch.setChecked(mData.get(position).isTimefree());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        Switch timeSwitch;
        TextView TextBookedName;


        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.nameTextView);
            timeSwitch = itemView.findViewById(R.id.switch2);
            TextBookedName = itemView.findViewById(R.id.textView25);
            timeSwitch.setTag("true");
            timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    //Toast.makeText(itemView.getContext(), "Clicked Laugh Vote 1" + getItem(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(itemView.getContext(), "Clicked Laugh Vote 2" + getItem(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    //makeSlotAvailable(InsSetupDate.selectedDate,getAdapterPosition(), b,getItem(getAdapterPosition()));
                    if(TextBookedName.getText().toString().equals("Empty")) {
                        makeSlotAvailable(InsSetupDate.selectedDate, getAdapterPosition(), b);
                    } else {
                        if (timeSwitch.getTag().toString().equals("true")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(zcontext)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Booking cancellation")
                                    .setMessage("There is a person has been booked\nAre you sure to clear the booking?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            //GetBookedAndClear();
                                            //finish();
                                            makeSlotAvailable(InsSetupDate.selectedDate, getAdapterPosition(), b);
                                            TextBookedName.setText("Empty");
                                            timeSwitch.setTag("true");

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what should happen when negative button is clicked
                                            Toast.makeText(zcontext, "Nothing Happened", Toast.LENGTH_LONG).show();
                                            timeSwitch.setTag("false");
                                            timeSwitch.setChecked(false);

                                        }
                                    })
                                    .show();
                        } else {
                            timeSwitch.setTag("true");
                        }
                    }


                }
            });
            //itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).getTimetext();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    private void makeSlotAvailable(String date, int TimeSlot,boolean Status) {

        String timeslot;
        String slottext;
        String Date = date.replaceAll("-","");
        boolean st = false;

        timeslot = Instructor.slot[TimeSlot].toString();
        slottext = Instructor.slottime[TimeSlot].toString();

        /*switch (TimeSlot) {
            case 0:
                timeslot = "slotA";
                break;
            case 1:
                timeslot = "slotB";
                break;
            case 2:
                timeslot = "slotC";
                break;
            case 3:
                timeslot = "slotD";
                break;
            case 4:
                timeslot = "slotE";
                break;
            case 5:
                timeslot = "slotF";
                break;
            case 6:
                timeslot = "slotG";
                break;
            case 7:
                timeslot = "slotH";
                break;
            default:
                timeslot = "" + TimeSlot;
        }*/

        Map<String, Object> values = new HashMap<>();
        values.put("time", slottext);
        values.put("status", Status);
        values.put("name", null);
        values.put("licence", null);


        reference.child(User.User_licence).child("booking").child(Date).child(timeslot)
                .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           // Context context = null;
                          //  Toast.makeText(zcontext, "Clicked Laugh Vote 2", Toast.LENGTH_SHORT).show();

                        } else {
                           // Toast.makeText(zcontext, "Clicked Laugh Vote 5", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}