package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class WeightMemory extends AbstractBehavior<WeightMemory.WeightMemoryCommand> {


    public interface WeightMemoryCommand {}

    public WeightMemory(ActorContext<WeightMemoryCommand> context) {
        super(context);
    }

    public static Behavior<WeightMemory.WeightMemoryCommand> create() {
        return Behaviors.setup(context -> new WeightMemory(context));
    }

    @Override
    public Receive<WeightMemoryCommand> createReceive() {
        return null;
    }

}
