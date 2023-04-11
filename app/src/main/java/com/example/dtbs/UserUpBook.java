package com.example.dtbs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserUpBook extends AppCompatActivity {

    EditText dateEdt;
    private int mYear, mMonth, mDay;
    List<ListElement> elements;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Instructor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_upbook);

        ConfirmActivity.UpdateMode = true;

        Button bookbtnback = (Button) findViewById(R.id.button_back);

        bookbtnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        Button bookbtncancel = (Button) findViewById(R.id.button_cancel);

        bookbtncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                         AlertDialog alertDialog = new AlertDialog.Builder(UserUpBook.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Booking cancellation")
                                .setMessage("Are you sure to cancel the booking?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what would happen when positive button is clicked
                                        GetBookedAndClear();
                                        //finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what should happen when negative button is clicked
                                        Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .show();
            }
        });




        // on below line we are initializing our variables.
        dateEdt = findViewById(R.id.txtdate);

        // on below line we are adding click listener
        // for our pick date button
        dateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        UserUpBook.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                dateEdt.setText(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
                                User.DateBooking = dateEdt.getText().toString();

                                GetInstructorAndGetSlot(User.DateBooking);


                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.setCanceledOnTouchOutside(false);
                datePickerDialog.show();
            }
        });

        dateEdt.performClick();





    }

    private void GetInstructorAndGetSlot(String date) { // this will get the instructor who is the most free
        String Date = date.replaceAll("-","");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Map<String, Integer> values = new HashMap<>();
                    Map.Entry<String, Integer> maxFree = null;

                    if (dataSnapshot != null) {

                        int countfree = 0; //count the instructor available amount

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            countfree = 0;//reset
                            if (snapshot.child("booking").child(Date).getChildrenCount() > 0) {
                                for (DataSnapshot snapshot2 : snapshot.child("booking").child(Date).getChildren()) {

                                    if (snapshot2.child("status").exists()) {
                                        if (snapshot2.child("status").getValue(boolean.class).equals(true)) {

                                            InstructorInfo InsProfile = snapshot.getValue(InstructorInfo.class);

                                            values.put(InsProfile.ins_licence_no, countfree);

                                            countfree++;

                                            for (Map.Entry<String, Integer> free : values.entrySet()) {
                                                if (maxFree == null || free.getValue().compareTo(maxFree.getValue()) > 0) {
                                                    maxFree = free;
                                                    User.InsNo = InsProfile.ins_licence_no;
                                                    User.InsName = InsProfile.ins_fullname;

                                                    //Toast.makeText(UserBook.this, "Something went wrong! Error : " + User.InsNo, Toast.LENGTH_LONG).show();

                                                }
                                            }

                                            getTimeSlot(Date);


                                        }
                                    }


                                }
                            } else {
                                Toast.makeText(UserUpBook.this, "Sorry no time available Please select other date", Toast.LENGTH_LONG).show();
                                dateEdt.performClick();
                            }
                        }


                        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.forceLayout();


                    }
                } catch (Exception e) {
                    Toast.makeText(UserUpBook.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    private void getTimeSlot(String date) {
        String Date = date.replaceAll("-","");


        reference.child(User.InsNo).child("booking").child(Date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() > 0) {

                        elements = new ArrayList<>();
                        elements.clear();

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            try {
                                if (snap.child("status").exists()) {

                                    boolean status;
                                    String name;
                                    String time;



                                    status = (boolean) snap.child("status").getValue(boolean.class);
                                    name = (String) snap.child("name").getValue(String.class);
                                    time = (String) snap.child("time").getValue(String.class);


                                    if (status == true) {
                                        elements.add(new ListElement("#569A00", time, User.InsName, "Available", snap.getKey()));
                                    } else {
                                        elements.add(new ListElement("#D80011", time, User.InsName, "Not Available", snap.getKey()));
                                    }
                                }

                            }
                            catch ( Exception e )  {
                                Toast.makeText(UserUpBook.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            finally {
                                init();
                            }
                        }
                    }
                } else {
                    RecyclerView recyclerView = findViewById(R.id.listRecyclerView);

                    recyclerView.removeAllViewsInLayout();
                    recyclerView.forceLayout();

                    Toast.makeText(UserUpBook.this, "Sorry no time available Please select other date", Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void init() {
        //populateList();

        ListAdapter listAdapter = new ListAdapter(elements, this);
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
/*
    private void cancelbooking(String InsNo,String licence, String name, String day, String time) {

        Map<String, Object> values = new HashMap<>();
        values.put("name", User.User_FullName);
        values.put("status", false);
        values.put("licence", User.User_licence);


        reference.child(InsNo).child("booking").child(day).child(time)
                .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(ConfirmActivity.this, UserSCActivity.class));
                            Toast.makeText(ConfirmActivity.this, "Booking Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ConfirmActivity.this, "Booking Error : " + task.getException(), Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }*/

    private void GetBookedAndClear(){
        try {
            reference = FirebaseDatabase.getInstance().getReference("Instructor");
            Query query = reference.orderByChild("licence");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){ //get inscode

                        for(DataSnapshot ds : datas.child("booking").getChildren()){

                            for(DataSnapshot ds2 : ds.getChildren()) {
                                if(ds2.child("licence").exists()) {
                                if (ds2.child("licence").getValue().toString().equals(User.User_licence)) { // found booked


                                    Map<String, Object> values = new HashMap<>();
                                    values.put("name", null);
                                    values.put("status", true);
                                    values.put("licence", null);


                                    reference.child(datas.getKey()).child("booking").child(ds.getKey()).child(ds2.getKey())
                                            .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        finishAffinity();
                                                        startActivity(new Intent(UserUpBook.this, UserSCActivity.class));
                                                        Toast.makeText(UserUpBook.this, "Cancel Successfully", Toast.LENGTH_LONG).show();

                                                    } else {
                                                        Toast.makeText(UserUpBook.this, "Cancel Error : " + task.getException(), Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });


                                    return;
                                }
                                }
                                //String name = ds2.child("name").getValue().toString();
                                //String latitudes = ds.child("name").getValue().toString();
                                //String longitudes=ds.child("longitudes").getValue().toString();
                            }
                        }
                    }


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(UserUpBook.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }





}