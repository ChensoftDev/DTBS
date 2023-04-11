package com.example.dtbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class ConfirmActivity extends AppCompatActivity {
    public static boolean UpdateMode;

    String slotno;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Instructor");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        try {
            // String userProfile = User.getUser_fullname();
            slotno = getIntent().getStringExtra("slotno");
            String time = getIntent().getStringExtra("time");

            TextView fullname = (TextView) findViewById(R.id.txtBookedDate);
            TextView licence = (TextView) findViewById(R.id.txtBookedIns);
            TextView Date = (TextView) findViewById(R.id.txtCDate);
            TextView InsName = (TextView) findViewById(R.id.txtCIns);
            TextView Time = (TextView) findViewById(R.id.txtCTime);

            fullname.setText(User.User_FullName);
            licence.setText(User.User_licence);
            InsName.setText(User.InsName);
            Time.setText(time);
            Date.setText(User.DateBooking);

        } catch (Exception e) {
            Toast.makeText(ConfirmActivity.this, "Error : " + e.getMessage() ,Toast.LENGTH_SHORT).show();
        }

        Button bookbtnback = (Button) findViewById(R.id.button_back2);

        bookbtnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
                finish();
            }
        });

        Button btnconfirm = (Button) findViewById(R.id.button_confirm);

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(UpdateMode == true) {
                        GetBookedAndClearAndBook();
                    } else {
                        bookTimeslot(User.InsNo,User.User_licence,User.User_FullName,User.DateBooking,slotno);
                    }

                } catch (Exception e) {
                    Toast.makeText(ConfirmActivity.this, "Booking Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void bookTimeslot(String InsNo,String licence, String name, String day, String time) {

        String Date = day.replaceAll("-","");
        Map<String, Object> values = new HashMap<>();
        values.put("name", User.User_FullName);
        values.put("status", false);
        values.put("licence", User.User_licence);


        reference.child(InsNo).child("booking").child(Date).child(time)
                .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finishAffinity();
                            startActivity(new Intent(ConfirmActivity.this, UserSCActivity.class));
                            Toast.makeText(ConfirmActivity.this, "Booking Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ConfirmActivity.this, "Booking Error : " + task.getException(), Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }


    private void GetBookedAndClearAndBook(){
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
                                                            bookTimeslot(User.InsNo,User.User_licence,User.User_FullName,User.DateBooking,slotno);
                                                        } else {
                                                            Toast.makeText(ConfirmActivity.this, "Cancel Error : " + task.getException(), Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                        return;
                                    }
                                }
                            }
                        }
                    }


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ConfirmActivity.this, "Something went wrong! " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(ConfirmActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}


