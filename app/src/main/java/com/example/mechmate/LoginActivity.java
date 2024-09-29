package com.example.mechmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        EditText phNum=findViewById(R.id.phNum);
        EditText pwd=findViewById(R.id.pwd);
        Button btn=findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phNum.getText().toString();
                String password = pwd.getText().toString();
                if (phNum.length() == 0 || pwd.length() == 0) {
                    phNum.setError("Name is required");
                    pwd.setError("Password is required");
                } else {
                    UserData userData = new UserData(LoginActivity.this);
                    boolean loginDetailsExist = userData.checkLoginDetails(phone, password);
                    if (loginDetailsExist) {

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Login Successful")
                                .setMessage("Welcome Back")
                                .setPositiveButton("OK",((dialog, which) ->{
                                    Intent intent=new Intent(LoginActivity.this,HomeActivity.class);

                                    if (phone != null && !phone.isEmpty()) {
                                        intent.putExtra("phone", phone);
                                    } else {
                                        // Show a message or handle the error
                                    }

                                    startActivity(intent);
                                    finish();
                                } ))
                                .show();
                    } else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Data not found")
                                .setMessage("Your data is not found.")
                                .setPositiveButton("OK",null)
                                .show();
                    }
                }
            }
        });

    }
}