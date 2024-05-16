package at.fhv.sysarch.lab2.homeautomation.helpClass;

import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.HashMap;

public class Order {

    private HashMap<Product, Integer> order = new HashMap<>();
    private static int counter = 0;
    private int orderCount;

    public Order(){
        orderCount = ++counter;
    }

    public HashMap<Product, Integer> getOrder() {
        return order;
    }

    public void setOrder(HashMap<Product, Integer> order) {
        this.order = order;
    }

    public int getOrderCount() {
        return orderCount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderCount=" + orderCount +
                "order=" + order +
                '}';
    }
}
