package com.example.prm392.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.prm392.R;

public class ResultActivity extends AppCompatActivity {

    TextView txtPaymentNotification;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtPaymentNotification = findViewById(R.id.txtPaymentNotification);

        Intent intent = new Intent();
        String result = intent.getStringExtra("result");

        if(result != null && !result.isBlank()) {
            txtPaymentNotification.setText(intent.getStringExtra("result"));
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
