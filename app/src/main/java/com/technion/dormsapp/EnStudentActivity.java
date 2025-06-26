package com.technion.dormsapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EnStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enstudent);  // Link to your layout

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear(); // Clear all stored values
                editor.apply();
                Intent intent = new Intent(EnStudentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();
            }
        });

        Button btnReportFault = findViewById(R.id.btnFaultReport);
        btnReportFault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnStudentActivity.this, EnReportFault.class);
                startActivity(intent);
            }
        });

        Button btnBorrowRequest = findViewById(R.id.btnBorrow);
        btnBorrowRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnStudentActivity.this, EnBorrowRequest.class);
                startActivity(intent);
            }
        });

        Button btnEnglish = findViewById(R.id.hebrew);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnStudentActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        });

        CardView contactCard = findViewById(R.id.contactCard);
        contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnStudentActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });

        Button btnBorrowList = findViewById(R.id.btnBorrowList);
        btnBorrowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnStudentActivity.this, EnBorrowList.class);
                startActivity(intent);
            }
        });

        Button faultStatus = findViewById(R.id.faultStatus);
        faultStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnStudentActivity.this, EnFaultList.class);
                startActivity(intent);
            }
        });

    }
}

