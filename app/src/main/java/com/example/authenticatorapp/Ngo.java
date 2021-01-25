package com.example.authenticatorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.authenticatorapp.NGOs.Fi;
import com.example.authenticatorapp.NGOs.Rb;
import com.example.authenticatorapp.NGOs.Trha;

public class Ngo extends AppCompatActivity {
    Button n1,n2,n3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo);
        n1 = findViewById(R.id.trha);
        n2 = findViewById(R.id.fi);
        n3 = findViewById(R.id.rb);
        n1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Trha.class));
            }
        });
        n2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Fi.class));
            }
        });
        n3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Rb.class));
            }
        });
    }
}
