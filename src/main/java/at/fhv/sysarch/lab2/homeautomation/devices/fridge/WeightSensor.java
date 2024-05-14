package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.Map;
import java.util.Optional;

public class WeightSensor extends AbstractBehavior<WeightSensor.WeightSensorCommand> {

    public interface WeightSensorCommand{}

    public static final class ConsumeProduct implements WeightSensorCommand {
        final Optional<Product> product;

        public ConsumeProduct(Optional<Product> product) {
            this.product = product;
        }
    }

    public static final class FillUpProduct implements WeightSensorCommand {
        final Optional<Map<Product, Integer>> products;

        public FillUpProduct(Optional<Map<Product, Integer>> products) {
            this.products = products;
        }
    }

    private final String deviceId;
    private final ActorRef<WeightMemory.WeightMemoryCommand> weightMemory;

    public WeightSensor(ActorContext<WeightSensorCommand> context, String deviceId, ActorRef<WeightMemory.WeightMemoryCommand> weightMemory) {
        super(context);
        this.deviceId = deviceId;
        this.weightMemory = weightMemory;
        getContext().getLog().info("WeightSensor is running");
    }

    public static Behavior<WeightSensorCommand> create(String deviceId, ActorRef<WeightMemory.WeightMemoryCommand> weightMemory) {
        return Behaviors.setup(context -> new WeightSensor(context, deviceId, weightMemory));
    }

    @Override
    public Receive<WeightSensorCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::consumeProduct)
                .onMessage(FillUpProduct.class, this::fillUpProduct)
                .build();
    }

    private Behavior<WeightSensorCommand> consumeProduct(ConsumeProduct c){
        getContext().getLog().debug("WeightSensor reading the consume {}", c.product.get().getName());
        weightMemory.tell(new WeightMemory.ConsumeProduct(Optional.of(c.product.get().getWeightInKg()), Optional.of("gramms")));

        return this;
    }

    private Behavior<WeightSensorCommand> fillUpProduct(FillUpProduct p){
        getContext().getLog().debug("Weight sensor reading the products {}", p.products.get().keySet());
        double sumWeight = 0;
        for (Product product : p.products.get().keySet()){
            sumWeight+= (product.getWeightInKg() * p.products.get().get(product));
        }
        double sumWeightRounded = Math.floor(sumWeight * 100) /100;
        weightMemory.tell(new WeightMemory.FillUpProduct(Optional.of(sumWeightRounded), Optional.of("gramms")));

        return this;
    }

}
