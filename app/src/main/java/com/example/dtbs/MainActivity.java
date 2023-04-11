package com.example.dtbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button)findViewById(R.id.button_login);
        EditText username   = (EditText)findViewById(R.id.username);
        EditText password   = (EditText)findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        CheckUserStatus();



        login.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                       // Log.v("EditText value=", username.getText().toString());
                       // Log.v("EditText value=", password.getText().toString());
                                /*AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Are you sure to Exit")
                                .setMessage("Exiting will call finish() method")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what would happen when positive button is clicked
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what should happen when negative button is clicked
                                        Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .show();*/
                    }
                });

        Button loginbtn = (Button) findViewById(R.id.button_login);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, UserSCActivity.class);
                //intent.putExtra("username",username.getText().toString());
                //intent.putExtra("password",password.getText().toString());
                String username_login,password_login;
                username_login = username.getText().toString();
                password_login = password.getText().toString();
                userLogin(username_login,password_login);
                //startActivity(intent);

            }
        });


        TextView txtviewregister = (TextView) findViewById(R.id.txtview_register);

        txtviewregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterAct.class);
                //intent.putExtra(key:,value:);
                startActivity(intent);

            }
        });


    }

    private void userLogin(String email,String password) {
        if(email.isEmpty()) {
            return;
        }

        if(password.isEmpty()){
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    CheckUserStatus();
                } else {
                    Toast.makeText(MainActivity.this, "Login Error : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void CheckUserStatus() {
        try {
            if (mAuth.getCurrentUser() != null) {
                reference = FirebaseDatabase.getInstance().getReference("Users");
                userID  = mAuth.getCurrentUser().getUid();

                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if(userProfile != null) {
                            String usercode = userProfile.user_code;

                            if(usercode.equals("9999")) {

                                Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                                finishAffinity();
                                startActivity(new Intent(MainActivity.this, Instructor.class));

                            } else {

                                Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                                finishAffinity();
                                startActivity(new Intent(MainActivity.this, UserSCActivity.class));

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Something went wrong! " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}