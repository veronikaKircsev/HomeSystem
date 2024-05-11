package at.fhv.sysarch.lab2.homeautomation.products;

public class Cheese extends Product{
    private double weightInKg = 0.6;
    private double price  = 5.3;
    private String name = "cheese";

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
        return "Cheese{" +
                "weightInKg=" + weightInKg +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
