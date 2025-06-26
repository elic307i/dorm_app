package com.technion.dormsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.technion.dormsapp.R;
import com.technion.dormsapp.models.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requestList;

    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, status, returnDate, rowNumber;

        public ViewHolder(View view) {
            super(view);
            rowNumber = view.findViewById(R.id.rowNumberTextView);
            itemName = view.findViewById(R.id.itemNameTextView);
            status = view.findViewById(R.id.statusTextView);
            returnDate = view.findViewById(R.id.returnDateTextView);
        }
    }

    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.rowNumber.setText(String.valueOf(position + 1));
        holder.itemName.setText(safe(request.getItemName()));
        holder.status.setText(safe(request.getStatus()));

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            holder.returnDate.setText(safe(request.getReturnDate()));
        } else {
            holder.returnDate.setText("");  // or "N/A", or hide the view
        }
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    // 🔐 Null-safe string fallback
    private String safe(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }
}
