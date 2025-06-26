package com.technion.dormsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.technion.dormsapp.adapters.FaultRequestAdapter;
import com.technion.dormsapp.models.Request;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class FaultList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noRecordsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faultstatus); // Reusing layout with RecyclerView

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(v -> finish());

        noRecordsTextView = findViewById(R.id.noRecordsTextView);
        recyclerView = findViewById(R.id.requestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            fetchFaultRequests(userId);
        } else {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchFaultRequests(int userId) {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.url + "user-requests/" + userId + "/?request_type=fault_report";

        okhttp3.Request request = new Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(FaultList.this, "Failed to load fault requests", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d("FaultRequests", "JSON: " + json);

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Request>>() {}.getType();
                    List<Request> faultRequests = gson.fromJson(json, listType);

                    runOnUiThread(() -> {
                        if (faultRequests.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noRecordsTextView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordsTextView.setVisibility(View.GONE);
                            FaultRequestAdapter adapter = new FaultRequestAdapter(faultRequests);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(FaultList.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
