package at.fhv.sysarch.lab2.homeautomation.devices.fridge;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class SpaceSensor extends AbstractBehavior<SpaceSensor.SpaceSensorCommand> {

    public interface SpaceSensorCommand{}

    public SpaceSensor(ActorContext<SpaceSensorCommand> context) {
        super(context);
    }

    public static Behavior<SpaceSensor.SpaceSensorCommand> create() {
        return Behaviors.setup(context -> new SpaceSensor(context));
    }

    @Override
    public Receive<SpaceSensorCommand> createReceive() {
        return null;
    }

}
