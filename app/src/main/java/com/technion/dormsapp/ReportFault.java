package com.technion.dormsapp;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import okhttp3.FormBody;

public class ReportFault extends AppCompatActivity {
    private boolean isSubmitting = false;
    private Button submitButton;
    private Spinner faultTypeSpinner;
    private EditText faultDescriptionEditText;
    private String selectedFaultType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportfault);

        faultDescriptionEditText = findViewById(R.id.faultDescription);
        submitButton = findViewById(R.id.btnSubmit);

        faultTypeSpinner = findViewById(R.id.itemType); // Must match XML spinner id
        String[] faultTypes = {
                "בחר סוג תקלה", "דלתות ומנעולים", "איטום", "חשמל", "שרברבות",
                "צבע", "עובש", "חימום", "מזיקים", "חלונות", "חדירת מים", "אחר"
        };

        Spinner urgencySpinner = findViewById(R.id.urgencySpinner);
        String[] urgencyLevels = {"בחר דחיפות", "נמוכה", "בינונית", "גבוהה"};
        ArrayAdapter<String> urgencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, urgencyLevels);
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urgencySpinner.setAdapter(urgencyAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, faultTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faultTypeSpinner.setAdapter(adapter);

        faultTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFaultType = position == 0 ? null : faultTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFaultType = null;
            }
        });

        submitButton.setOnClickListener(v -> {
            if (isSubmitting) {
                Log.w("FaultReport", "Submission already in progress.");
                return;
            }

            if (selectedFaultType == null || selectedFaultType.isEmpty()) {
                Toast.makeText(this, "Please select a fault type", Toast.LENGTH_SHORT).show();
                return;
            }

            if (faultDescriptionEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please describe the fault", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            int roomId = prefs.getInt("room_id", -1);
            if (roomId == -1) {
                Toast.makeText(this, "You must be assigned a room to report a fault.", Toast.LENGTH_LONG).show();
                return;
            }

            isSubmitting = true;
            submitButton.setEnabled(false);
            String selectedUrgency = urgencySpinner.getSelectedItem().toString();
            if (selectedUrgency.equals("בחר דחיפות")) {
                Toast.makeText(this, "Please select urgency level", Toast.LENGTH_SHORT).show();
                submitButton.setEnabled(true);
                return;
            }

            submitFaultReport(faultDescriptionEditText.getText().toString(), selectedUrgency, selectedFaultType);

        });

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // ← This goes back to the previous activity
            }
        });
    }

    private void submitFaultReport(String description, String urgency, String faultType) {
        try {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            int roomId = prefs.getInt("room_id", -1);

            if (userId == -1 || roomId == -1) {
                Log.e("FaultReport", "Missing user_id or room_id in SharedPreferences");
                Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_LONG).show();
                submitButton.setEnabled(true);
                return;
            }

            Log.d("FaultReport", "Preparing to submit fault: user=" + userId + ", room=" + roomId +
                    ", type=" + faultType + ", urgency=" + urgency + ", desc=" + description);

            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("request_type", "fault_report")
                    .add("user", String.valueOf(userId))
                    .add("room", String.valueOf(roomId))
                    .add("fault_type", faultType)
                    .add("fault_description", description)
                    .add("urgency", urgency)
                    .build();

            Request request = new Request.Builder()
                    .url(MainActivity.url + "submit-request/")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FaultReport", "Network failure: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        submitButton.setEnabled(true);
                        Toast.makeText(ReportFault.this, "Network error. Please try again.", Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    Log.d("FaultReport", "Submit response code: " + response.code());
                    Log.d("FaultReport", "Response body: " + res);

                    runOnUiThread(() -> {
                        submitButton.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(ReportFault.this, "Fault report submitted successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            if (res.contains("one fault report per day")) {
                                Toast.makeText(ReportFault.this, "You have already submitted a fault report today.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ReportFault.this, "Failed to submit fault report.", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                }
            });

        } catch (Exception e) {
            Log.e("FaultReport", "Exception during submission", e);
            runOnUiThread(() -> {
                submitButton.setEnabled(true);
                Toast.makeText(ReportFault.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }

}
