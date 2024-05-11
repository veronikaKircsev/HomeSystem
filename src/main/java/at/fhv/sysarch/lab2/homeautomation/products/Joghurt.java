package at.fhv.sysarch.lab2.homeautomation.products;

public class Joghurt extends Product{
    private double weightInKg = 0.8;
    private double price  = 1.6;
    private String name = "joghurt";

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
        return "Joghurt{" +
                "weightInKg=" + weightInKg +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
