package com.example.dtbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Instructor extends AppCompatActivity {

    TextView full_name, licennum;
    FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String userID;

    public static String[] slottime = {"09.00 - 10.00", "10.00 - 11.00", "11.00 - 12.00", "12.00 - 13.00", "13.00 - 14.00", "14.00 - 15.00", "15.00 - 16.00", "16.00 - 17.00"};
    public static String[] slot = {"slotA", "slotB", "slotC", "slotD", "slotE", "slotF", "slotG", "slotH"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userID  = mAuth.getCurrentUser().getUid();

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if(userProfile != null) {
                        String fullname = userProfile.user_fullname;
                        String licencenum = userProfile.user_licence_no;

                        User.User_FullName = userProfile.user_fullname;
                        User.User_Birthday = userProfile.user_birthday;
                        User.User_Email = userProfile.user_email;
                        User.User_licence = userProfile.user_licence_no;

                        full_name = findViewById(R.id.txtName2);
                        licennum = findViewById(R.id.textView7);

                        full_name.setText("Hi " + fullname);
                        licennum.setText("Licence no. " + licencenum);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Instructor.this, "Something went wrong! " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }



        Button btnSignOut = (Button) findViewById(R.id.signout_ins);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                finishAffinity();

                Intent intent = new Intent(Instructor.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(intent);
            }
        });


        Button btnSetup = (Button) findViewById(R.id.book_btn4);

        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Instructor.this, InsSetupDate.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(intent);


            }
        });

        Button btnSumdate = (Button) findViewById(R.id.sumdate_btn);

        btnSumdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Instructor.this, SumDate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

        Button btnSumtime = (Button) findViewById(R.id.btn_sumtime);

        btnSumtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Instructor.this, SumTime.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(intent);


            }
        });

        Button btnFind = (Button) findViewById(R.id.btn_find);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SearchByLicence(Instructor.this);


            }
        });




    }

    private void SearchByLicence(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("SEARCH BY DRIVER LICENCE NUMBER")
                .setMessage("Enter the licence number")
                .setView(taskEditText)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String linumber = String.valueOf(taskEditText.getText());


                        DatabaseReference mDatabaseRef =FirebaseDatabase.getInstance().getReference("Users");

                        Query query=mDatabaseRef.orderByChild("user_licence_no").equalTo(linumber).limitToFirst(1);

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    for (DataSnapshot data : dataSnapshot.getChildren()) {


                                        User userinfo = data.getValue(User.class);
                                        String name = userinfo.user_fullname;
                                        String dob = userinfo.user_birthday;
                                        String email = userinfo.user_email;
                                        String licence = userinfo.user_licence_no;

                                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(c);
                                        dlgAlert.setMessage("Name : " + name + "\nDate of birth : " + dob + "\nEmail : " + email);
                                        dlgAlert.setTitle("Detail for : " + licence);
                                        dlgAlert.setPositiveButton("OK", null);
                                        dlgAlert.setCancelable(true);
                                        dlgAlert.create().show();



                                    }
                                } else {
                                    Toast.makeText(Instructor.this, "No user found", Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(Instructor.this, "Something went wrong! Error : " + User.InsNo, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }



}