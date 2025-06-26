package com.technion.dormsapp.models;

public class InventoryItem {
    private int id;
    private String item_name;
    private String item_name_en;
    public int getId() {
        return id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_name_en() {
        return item_name_en;
    }
    // Add setter if needed
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
}