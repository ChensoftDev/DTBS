package com.example.dtbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class UserBook extends AppCompatActivity {

    EditText dateEdt;

    List<ListElement> elements;

    FirebaseAuth mAuth;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Instructor");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book);


        ConfirmActivity.UpdateMode = false;




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
                        UserBook.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //Toast.makeText(getApplicationContext(),"Nothing Happened" + sDay,Toast.LENGTH_LONG).show();


                                //dateEdt.setText(String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year);
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
                datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, null, (DialogInterface.OnClickListener) null);

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
                                Toast.makeText(UserBook.this, "Sorry no time available Please select other date", Toast.LENGTH_LONG).show();
                                dateEdt.performClick();
                            }
                        }


                        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.forceLayout();


                    }
                } catch (Exception e) {
                    Toast.makeText(UserBook.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    private void getTimeSlot(String date) {
        String Date = date.replaceAll("-","");
        //elements.clear();

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
                                Toast.makeText(UserBook.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
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

                    Toast.makeText(UserBook.this, "Sorry no time available Please select other date", Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void init() {

      //  populateList();

        ListAdapter listAdapter = new ListAdapter(elements, this);
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);


    }





}