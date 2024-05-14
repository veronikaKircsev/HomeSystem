package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.*;

public class ProductListMemory extends AbstractBehavior<ProductListMemory.ProductProcessCommand> {


    public interface ProductProcessCommand{}

    public static final class ConsumeProduct implements ProductProcessCommand {
        final Optional<Product> product;
        final Optional<Integer> number;

        public ConsumeProduct(Optional<Product> product, Optional<Integer> number) {
            this.product = product;
            this.number = number;
        }
    }

    public static final class FillUpProduct implements ProductProcessCommand {
        final Optional<Map<Product,Integer>> products;

        public FillUpProduct(Optional<Map<Product,Integer>> products) {
            this.products = products;
        }
    }

    public static class RequestProducts implements ProductProcessCommand {
        public final ActorRef<Response> replyTo;

        public RequestProducts(ActorRef<Response> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class Response {
        public final String result;

        public Response(String result) {
            this.result = result;
        }
    }

    private Map<Product, Integer> productList = new HashMap<>();
    private Set<Product> missing = new HashSet<>();
    private ActorRef<OrderProcessManager.OrderCommand> order;

    public ProductListMemory(ActorContext<ProductProcessCommand> context, ActorRef<OrderProcessManager.OrderCommand> order) {
        super(context);
        this.order = order;
        getContext().getLog().info("ProductListMemory is running");
    }

    public static Behavior<ProductListMemory.ProductProcessCommand> create(ActorRef<OrderProcessManager.OrderCommand> order) {
        return Behaviors.setup(context -> new ProductListMemory(context, order));
    }

    @Override
    public Receive<ProductProcessCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::consumeProduct)
                .onMessage(FillUpProduct.class, this::fillUpProduct)
                .onMessage(RequestProducts.class, this::onRequest)
                .build();
    }




    private Behavior<ProductProcessCommand> consumeProduct(ConsumeProduct c) {
        getContext().getLog().debug("ProductList reading the consume{} and value {}", c.product.get()
                , c.number.get());
        int amount;
        if (productList.get(c.product.get())!= 0) {
            getContext().getLog().debug("Product will be taken {}", c.product.get().getName());
            amount = productList.get(c.product.get());
            amount -= c.number.get();
            if (amount == 0) {
                Map<Product, Integer> o = new HashMap<>();
                missing.add(c.product.get());
                for (Product p : missing){
                        o.put(p, 2);
                }
                getContext().getLog().debug("Order started {} ", c.product.get().getName());
                order.tell(new OrderProcessManager.StartOrder(Optional.of(o)));
                }
            productList.put(c.product.get(), amount);
        } else {
            getContext().getLog().debug("Fridge don't have enough {}" + c.product.get() );
        }
        return this;
    }

    private Behavior<ProductProcessCommand> fillUpProduct(FillUpProduct f) {
        getContext().getLog().debug("ProductList reading the fillUp {}", f.products.get());
        for (Product product: f.products.get().keySet()){
            if (missing.contains(product)){
                missing.remove(product);
            }
            if (productList.containsKey(product)){
                int amount = productList.get(product) + f.products.get().get(product);
                productList.put(product, amount);
            } else {
                productList.put(product, f.products.get().get(product));
            }
        }
        return this;
    }


    private Behavior<ProductProcessCommand> onRequest(RequestProducts request) {
        getContext().getLog().debug("ProductListMemory reads request");
        StringBuilder sb = new StringBuilder();
        for (Product product : productList.keySet()){
            sb.append(product.getName()).append(" ").append(productList.get(product)).append("\n");
        }
        request.replyTo.tell(new Response(sb.toString()));
        return Behaviors.same();
    }


}
