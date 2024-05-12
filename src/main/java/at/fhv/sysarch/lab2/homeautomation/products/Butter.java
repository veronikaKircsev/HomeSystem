package at.fhv.sysarch.lab2.homeautomation.products;


import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Butter butter)) return false;
        return Double.compare(butter.getWeightInKg(), getWeightInKg()) == 0 && Double.compare(butter.getPrice(), getPrice()) == 0 && getName().equals(butter.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeightInKg(), getPrice(), getName());
    }
}
