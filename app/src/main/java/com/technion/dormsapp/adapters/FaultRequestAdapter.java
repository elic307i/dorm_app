package com.technion.dormsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.technion.dormsapp.R;
import com.technion.dormsapp.models.Request;

import java.util.List;

public class FaultRequestAdapter extends RecyclerView.Adapter<FaultRequestAdapter.ViewHolder> {

    private List<Request> faultRequestList;

    public FaultRequestAdapter(List<Request> faultRequestList) {
        this.faultRequestList = faultRequestList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rowNumber, faultType, status, faultDate;

        public ViewHolder(View view) {
            super(view);
            rowNumber = view.findViewById(R.id.rowNumberTextView);
            faultType = view.findViewById(R.id.faultTypeTextView);
            status = view.findViewById(R.id.statusTextView);
            faultDate = view.findViewById(R.id.openDateTextView);
        }
    }

    @Override
    public FaultRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fault_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = faultRequestList.get(position);

        holder.rowNumber.setText(String.valueOf(position + 1));
        holder.faultType.setText(safe(request.getFaultType()));
        holder.status.setText(safe(request.getStatus()));
        holder.faultDate.setText(safe(request.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return faultRequestList != null ? faultRequestList.size() : 0;
    }

    private String safe(String val) {
        return (val == null || val.trim().isEmpty()) ? "—" : val;
    }
}
