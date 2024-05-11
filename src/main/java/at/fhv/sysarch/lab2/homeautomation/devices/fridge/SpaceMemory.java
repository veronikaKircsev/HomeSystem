package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.Optional;

public class SpaceMemory extends AbstractBehavior<SpaceMemory.SpaceMemoryCommand> {

    public interface SpaceMemoryCommand{}

    public static final class ConsumeProduct implements SpaceMemoryCommand {
        final Optional<Integer> value;

        public ConsumeProduct(Optional<Integer> value) {
            this.value = value;
        }
    }

    public static final class FillUpProduct implements SpaceMemoryCommand {
        final Optional<Integer> value;

        public FillUpProduct(Optional<Integer> value) {
            this.value = value;
        }
    }


    public static final class ReadSpace implements SpaceMemoryCommand {
        final ActorRef<SpaceMemory.RequiredSpace> replyTo;

        public ReadSpace(ActorRef<SpaceMemory.RequiredSpace> replyTo) {
            this.replyTo = replyTo;
        }
    }


    public static final class RequiredSpace {
        final Optional<Integer> value;

        public RequiredSpace(Optional<Integer> value) {
            this.value = value;
        }
    }

    private int productCount;
    private final int maxProduct;
    private final String deviceId;

    public SpaceMemory(ActorContext<SpaceMemoryCommand> context, int maxProduct, String deviceId) {
        super(context);
        this.maxProduct = maxProduct;
        this.deviceId = deviceId;
    }

    public static Behavior<SpaceMemory.SpaceMemoryCommand> create(int maxProduct, String deviceId) {
        return Behaviors.setup(context -> new SpaceMemory(context, maxProduct, deviceId));
    }

    @Override
    public Receive<SpaceMemoryCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::consumeProduct)
                .onMessage(FillUpProduct.class, this::fillUpProduct)
                .onMessage(ReadSpace.class, this::onRequiredSpace)
                .build();
    }


    private Behavior<SpaceMemoryCommand> onRequiredSpace(ReadSpace r){
        getContext().getLog().info("SpaceMemory read request {}", r.replyTo);
        r.replyTo.tell(new RequiredSpace(Optional.of(maxProduct-productCount)));
        return this;
    }

    private Behavior<SpaceMemoryCommand> consumeProduct(ConsumeProduct c){
        getContext().getLog().info("SpaceMemory reading the consume {}", c.value.get());
        if (c.value != null){
            productCount-=c.value.get();
        }
        return this;
    }

    private Behavior<SpaceMemoryCommand> fillUpProduct(FillUpProduct c){
        getContext().getLog().info("SpaceMemory reading the fillUp {}", c.value.get());
        if (c.value.get() != null){
            productCount+=c.value.get();
        }
        return this;
    }


}
