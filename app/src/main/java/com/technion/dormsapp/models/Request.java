package com.technion.dormsapp.models;

import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("request_type")
    private String requestType;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("status")
    private String status;

    @SerializedName("return_date")
    private String returnDate;

    @SerializedName("fault_description")
    private String faultDescription;

    @SerializedName("fault_type")
    private String faultType;

    @SerializedName("urgency")
    private String urgency;

    @SerializedName("created_at")
    private String createdAt;
    public String getRequestType() {
        return requestType;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getItemName() {
        return itemName;
    }

    public String getStatus() {
        return status;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public String getFaultType() {
        return faultType;
    }

    public String getUrgency() {
        return urgency;
    }
}
