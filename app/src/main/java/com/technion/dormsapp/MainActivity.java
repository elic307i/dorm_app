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
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button studentButton;
    Button BuildingManButton;
    Button officeManButton;
    EditText username;
    EditText password;

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
        username = findViewById(R.id.UserName);
        password = findViewById(R.id.password);


        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_str = username.getText().toString();
                String password_str = password.getText().toString();
                authenticate(user_str, password_str);
                //Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                //startActivity(intent);
            }
        });
    }

    private void authenticate(String username, String password) {
        Log.d("MainActivity.auth", "username: " + username);
        Log.d("MainActivity.auth", "username: " + password);
        OkHttpClient client = new OkHttpClient();
        String sql = "SELECT * FROM \"DormitoriesPlus_user\" WHERE username = '" + username + "' And password = '" + password + "'";
        Log.d("MainActivity.auth", "sql: " + sql);
        HttpUrl url = HttpUrl.parse("http://10.0.2.2:8000/api/users/")
                .newBuilder()
                .addQueryParameter("q", sql)
                .build();

        Log.d("MainActivity.auth", "Sending request to: " + url.toString());
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity.auth", "Network error", e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("MainActivity.auth", "got response");
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    // Parse JSON using Gson or org.json
                    Log.d("MainActivity.auth", json);
                    if(json.equals("[]"))
                        Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                    else
                        Log.e("MainActivity.auth", "Got good json response");
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity.auth", "Request failed. Code: " + response.code());
                    Log.e("MainActivity.auth", "Body: " + response.body().string());
                }
            }

        });
    }
}