package com.technion.dormsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class ReportFault extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportfault);  // Link to your layout

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // ← This goes back to the previous activity
            }
        });

        Spinner spinner = findViewById(R.id.itemType);
        String[] items = {"בחר סוג תקלה", "דלתות ומנעולים", "איטום", "חשמל", "שרברבות", "צבע", "עובש", "חימום", "מזיקים", "חלונות", "חדירת מים", "אחר"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
