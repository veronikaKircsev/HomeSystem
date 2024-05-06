package at.fhv.sysarch.lab2.homeautomation.helpClass;

import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.HashMap;

public class Order {

    private HashMap<Product, Integer> order = new HashMap<>();
    private static int orderCount = 0;

    public Order(){
        orderCount++;
    }

    public HashMap<Product, Integer> getOrder() {
        return order;
    }

    public void setOrder(HashMap<Product, Integer> order) {
        this.order = order;
    }
}
