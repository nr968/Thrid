package com.example.thrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Complaint extends AppCompatActivity {

    RadioButton yes,no;
    TextView date;
    EditText place,desc;
    Spinner spin;
    String type,datee,placeoo,description;
    DatabaseReference complaints;
    Button reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        spin = (Spinner) findViewById(R.id.spinner);
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        yes = (RadioButton) findViewById(R.id.yes);
        no = (RadioButton) findViewById(R.id.no);
        date = (TextView) findViewById(R.id.date);
        place = (EditText) findViewById(R.id.place);
        desc = (EditText) findViewById(R.id.desc);
        reg = (Button) findViewById(R.id.register);

        complaints = FirebaseDatabase.getInstance().getReference();

        //spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.crimes, android.R.layout.simple_spinner_item);

        spin.setAdapter(adapter);

        //calender
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange( CalendarView view, int year, int month, int dayOfMonth) {
                date.setText(dayOfMonth+"/"+month+"/"+year);
            }
        });

        type = spin.getSelectedItem().toString();
        datee = date.getText().toString();
        placeoo = place.getText().toString();
        description = desc.getText().toString();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(place.getText().toString().isEmpty()){
                    place.setError("Please specify the place of Occurance");
                }
                else if(desc.getText().toString().isEmpty()){
                    desc.setError("Pleace describe the incident");
                }
                else if(datee == "Select Date"){
                    Toast.makeText(Complaint.this,"Please select a Date",Toast.LENGTH_SHORT).show();
                }
                else {
                    reg_Complaint();
                    date.setText("Select Date");
                    place.setText("");
                    desc.setText("");
                }
            }
        });



    }

    public void reg_Complaint(){

        String typee = spin.getSelectedItem().toString();
        String dateee = date.getText().toString();
        String placeo = place.getText().toString();
        String descriptionn = desc.getText().toString();

        String id = complaints.push().getKey();

        Complaint_reg complaint = new Complaint_reg(id,typee,dateee,placeo,descriptionn);

        complaints.child("complaint_register").setValue(complaint);
    }
}
