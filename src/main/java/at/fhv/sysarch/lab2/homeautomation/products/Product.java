package at.fhv.sysarch.lab2.homeautomation.products;

public abstract class Product {

    protected double weight;
    protected double price;
    protected String name;


    public double getWeight() {
        return weight;
    }


    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
