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

public class SpaceSensor extends AbstractBehavior<SpaceSensor.SpaceSensorCommand> {

    public interface SpaceSensorCommand{}

    public static final class ConsumeProduct implements SpaceSensorCommand{
        final Optional<Product> product;

        public ConsumeProduct(Optional<Product> product) {
            this.product = product;
        }
    }

    public static final class FillUpProduct implements SpaceSensorCommand{
        final Optional<Map<Product, Integer>> products;

        public FillUpProduct(Optional<Map<Product, Integer>> products) {
            this.products = products;
        }
    }

    public SpaceSensor(ActorContext<SpaceSensorCommand> context, String deviceId, ActorRef<SpaceMemory.SpaceMemoryCommand> spaceMemory) {
        super(context);
        this.deviceId = deviceId;
        this.spaceMemory = spaceMemory;
    }

    public static Behavior<SpaceSensorCommand> create(String deviceId, ActorRef<SpaceMemory.SpaceMemoryCommand> spaceMemory) {
        return Behaviors.setup(context -> new SpaceSensor(context, deviceId, spaceMemory));
    }

    private final String deviceId;
    private final ActorRef<SpaceMemory.SpaceMemoryCommand> spaceMemory;

    @Override
    public Receive<SpaceSensorCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::consumeProduct)
                .onMessage(FillUpProduct.class, this::fillUpProduct)
                .build();
    }

    private Behavior<SpaceSensorCommand> consumeProduct(ConsumeProduct c){
        getContext().getLog().info("SpaceSensor reading the consume {}", c.product.get().getName());
        spaceMemory.tell(new SpaceMemory.ConsumeProduct(Optional.of(1)));
        return this;
    }

    private Behavior<SpaceSensorCommand> fillUpProduct(FillUpProduct p){
            getContext().getLog().info("SpaceSensor reading the new products {}", p.products.get().toString());
        int sum = 0;
        for (Integer i : p.products.get().values()){
            sum += i;
        }
        spaceMemory.tell(new SpaceMemory.FillUpProduct(Optional.of(sum)));

        return this;
    }

}
