package com.example.dtbs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InsSetupTime extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;


    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Instructor");


    private ArrayList<myTimeSlot> settime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_setup_time);

        recyclerView = findViewById(R.id.listRecyclerView);
        settime = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(this,settime);

        TextView Title;
        String date = getIntent().getStringExtra("selectedDate");
        Title = findViewById(R.id.titleTextView);

        Title.setText(date);

        buildTimeSlot(date);


    }

     private void buildTimeSlot(String date) {


             String Date = date.replaceAll("-","");

             settime.clear();

             for (int i = 0; i < Instructor.slot.length; i++) {

                 int finalI = i;

                 reference.child(User.User_licence).child("booking").child(Date).child(Instructor.slot[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         try {
                             boolean swstatus;
                             String bookedname;

                             if (dataSnapshot.child("status").exists()) {
                                 swstatus = dataSnapshot.child("status").getValue(boolean.class);

                                 if (dataSnapshot.child("name").exists() || dataSnapshot.child("licence").exists()) {
                                     bookedname = "Bookked by : " + dataSnapshot.child("name").getValue(String.class) + " - " + dataSnapshot.child("licence").getValue(String.class);
                                 } else {
                                     bookedname = "Empty";
                                 }

                             } else {
                                 swstatus = false;
                                 bookedname = "Empty";
                             }



                             settime.add(new myTimeSlot(Instructor.slottime[finalI], swstatus, bookedname));
                             adapter.notifyDataSetChanged();

                         } catch (Exception e){
                             Toast.makeText(InsSetupTime.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

                         }finally {
                             setAdapter();
                         }

                     }


                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });


             }

     }

    private void setAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


}