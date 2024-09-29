package com.example.mechmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        EditText name=findViewById(R.id.name);
        EditText phNum=findViewById(R.id.phNum);
        EditText pwd=findViewById(R.id.pwd);
        EditText conpwd=findViewById(R.id.conpwd);
        Button register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.length() == 0 || phNum.length() == 0 || pwd.length() == 0 || conpwd.length() == 0) {
                    name.setError("Name is required");
                    phNum.setError("Phone number is required");
                    pwd.setError("Password is required");
                    conpwd.setError("Confirm password is required");
                } else if (!pwd.getText().toString().equals(conpwd.getText().toString())) {
                    conpwd.setError("Passwords do not match");
                } else {
                    String user = name.getText().toString();
                    String phone = phNum.getText().toString();
                    String password = pwd.getText().toString();

                    // Insert into database
                    UserData userData = new UserData(MainActivity2.this);
                    long result = userData.insertData(user, phone, password);

                    // Check the result
                    if (result != -1) {
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("Registration Successful")
                                .setMessage("Your registration is successful.")
                                .setPositiveButton("OK", ((dialog, which) ->{
                                    Intent intent=new Intent(MainActivity2.this,HomeActivity.class);

                                    if (phone != null && !phone.isEmpty()) {
                                        intent.putExtra("phone", phone);
                                    } else {
                                        // Show a message or handle the error
                                    }

                                    startActivity(intent);
                                } ))
                                .show();
                    } else {
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("Registration Not Successful")
                                .setMessage("Your registration is not successful. The phone number may already exist.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            }
        });

    }
}