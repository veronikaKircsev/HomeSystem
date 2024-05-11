package at.fhv.sysarch.lab2.homeautomation.products;

public class Fruit extends Product {
    private double weightInKg = 0.3;
    private double price  = 0.8;
    private String name = "fruit";

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
        return "Fruit{" +
                "weightInKg=" + weightInKg +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
