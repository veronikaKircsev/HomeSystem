package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;

public class Gateway extends AbstractBehavior<Gateway.GatewayCommand> {


    public interface GatewayCommand{}

    public Gateway(ActorContext<GatewayCommand> context) {
        super(context);
    }

    public static Behavior<Gateway.GatewayCommand> create() {
        return Behaviors.setup(context -> new Gateway(context));
    }

    @Override
    public Receive<GatewayCommand> createReceive() {
        return null;
    }
}
