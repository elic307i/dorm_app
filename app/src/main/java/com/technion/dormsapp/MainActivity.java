package com.technion.dormsapp;

import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String url = "https://elichome.tplinkdns.com/api/";
    Button studentButton;
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

        studentButton.setOnClickListener(v -> {
            String user_str = username.getText().toString();
            String password_str = password.getText().toString();
            authenticate(user_str, password_str);
        });
    }

    private OkHttpClient getSecureOkHttpClient(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.rootca);
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            X509TrustManager trustManager = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    trustManager = (X509TrustManager) tm;
                    break;
                }
            }
            if (trustManager == null) {
                throw new IllegalStateException("No X509TrustManager found");
            }

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to set up secure OkHttpClient", e);
        }
    }

    private void authenticate(String username, String password) {
        Log.d("MainActivity.auth", "username: " + username);
        Log.d("MainActivity.auth", "password: " + password);

        OkHttpClient client = getSecureOkHttpClient(this);

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        String tempurl = url + "login/";
        Log.d("MainActivity.auth", "Url: " + tempurl);

        Request request = new Request.Builder()
                .url(tempurl)
                .post(body)
                .build();

        Log.d("MainActivity.auth", "Sending request...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Log.e("MainActivity.auth", "Network error", e);
                    Toast.makeText(MainActivity.this, "Cannot access server", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("MainActivity.auth", "Response: " + json);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                Log.d("MainActivity.auth", "Login successful");
                                int userId = jsonObject.getInt("user_id");
                                int roomId = jsonObject.optInt("room_id", -1);
                                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("user_id", userId);
                                editor.putInt("room_id", roomId);
                                editor.apply();
                                Log.d("MainActivity.auth", "Saved to SharedPreferences - user_id: " + userId + ", room_id: " + roomId);
                                Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = jsonObject.optString("error", "Invalid credentials");
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Invalid server response", Toast.LENGTH_LONG).show();
                            Log.e("MainActivity.auth", "JSON parse error", e);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login failed. Invalid credentials", Toast.LENGTH_LONG).show();
                        Log.e("MainActivity.auth", "Request failed. Code: " + response.code());
                    }
                });
            }
        });
    }
}