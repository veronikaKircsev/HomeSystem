package at.fhv.sysarch.lab2.homeautomation.products;


public class Butter extends Product {

    private double weightInKg = 0.3;
    private double price  = 2.1;
    private String name = "butter";

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
        return "Butter{" +
                "weightInKg=" + weightInKg +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
