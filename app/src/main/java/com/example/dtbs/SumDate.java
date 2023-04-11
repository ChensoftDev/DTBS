package com.example.dtbs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SumDate extends AppCompatActivity {

    EditText dateEdt;

    private ArrayList<clsSumDate> dateList;
    private RecyclerView recyclerView;

    sumDateAdapter adapter;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Instructor");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_date);
        recyclerView = findViewById(R.id.listRecyclerView);
        dateList = new ArrayList<>();

        // on below line we are initializing our variables.
        dateEdt = findViewById(R.id.txtdate2);

        adapter = new sumDateAdapter(dateList);






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
                        SumDate.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //Toast.makeText(getApplicationContext(),"Nothing Happened" + sDay,Toast.LENGTH_LONG).show();
                                String date = (monthOfYear+1) + "/" + (dayOfMonth) + "/" + year;
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                                try {
                                    Date convertedDate = dateFormat.parse(date);
                                    c.setTime(convertedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                int numDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                                DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM, yyyy", Locale.ENGLISH);

                                String monthstr;

                                monthstr = formatter.format(parser.parse(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth)));

                                dateEdt.setText(monthstr);


                                dateList.clear();


                                reference.child(User.User_licence).child("booking").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            try {


                                            int totalcount = 0;

                                            for (int i = 1; i <= numDays; i++) {

                                                //slotA - slotH get data
                                                String querystr = year + String.format("%02d", (monthOfYear + 1)) + String.format("%02d", i);
                                                int counter = 0;
                                                if (dataSnapshot.child(querystr).exists()) {


                                                    for (DataSnapshot snapshot2 : dataSnapshot.child(querystr).getChildren()) {
                                                        // Toast.makeText(SumDate.this, snapshot2.getKey(), Toast.LENGTH_LONG).show()
                                                        //Log.i("TEST","TEST");
                                                        if (snapshot2.child("name").exists() && snapshot2.child("status").exists()) {
                                                            if (snapshot2.child("status").getValue(boolean.class) == false) {
                                                                counter++;
                                                                totalcount++;
                                                            }
                                                        }
                                                    }
                                                }

                                                int percent = (int) Math.round((counter * 100) / (Instructor.slot.length)); //8 = 8slot
                                                dateList.add(new clsSumDate("" + String.format("%02d", i) + "/" + String.format("%02d", monthOfYear + 1), percent, counter));
                                                adapter.notifyDataSetChanged();
                                            }

                                            int totalpercent = (int) Math.round((totalcount * 100) / ((Instructor.slot.length) * numDays)); //8 = 8slot

                                            dateList.add(new clsSumDate("Total", totalpercent, totalcount));
                                        } catch (Exception e) {
                                                Toast.makeText(SumDate.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

                                            }
                                        }

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });


                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.


                // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.setCanceledOnTouchOutside(false);
                datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, null, (DialogInterface.OnClickListener) null);

                datePickerDialog.show();



            }
        });

        dateEdt.performClick();

        //setDateList();
        setAdapter();


    }


    private void setAdapter() {
        //sumDateAdapter adapter = new sumDateAdapter(dateList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setDateList() {
        for (int i = 0; i < 5; i++) {
            dateList.add(new clsSumDate("FFFFFF",20,20));
        }
        //dateList.add(new clsSumDate("FFFFFF",100,20));
        //dateList.add(new clsSumDate("FFFFFF",20,20));
    }
}