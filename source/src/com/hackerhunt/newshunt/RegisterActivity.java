package com.hackerhunt.newshunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterActivity extends Activity {

    EditText name,email,password;
    Spinner age, gender;
    TextView register;

    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        getActionBar().setTitle("");
        
        age = (Spinner)findViewById(R.id.spinner_age);
        gender = (Spinner)findViewById(R.id.spinner_gender);

        signUp = (Button)findViewById(R.id.but_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categories = new Intent(RegisterActivity.this, Categories.class);
                startActivity(categories);
            }
        });


    }
}