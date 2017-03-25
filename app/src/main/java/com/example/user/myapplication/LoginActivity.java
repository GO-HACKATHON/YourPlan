package com.example.user.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button login_button;
    TextView register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView logo1 = (TextView) findViewById(R.id.logo_planner1);
//        TextView logo2 = (TextView) findViewById(R.id.logo_planner2);
//        Typeface logoFont = Typeface.createFromAsset(getAssets(), "fonts/QUIGLEYW.TTF");
//        logo1.setTypeface(logoFont);
//        logo2.setTypeface(logoFont);
        setContentView(R.layout.activity_login);


        login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ListPlanner.class);
                startActivity(intent);
            }
        });

        register_button = (TextView)findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

}
