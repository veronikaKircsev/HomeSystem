package at.fhv.sysarch.lab2.homeautomation.products;

import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Milk milk)) return false;
        return Double.compare(milk.getWeightInKg(), getWeightInKg()) == 0 && Double.compare(milk.getPrice(), getPrice()) == 0 && getName().equals(milk.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeightInKg(), getPrice(), getName());
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
