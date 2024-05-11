package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class WeightMemory extends AbstractBehavior<WeightMemory.WeightMemoryCommand> {


    public interface WeightMemoryCommand {}

    public static final class ConsumeProduct implements WeightMemoryCommand {
        final Optional<Double> value;
        final Optional<String> units;

        public ConsumeProduct(Optional<Double> value, Optional<String> units) {
            this.value = value;
            this.units = units;
        }
    }

    public static final class FillUpProduct implements WeightMemoryCommand {
        final Optional<Double> value;
        final Optional<String> units;

        public FillUpProduct(Optional<Double> value, Optional<String> units) {
            this.value = value;
            this.units = units;
        }
    }

    public static final class ReadWeight implements WeightMemoryCommand {
        final ActorRef<WeightMemory.RequiredWeight> replyTo;

        public ReadWeight(ActorRef<WeightMemory.RequiredWeight> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class RequiredWeight {
        final Optional<Double> value;

        public RequiredWeight(Optional<Double> value) {
            this.value = value;
        }
    }

    private double sumWeight;
    private final double maxWeight;
    private final String deviceId;

    public WeightMemory(ActorContext<WeightMemoryCommand> context, double maxWeight, String deviceId) {
        super(context);
        this.maxWeight = maxWeight;
        this.deviceId = deviceId;
    }

    public static Behavior<WeightMemory.WeightMemoryCommand> create(double maxWeight, String deviceId) {
        return Behaviors.setup(context -> new WeightMemory(context, maxWeight, deviceId));
    }

    @Override
    public Receive<WeightMemoryCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::consumeProduct)
                .onMessage(FillUpProduct.class, this::fillUpProduct)
                .onMessage(ReadWeight.class, this::onRequiredWeight)
                .build();
    }

    private Behavior<WeightMemoryCommand> consumeProduct(ConsumeProduct c){
        getContext().getLog().info("WeightMemory reading the consume {} in {}", c.value.get(), c.units.get());
        if (c.value.get() != null){
            sumWeight-=c.value.get();
        }
        return this;
    }
    private Behavior<WeightMemoryCommand> fillUpProduct(FillUpProduct c){
        getContext().getLog().info("WeightMemory reading the fillUp {} in {}", c.value.get(), c.units.get());
        if (c.value.get() != null){
            sumWeight+=c.value.get();
        }
        return this;
    }

    private Behavior<WeightMemoryCommand> onRequiredWeight(ReadWeight r){
        getContext().getLog().info("WeightMemory read request {}", r.replyTo);
        r.replyTo.tell(new RequiredWeight(Optional.of(maxWeight-sumWeight)));
        return this;
    }
}
