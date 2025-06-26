package com.technion.dormsapp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.technion.dormsapp.adapters.RequestAdapter;

import android.content.SharedPreferences;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.List;
import okhttp3.OkHttpClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.technion.dormsapp.models.Request;

import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.lang.reflect.Type;

public class BorrowList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noRecordsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowlist);  // Link to your layout

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // ← This goes back to the previous activity
            }
        });

        noRecordsTextView = findViewById(R.id.noRecordsTextView);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);  // -1 is default if not found
        recyclerView = findViewById(R.id.requestRecyclerView);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        fetchBorrowedRequests(userId);

    }

    private void fetchBorrowedRequests(int userId) {
        OkHttpClient client = new OkHttpClient();

        String url = MainActivity.url + "user-requests/" + userId + "/?request_type=equipment_rental";

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(BorrowList.this, "Failed to load borrowed requests", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d("BorrowedItems", "JSON: " + json);

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Request>>() {}.getType();
                    List<Request> borrowedRequests = gson.fromJson(json, listType);

                    runOnUiThread(() -> {
                        if (borrowedRequests.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noRecordsTextView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordsTextView.setVisibility(View.GONE);
                            RequestAdapter adapter = new RequestAdapter(borrowedRequests);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(BorrowList.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

}

