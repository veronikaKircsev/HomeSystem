package at.fhv.sysarch.lab2.homeautomation.products;

public class Vegetables extends Product{

    private double weightInKg = 0.4;
    private double price  = 0.9;
    private String name = "vegetable";

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
        return "Vegetables{" +
                "weightInKg=" + weightInKg +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
