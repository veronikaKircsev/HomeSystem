package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class OpticSensor extends AbstractBehavior<OpticSensor.OpticSensorCommand> {


    public interface OpticSensorCommand{}

    public OpticSensor(ActorContext<OpticSensorCommand> context) {
        super(context);
    }

    public static Behavior<OpticSensor.OpticSensorCommand> create() {
        return Behaviors.setup(context -> new OpticSensor(context));
    }

    @Override
    public Receive<OpticSensorCommand> createReceive() {
        return null;
    }

}
