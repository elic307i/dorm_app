package com.technion.dormsapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BUILDINGS";
    private static final String URL = "http://10.0.2.2:8000/api/buildings/";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    Button studentButton;
    Button BuildingManButton;
    Button officeManButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentButton = findViewById(R.id.btnStudent);


        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (false) { // TBD should be true
                    Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                    startActivity(intent);
                } else {
                    fetchBuildings();
                }
            }
        });


    }
    private void fetchBuildings() {
        Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch buildings: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected response: " + response);
                    return;
                }

                String json = response.body().string();
                Type listType = new TypeToken<List<Building>>() {}.getType();
                List<Building> buildings = gson.fromJson(json, listType);

                for (Building b : buildings) {
                    Log.d(TAG, "Building: " + b.building_name);
                }

                // Optionally update UI — run on UI thread if needed
                // runOnUiThread(() -> ... );
            }
        });
    }
}