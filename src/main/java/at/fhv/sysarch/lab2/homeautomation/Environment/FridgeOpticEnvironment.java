package at.fhv.sysarch.lab2.homeautomation.Environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FridgeOpticEnvironment extends AbstractBehavior<FridgeOpticEnvironment.FridgeOpticEnvironmentCommand> {

    public interface FridgeOpticEnvironmentCommand{}

    public static class PlaceProducts implements FridgeOpticEnvironmentCommand{
        final Optional<Map<Product, Integer>> products;

        public PlaceProducts(Optional<Map<Product, Integer>> products) {
            this.products = products;
        }
    }

    public static class ConsumeProducts implements FridgeOpticEnvironmentCommand{
        final Optional<Product> product;

        public ConsumeProducts(Optional<Product> product) {
            this.product = product;
        }
    }

    private Map<Product, Integer> products = new HashMap<>();
    private ActorRef<Fridge.FridgeCommand> fridge;


    public FridgeOpticEnvironment(ActorContext<FridgeOpticEnvironmentCommand> context, ActorRef<Fridge.FridgeCommand> fridge) {
        super(context);
        this.fridge = fridge;
        getContext().getLog().info("FridgeOpticEnvironment is running");
    }

    public static Behavior<FridgeOpticEnvironmentCommand> create(ActorRef<Fridge.FridgeCommand> fridge){
        return Behaviors.setup(context-> new FridgeOpticEnvironment(context, fridge));

    }

    @Override
    public Receive<FridgeOpticEnvironmentCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(PlaceProducts.class, this::placeProducts)
                .onMessage(ConsumeProducts.class, this::consumeProducts)
                .build();
    }

    private Behavior<FridgeOpticEnvironmentCommand> placeProducts(PlaceProducts p) {
        getContext().getLog().info("FridgeOpticEnvironment get place product command {}", p.products.get());
        for (Product product : p.products.get().keySet()){
            if (products.containsKey(product)){
                products.put(product, products.get(product) + p.products.get().get(product) );
            } else {
                products.put(product, p.products.get().get(product));
            }
        }
        fridge.tell(new Fridge.ReceiveProducts(p.products));

        return this;
    }

    private Behavior<FridgeOpticEnvironmentCommand> consumeProducts(ConsumeProducts p) {
        getContext().getLog().info("FridgeOpticEnvironment read consume {}", p.product.get().getName());
        if (products.get(p.product.get()) > 0){
            int amount = products.get(p.product.get()) - 1;
            products.put(p.product.get(), amount);
            fridge.tell(new Fridge.Consume(p.product));
        }
        return this;
    }
}
