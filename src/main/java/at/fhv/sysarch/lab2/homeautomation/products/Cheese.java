package at.fhv.sysarch.lab2.homeautomation.products;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cheese cheese)) return false;
        return Double.compare(cheese.getWeightInKg(), getWeightInKg()) == 0 && Double.compare(cheese.getPrice(), getPrice()) == 0 && Objects.equals(getName(), cheese.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeightInKg(), getPrice(), getName());
    }
}
