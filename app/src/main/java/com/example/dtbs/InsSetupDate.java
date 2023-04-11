package com.example.dtbs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsSetupDate extends AppCompatActivity {

    public static String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_setup_date);

        CalendarView cv = (CalendarView)findViewById(R.id.calendarView);

        cv.setMinDate(System.currentTimeMillis() - 1000);

        Button btnNextSetTime = (Button) findViewById(R.id.insbutton_next);
        Button btnBackSetTime = (Button) findViewById(R.id.insbtn_back);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int days = c.get(Calendar.DAY_OF_WEEK);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);


        selectedDate = year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", day);

        switch (days) {
            case Calendar.SUNDAY:
                btnNextSetTime.setEnabled(false);
                break;

            case Calendar.SATURDAY:
                btnNextSetTime.setEnabled(false);
                break;
            default:
                btnNextSetTime.setEnabled(true);
                break;

        }


        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                String  curDate = String.valueOf(dayOfMonth);
                String  Year = String.valueOf(year);
                String  Month = String.valueOf(month);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int monthofyear = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                switch (dayOfWeek ) {
                    case Calendar.SUNDAY:
                        btnNextSetTime.setEnabled(false);
                        Toast.makeText(getApplicationContext(),"Sorry : weekend not allow" ,Toast.LENGTH_LONG).show();
                        break;

                    case Calendar.SATURDAY:
                        btnNextSetTime.setEnabled(false);
                        Toast.makeText(getApplicationContext(),"Sorry : weekend not allow" ,Toast.LENGTH_LONG).show();
                        break;
                    default:
                        btnNextSetTime.setEnabled(true);
                        break;

                }



                //selectedDate = String.format("%02d", day) + "-" + String.format("%02d", monthofyear+1)  + "-" +Year;
                selectedDate = Year + "-" + String.format("%02d", monthofyear+1) + "-" + String.format("%02d", day);


            }
        });

        btnNextSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InsSetupDate.this, InsSetupTime.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("selectedDate",selectedDate);
                startActivity(intent);

            }
        });

        btnBackSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                finish();

            }
        });

    }
}