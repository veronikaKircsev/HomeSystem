package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.ActorSelection;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.util.LineNumbers;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;


public class OrderProcessManager extends AbstractBehavior<OrderProcessManager.OrderCommand> {


    public interface OrderCommand{}


    public static final class RespondSpace implements OrderCommand{
        final Optional<Integer> value;

        public RespondSpace(Optional<Integer> value) {
            this.value = value;
        }
    }

    public static final class SetSpace implements OrderCommand{
        final Optional<Integer> value;

        public SetSpace(Optional<Integer> value) {
            this.value = value;
        }
    }

    private ActorRef<SpaceMemory.SpaceMemoryCommand> space;
    private final Duration timeout = Duration.ofSeconds(3);
    private Optional<Integer> countedSpace;


    public OrderProcessManager(ActorContext<OrderCommand> context, ActorRef<SpaceMemory.SpaceMemoryCommand> space) {
        super(context);
        this.space = space;
    }


    public static Behavior<OrderProcessManager.OrderCommand> create(ActorRef<SpaceMemory.SpaceMemoryCommand> space) {
        return Behaviors.setup(context -> new OrderProcessManager(context, space));
    }

    @Override
    public Receive<OrderCommand> createReceive() {
        return null;
    }

    private Behavior<OrderProcessManager.OrderCommand> processOrder(ActorContext<OrderCommand> context) {

        /*
        context.ask(
                SpaceMemory.RequiredSpace.class,
                space,
                timeout,
                (ActorRef<SpaceMemory.RequiredSpace> ref) -> new SpaceMemory.ReadSpace(ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    Optional<Integer> size;
                    if (response != null) {
                        return new SetSpace(response.value);
                    } else {
                        getContext().getLog().info("Request failed");
                        return new SetSpace(Optional.empty());
                    }
                });

        CompletableFuture<Object> future1 =
                ask((ActorSelection) space, "request", Duration.ofMillis(1000)).toCompletableFuture();

        CompletableFuture<LineNumbers.Result> transformed =
                future1.thenCombine(future2, (x, s) -> new LineNumbers.Result((String) x, (String) s));

         */


        return this;
    }

    private Behavior<OrderProcessManager.OrderCommand> setSpace(SetSpace s) {
        countedSpace = s.value;
        return this;
    }



}
