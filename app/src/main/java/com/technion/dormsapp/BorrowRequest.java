package com.technion.dormsapp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.technion.dormsapp.models.InventoryItem;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import okhttp3.Callback;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

import android.widget.AdapterView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import com.technion.dormsapp.models.AvailableItem;

public class BorrowRequest extends AppCompatActivity {
    private boolean isSubmitting = false;
    private Button submitButton;
    Spinner itemSpinner;
    List<InventoryItem> inventoryList = new ArrayList<>();
    List<String> itemNames = new ArrayList<>();
    InventoryItem selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowrequest);

        EditText note = findViewById(R.id.note);

        submitButton = findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            if (isSubmitting) {
                Log.w("BorrowRequest", "Submission already in progress.");
                return;
            }

            if (selectedItem == null) {
                Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            int roomId = prefs.getInt("room_id", -1);
            if (roomId == -1) {
                Toast.makeText(this, "You must be assigned a room to submit a request.", Toast.LENGTH_LONG).show();
                return;
            }

            isSubmitting = true;
            submitButton.setEnabled(false);

            String returnDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            fetchAvailableItemId(selectedItem.getId(), new OnAvailableItemFetchedListener() {
                @Override
                public void onItemFetched(int itemId) {
                    submitBorrowRequest(itemId, returnDate, note.getText().toString());
                }

                @Override
                public void onFetchFailed() {
                    isSubmitting = false;
                    submitButton.setEnabled(true);
                    Toast.makeText(BorrowRequest.this, "No available item found for selected type.", Toast.LENGTH_LONG).show();
                }
            });
        });

        Button btnBack = findViewById(R.id.btnReturn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // ← This goes back to the previous activity
            }
        });
        itemNames.add("בחר פריט");
        itemSpinner = findViewById(R.id.items);
        fetchItemsFromBackend();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                itemNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedItem = null;  // First item is placeholder
                    return;
                }
                selectedItem = inventoryList.get(position - 1);  // adjust for "Select an item"
                Toast.makeText(BorrowRequest.this, "Selected: " + selectedItem.getItem_name(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItem = null;
            }
        });

    }

    private void submitBorrowRequest(int itemId, String returnDate, String note) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        int roomId = prefs.getInt("room_id", -1);

        if (userId == -1 || roomId == -1) {
            Log.e("BorrowRequest", "Missing user_id or room_id in SharedPreferences");
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("request_type", "equipment_rental")
                .add("item", String.valueOf(itemId))
                .add("user", String.valueOf(userId))
                .add("room", String.valueOf(roomId))
                .add("note", note)
                .add("return_date", returnDate)
                .build();

        Request request = new Request.Builder()
                .url(MainActivity.url + "submit-request/")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("BorrowRequest", "Network failure: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    Toast.makeText(BorrowRequest.this, "Network error. Please try again.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("BorrowRequest", "Submit response code: " + response.code());
                Log.d("BorrowRequest", "Response body: " + res);

                runOnUiThread(() -> {
                    isSubmitting = false;
                    submitButton.setEnabled(true);

                    if (response.isSuccessful()) {
                        Toast.makeText(BorrowRequest.this, "Request submitted successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        if (res.contains("You can only submit one equipment request per day")) {
                            Toast.makeText(BorrowRequest.this, "You have already submitted a request today.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(BorrowRequest.this, "Request failed. Try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public interface OnAvailableItemFetchedListener {
        void onItemFetched(int itemId);
        void onFetchFailed();
    }
    private void fetchAvailableItemId(int inventoryId, OnAvailableItemFetchedListener listener) {
        OkHttpClient client = new OkHttpClient();

        String url = MainActivity.url + "available-items/?inventory_id=" + inventoryId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("BorrowRequest", "Failed to fetch available items", e);
                runOnUiThread(() -> {
                    Toast.makeText(BorrowRequest.this, "Network error while checking availability", Toast.LENGTH_LONG).show();
                    listener.onFetchFailed();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("BorrowRequest", "Response not successful: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(BorrowRequest.this, "Server error", Toast.LENGTH_LONG).show();
                        listener.onFetchFailed();
                    });
                    return;
                }

                String json = response.body().string();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<AvailableItem>>() {}.getType();
                List<AvailableItem> availableItems = gson.fromJson(json, listType);

                if (availableItems.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(BorrowRequest.this, "No available item found for this type", Toast.LENGTH_LONG).show();
                        listener.onFetchFailed();
                    });
                } else {
                    int itemId = availableItems.get(0).getId();  // Use first available
                    listener.onItemFetched(itemId);
                }
            }
        });
    }


    private void fetchItemsFromBackend() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MainActivity.url + "inventory-items/")  // your backend
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(BorrowRequest.this, "Failed to load items", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d("BorrowRequest", "Response JSON: " + json);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<InventoryItem>>() {}.getType();
                    inventoryList = gson.fromJson(json, listType);

                    runOnUiThread(() -> {
                        itemNames.clear();
                        itemNames.add("בחר פריט"); // <--- important
                        for (InventoryItem item : inventoryList) {
                            itemNames.add(item.getItem_name());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                BorrowRequest.this,
                                android.R.layout.simple_spinner_item,
                                itemNames
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        itemSpinner.setAdapter(adapter);

                        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    selectedItem = null; // Nothing selected
                                } else {
                                    selectedItem = inventoryList.get(position - 1); // adjust for "Select an item"
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedItem = null;
                            }
                        });
                    });
                }
            }
        });
    }
}
