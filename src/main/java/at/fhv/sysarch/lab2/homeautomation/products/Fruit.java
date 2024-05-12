package at.fhv.sysarch.lab2.homeautomation.products;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fruit fruit)) return false;
        return Double.compare(fruit.getWeightInKg(), getWeightInKg()) == 0 && Double.compare(fruit.getPrice(), getPrice()) == 0 && getName().equals(fruit.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeightInKg(), getPrice(), getName());
    }
}
