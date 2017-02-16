package com.teksystems.qe.automata.sample.amazon.data;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Item> items        = new ArrayList<Item>();
    private OrderStatus status      = OrderStatus.INIT;
    
    public Order(){}

    public List<Item> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public Item getCurrentItemBelowStatus(ItemStatus state){
        for(Item each : items){
            if(each.getStatus().compareTo(state)<1){
                return each;
            }
        }
        return null;
    }
}
