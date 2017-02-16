package com.teksystems.qe.automata.sample.amazon.data;

public class Item {
    private ItemStatus status   = ItemStatus.INIT;
    private int quantity        = 1;
    private String itemName     = null;
    private int itemThreshold   = 90;
    
    public Item(){}

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getSearchThreshold() {
        return itemThreshold;
    }

    public void setSearchThreshold(int itemThreshold) {
        this.itemThreshold = itemThreshold;
    }

}
