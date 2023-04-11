package com.example.dtbs;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterAct extends AppCompatActivity {

    FirebaseAuth mAuth;
    private EditText regFName,regLName,regDOB,regLiNO,regPass,regEmail,regInCode;

    private ProgressBar progressBar;

    Button btnReg;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regFName = findViewById(R.id.first_name_reg);
        regLName = findViewById(R.id.lastname_reg);
        regDOB = findViewById(R.id.dob_reg);
        regLiNO = findViewById(R.id.licenceno_reg);
        regPass = findViewById(R.id.password_reg);
        regEmail = findViewById(R.id.email_reg);
        regInCode = findViewById(R.id.instructorcode_reg);

        btnReg = findViewById(R.id.button_register);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnReg.setOnClickListener(view -> {
            createUser();
            closeKeyboard();
        });




        // on below line we are adding click listener
        // for our pick date button
        regDOB.setOnClickListener(new View.OnClickListener() {
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
                        RegisterAct.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                regDOB.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.setCanceledOnTouchOutside(false);
                datePickerDialog.show();
            }
        });







        TextView txtviewbacklogin = (TextView) findViewById(R.id.txtview_backlogin);

        txtviewbacklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAct.this, MainActivity.class);
                //intent.putExtra(key:,value:);
                startActivity(intent);
                finish();
            }
        });

    }

    private void createUser() {
        String firstname = regFName.getText().toString();
        String lastname = regLName.getText().toString();
        String birthday = regDOB.getText().toString();
        String licence_no = regLiNO.getText().toString();
        String password = regPass.getText().toString();
        String email = regEmail.getText().toString();
        String inscode = regInCode.getText().toString();

        if(firstname.isEmpty()) {
            regFName.setError("Full name is required!");
            regFName.requestFocus();
            return;
        }

        if(lastname.isEmpty()) {
            regLName.setError("Last name is required!");
            regLName.requestFocus();
            return;
        }

        if(birthday.isEmpty()) {
            regDOB.setError("Date of birth is required!");
            regDOB.requestFocus();
            return;
        }

        if(licence_no.isEmpty()) {
            regLiNO.setError("Licence number is required!");
            regLiNO.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            regEmail.setError("Email is required!");
            regEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            regPass.setError("Password is required!");
            regPass.requestFocus();
            return;
        }

        if(!inscode.isEmpty()) {
            if(!inscode.equals("9999")) {
                regInCode.setError("Instructor code invalid");
                regInCode.requestFocus();
                return;
            }
        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {


                                User user = new User(firstname + " " + lastname, birthday, licence_no, email, inscode);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterAct.this, "Register Successfully", Toast.LENGTH_LONG).show();
                                                finishAffinity();
                                                Intent intent = new Intent(RegisterAct.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(RegisterAct.this, "Registration Error : " + task.getException(), Toast.LENGTH_LONG).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                            if(inscode.equals("9999")) {

                                InstructorInfo InsUser = new InstructorInfo(firstname + " " + lastname, licence_no, email);
                                FirebaseDatabase.getInstance().getReference("Instructor/" + licence_no)
                                        .setValue(InsUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }

                                        });
                            }


                        } else {
                            Toast.makeText(RegisterAct.this, "Registration Error " + task.getException(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

}