package at.fhv.sysarch.lab2.homeautomation.Environment;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Gateway;

public class InternetEnvironment extends AbstractBehavior<InternetEnvironment.InternetEnvironmentCommand> {

    public interface InternetEnvironmentCommand{}

    public InternetEnvironment(ActorContext<InternetEnvironmentCommand> context) {
        super(context);
    }

    public static Behavior<InternetEnvironment.InternetEnvironmentCommand> create() {
        return Behaviors.setup(context -> new InternetEnvironment(context));
    }

    @Override
    public Receive<InternetEnvironmentCommand> createReceive() {
        return null;
    }
}
