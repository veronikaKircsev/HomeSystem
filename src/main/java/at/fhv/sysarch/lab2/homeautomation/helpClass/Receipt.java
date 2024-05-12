package at.fhv.sysarch.lab2.homeautomation.helpClass;

import java.util.ArrayList;
import java.util.List;

public class Receipt {

    private static int receiptNumber = 0;
    private List<ProductReceipt> products = new ArrayList<ProductReceipt>();
    private double totalPrice;
    private int totalAmount;

    public Receipt() {
        receiptNumber++;
    }

    public List<ProductReceipt> getProducts() {
        return products;
    }

    public void setProducts(List<ProductReceipt> products) {
        this.products = products;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void addProduct(ProductReceipt product){
        products.add(product);
    }

    public void calculateTotal(){
        for (ProductReceipt product : products){
            totalPrice += product.getTotalPrice();
            totalAmount += product.getAmount();
        }
    }

    @Override
    public String toString() {
        return "Receipt Number: "+ receiptNumber + "\n" +
                "products: " + products +
                ", totalPrice: " + totalPrice +
                ", totalWeight: " + totalAmount;
    }
}
