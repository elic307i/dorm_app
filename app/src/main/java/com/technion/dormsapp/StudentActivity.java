package com.technion.dormsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);  // Link to your layout

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // ← This goes back to the previous activity
            }
        });

        Button btnReportFault = findViewById(R.id.btnFaultReport);
        btnReportFault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ReportFault.class);
                startActivity(intent);
            }
        });

        Button btnBorrowRequest = findViewById(R.id.btnBorrow);
        btnBorrowRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, BorrowRequest.class);
                startActivity(intent);
            }
        });

        ImageButton btnContact = findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });

        Button btnBorrowList = findViewById(R.id.btnBorrowList);
        btnBorrowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, BorrowList.class);
                startActivity(intent);
            }
        });

        Button faultStatus = findViewById(R.id.faultStatus);
        faultStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, FaultList.class);
                startActivity(intent);
            }
        });

    }
}

