package com.example.thrid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextInputEditText firstname,lastname,password,flat,street,city,state,email,mobile,aadhar;
    Button register;
    TextView login;
    DatabaseReference users;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstname = (TextInputEditText)findViewById(R.id.firstname);
        lastname = (TextInputEditText)findViewById(R.id.lastname);
        password = (TextInputEditText)findViewById(R.id.password1);
        flat = (TextInputEditText)findViewById(R.id.flat);
        street = (TextInputEditText)findViewById(R.id.street);
        city = (TextInputEditText)findViewById(R.id.city);
        state = (TextInputEditText)findViewById(R.id.state);
        email = (TextInputEditText)findViewById(R.id.email);
        mobile = (TextInputEditText)findViewById(R.id.mobile);
        aadhar = (TextInputEditText)findViewById(R.id.aadhar);
        register = (Button)findViewById(R.id.register);
        login = (TextView)findViewById(R.id.newuser);

        users = FirebaseDatabase.getInstance().getReference("users");
        auth = FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstname.getText().toString().isEmpty() || lastname.getText().toString().isEmpty() || password.getText().toString().isEmpty() || flat.getText().toString().isEmpty() || street.getText().toString().isEmpty() || city.getText().toString().isEmpty() || state.getText().toString().isEmpty() || email.getText().toString().isEmpty() || mobile.getText().toString().isEmpty() || aadhar.getText().toString().isEmpty()){
                    Toast.makeText(Register.this,"Enter all Flieds",Toast.LENGTH_SHORT).show();
                }
                else {

                    adduser();
                    createuser();
                    firstname.setText("");
                    lastname.setText("");
                    password.setText("");
                    flat.setText("");
                    street.setText("");
                    city.setText("");
                    state.setText("");
                    email.setText("");
                    mobile.setText("");
                    aadhar.setText("");
                    Intent a = new Intent(Register.this, Trusted_Contacts.class);
                    startActivity(a);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(Register.this, Login.class);
                startActivity(b);
            }
        });
    }

    public void adduser(){

        String fn = firstname.getText().toString();
        String ln = lastname.getText().toString();
        String pw = password.getText().toString();
        String f = flat.getText().toString();
        String st = street.getText().toString();
        String c = city.getText().toString();
        String sta = state.getText().toString();
        String em = email.getText().toString();
        String mo = mobile.getText().toString();
        String aa = aadhar.getText().toString();



        String id = users.push().getKey();

        Users user = new Users(id,fn,ln,pw,f,st,c,sta,em,mo,aa);

        users.child(id).setValue(user);
    }

    public void createuser(){

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this,"Successful",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Register.this,"UnSuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
