package at.fhv.sysarch.lab2.homeautomation.products;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vegetables that)) return false;
        return Double.compare(that.getWeightInKg(), getWeightInKg()) == 0 && Double.compare(that.getPrice(), getPrice()) == 0 && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeightInKg(), getPrice(), getName());
    }
}
