package at.fhv.sysarch.lab2.homeautomation.helpClass;

public class ProductReceipt {

    private String name;
    private int amount;
    private double price;
    private double totalPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void calculatePrise(){
        setTotalPrice(amount*price);
    }

    @Override
    public String toString() {
        return "name: " + name +
                ", amount: " + amount +
                ", price: " + price +
                ", totalPrice: " + totalPrice + "\n";
    }
}
