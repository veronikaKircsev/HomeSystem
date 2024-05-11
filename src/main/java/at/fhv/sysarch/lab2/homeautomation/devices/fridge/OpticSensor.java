package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpticSensor extends AbstractBehavior<OpticSensor.OpticSensorCommand> {


    public interface OpticSensorCommand{}

    public static final class ConsumeProduct implements OpticSensorCommand {
        final Optional<Product> product;

        public ConsumeProduct(Optional<Product> product) {
            this.product = product;
        }
    }

    public static final class FillUpProduct implements OpticSensorCommand {
        final Optional<Map<Product, Integer>> products;

        public FillUpProduct(Optional<Map<Product, Integer>> products) {
            this.products = products;
        }
    }

    private final String deviceId;
    private ActorRef<ProductListMemory.ProductProcessCommand> productMemory;

    public OpticSensor(ActorContext<OpticSensorCommand> context, String deviceId, ActorRef<ProductListMemory.ProductProcessCommand> productMemory) {
        super(context);
        this.deviceId = deviceId;
        this.productMemory = productMemory;
    }

    public static Behavior<OpticSensorCommand> create(String deviceId, ActorRef<ProductListMemory.ProductProcessCommand> productMemory) {
        return Behaviors.setup(context -> new OpticSensor(context, deviceId, productMemory));
    }

    @Override
    public Receive<OpticSensorCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::consumeProduct)
                .onMessage(FillUpProduct.class, this::fillUpProduct)
                .build();
    }

    private Behavior<OpticSensorCommand> consumeProduct(ConsumeProduct c){
        getContext().getLog().info("OpticSensor reading the consume {}", c.product.get());
        productMemory.tell(new ProductListMemory.ConsumeProduct(Optional.of(c.product.get()), Optional.of(1)));
        return this;
    }

    private Behavior<OpticSensorCommand> fillUpProduct(FillUpProduct p){
            getContext().getLog().info("OpticSensor reading the new product {}", p.products.get());
            productMemory.tell(new ProductListMemory.FillUpProduct(p.products));

        return this;
    }

}
