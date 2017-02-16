package com.teksystems.qe.automata.sample.amazon.data;

import java.util.ArrayList;
import java.util.List;

public class AmazonUser {
    private List<Order> orders   = new ArrayList<Order>();
    
    public AmazonUser(){}

    public List<Order> getOrders() {
        return orders;
    }
    
    public void addItemToBuy(Item item){
        Order order = new Order();
        order.getItems().add(item);
        orders.add(order);
    }
    
    public Order getCurrentUnfinishedOrder(){
        for(Order each : orders){
            if(each.getStatus().compareTo(OrderStatus.PAID)<1){
                return each;
            }
        }
        return null;
    }
}
