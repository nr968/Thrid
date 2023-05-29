package com.example.thrid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Trusted_Contacts extends AppCompatActivity {

    TextInputEditText contact_name1,phone_number1,contact_name2,phone_number2,contact_name3,phone_number3;
    Button submit;
    String[] name;
    String[] number;
    DatabaseReference contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trusted_contacts);
        contact_name1 = (TextInputEditText) findViewById(R.id.contact_name1);
        phone_number1 = (TextInputEditText) findViewById(R.id.phone_number1);
        contact_name2 = (TextInputEditText) findViewById(R.id.contact_name2);
        phone_number2 = (TextInputEditText) findViewById(R.id.phone_number2);
        contact_name3 = (TextInputEditText) findViewById(R.id.contact_name3);
        phone_number3 = (TextInputEditText) findViewById(R.id.phone_number3);
        submit = (Button) findViewById(R.id.submit);
        name = new String[5];
        number = new String[5];

        contacts = FirebaseDatabase.getInstance().getReference("Contacts");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 101);

        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(contact_name1.getText().toString().isEmpty()){
                    contact_name1.setError("please enter 1st contact name");
                }
                else if(phone_number1.getText().toString().isEmpty()){
                    phone_number1.setError("please enter phone number");
                }
                else if(contact_name2.getText().toString().isEmpty()){
                    contact_name2.setError("please enter 2nd contact name");
                }
                else if(phone_number2.getText().toString().isEmpty()){
                    phone_number2.setError("please enter phone number");
                }
                else if(contact_name3.getText().toString().isEmpty()){
                    contact_name3.setError("please enter 3rd contact name");
                }
                else if(phone_number3.getText().toString().isEmpty()){
                    phone_number3.setError("please enter phone number");
                }
                else {

                    addcontacts();
                    //name[0] = contact_name1.getText().toString();
                    number[0] = phone_number1.getText().toString();
                    //name[1] = contact_name2.getText().toString();
                    number[1] = phone_number2.getText().toString();
                    //name[2] = contact_name3.getText().toString();
                    number[2] = phone_number3.getText().toString();
                    Intent a = new Intent(Trusted_Contacts.this, Sos.class);
                    //a.putExtra("name1", name[0]);
                    a.putExtra("number1", number[0]);
                   // a.putExtra("name2", name[1]);
                    a.putExtra("number2", number[1]);
                   // a.putExtra("name3", name[2]);
                    a.putExtra("number3", number[2]);
                    Toast.makeText(Trusted_Contacts.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(a);
                }
            }

        });
    }

    public void addcontacts(){

        String name1 = contact_name1.getText().toString();
        String name2 = contact_name2.getText().toString();
        String name3 = contact_name3.getText().toString();
        String number1 = phone_number1.getText().toString();
        String number2 = phone_number2.getText().toString();
        String number3 = phone_number3.getText().toString();

        String id = contacts.push().getKey();

        Contacts contact = new Contacts(id,name1,number1,name2,number2,name3,number3);

        contacts.child(id).setValue(contact);

    }
}
