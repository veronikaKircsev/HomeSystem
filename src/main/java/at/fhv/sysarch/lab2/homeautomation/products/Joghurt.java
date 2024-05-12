package at.fhv.sysarch.lab2.homeautomation.products;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Joghurt joghurt)) return false;
        return Double.compare(joghurt.getWeightInKg(), getWeightInKg()) == 0 && Double.compare(joghurt.getPrice(), getPrice()) == 0 && getName().equals(joghurt.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeightInKg(), getPrice(), getName());
    }
}
