package com.example.dtbs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class UserSCActivity extends AppCompatActivity {


    TextView full_name, licennum;
    FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scactivity);

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

                        full_name = findViewById(R.id.txtName);
                        licennum = findViewById(R.id.textView8);


                        //String str_fullname = getIntent().getStringExtra("username");
                        full_name.setText("Hi " + fullname);
                        licennum.setText("Licence no. " + licencenum);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserSCActivity.this, "Something went wrong! " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        GetBooked();

        Button bookbtn = (Button) findViewById(R.id.book_btn);

        bookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSCActivity.this, UserBook.class);
                //intent.putExtra("username",username.getText().toString());
                //intent.putExtra("password",password.getText().toString());
                startActivity(intent);

            }
        });

        Button bookbtn2 = (Button) findViewById(R.id.book_btn2);

        bookbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSCActivity.this, UserUpBook.class);
                //intent.putExtra("username",username.getText().toString());
                //intent.putExtra("password",password.getText().toString());
                startActivity(intent);

            }
        });

        Button btnSignout = (Button) findViewById(R.id.signout);

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finishAffinity();

                Intent intent = new Intent(UserSCActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(intent);


            }
        });

    }

    private void GetBooked(){
        try {
            reference = FirebaseDatabase.getInstance().getReference("Instructor");
            //Query query = reference.orderByChild("licence");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        //String keys= datas.getKey();
                        // Toast.makeText(UserSCActivity.this, "DATA " + keys, Toast.LENGTH_LONG).show();

                        for (DataSnapshot ds : datas.child("booking").getChildren()) {
                            if(ds.exists()) {

                            for (DataSnapshot ds2 : ds.getChildren()) {
                                if(ds2.exists()) {
                                    if(ds2.child("licence").exists()) {
                                        String licence = (String)  ds2.child("licence").getValue().toString();
                                        if (licence.equals(User.User_licence)) { // found booked
                                            TextView bookeddate = findViewById(R.id.txtBookedDate);
                                            TextView bookedinsname = findViewById(R.id.txtBookedIns);

                                            Button updatebtn = findViewById(R.id.book_btn2);

                                            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyyMMdd");
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE, MMM d, yyyy", Locale.ENGLISH);

                                            bookedinsname.setText(datas.child("ins_fullname").getValue().toString());
                                            bookeddate.setText(formatter.format(parser.parse( ds.getKey())) + " " + ds2.child("time").getValue().toString());

                                            Button bookbtn = (Button) findViewById(R.id.book_btn);

                                            bookbtn.setEnabled(false);
                                            updatebtn.setEnabled(true);


                                            bookbtn.setBackgroundTintList(ContextCompat.getColorStateList(UserSCActivity.this, com.google.android.material.R.color.mtrl_textinput_disabled_color));
                                            updatebtn.setBackgroundTintList(ContextCompat.getColorStateList(UserSCActivity.this, R.color.orange));

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

                    }

                }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(UserSCActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}