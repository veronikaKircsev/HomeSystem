package at.fhv.sysarch.lab2.homeautomation.products;

public abstract class Product {

    protected double weightInKg;
    protected double price;
    protected String name;


    public double getWeightInKg() {
        return weightInKg;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
