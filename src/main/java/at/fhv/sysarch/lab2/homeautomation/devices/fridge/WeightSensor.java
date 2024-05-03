package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class WeightSensor extends AbstractBehavior<WeightSensor.WeightSensorCommand> {

    public interface WeightSensorCommand{}

    public WeightSensor(ActorContext<WeightSensorCommand> context) {
        super(context);
    }

    public static Behavior<WeightSensor.WeightSensorCommand> create() {
        return Behaviors.setup(context -> new WeightSensor(context));
    }

    @Override
    public Receive<WeightSensorCommand> createReceive() {
        return null;
    }

}
