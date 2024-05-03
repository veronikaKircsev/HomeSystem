package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.helpClass.Order;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.List;

public class ProductListMemory extends AbstractBehavior<ProductListMemory.ProductProcessCommand> {


    public interface ProductProcessCommand{}

    public ProductListMemory(ActorContext<ProductProcessCommand> context) {
        super(context);
    }

    public static Behavior<ProductListMemory.ProductProcessCommand> create() {
        return Behaviors.setup(context -> new ProductListMemory(context));
    }

    @Override
    public Receive<ProductProcessCommand> createReceive() {
        return null;
    }

    private List<Product> productList;
    private List<Order> orderList;

    //TODO:initialise the products

    public void addProduct(Product product) {
        productList.add(product);
    }

    public Product getProduct(String name) {
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                Product product1 = product;
                productList.remove(product);
                return product1;
            }
        }
        return null;
    }

    public void addOrder(Order order) {
        orderList.add(order);
    }

    public List<Order> getOrderHistory() {
        List<Order> orderList1 = orderList;
        return orderList1;
    }
}
