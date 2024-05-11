package at.fhv.sysarch.lab2.homeautomation.products;

import at.fhv.sysarch.lab2.homeautomation.products.Product;

public class Milk extends Product {

    private double weightInKg = 1.1;
    private double price  = 1.1;
    private String name = "milk";

    @Override
    public double getWeightInKg() {
        return weightInKg;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Milk{" +
                "weightInKg=" + weightInKg +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
