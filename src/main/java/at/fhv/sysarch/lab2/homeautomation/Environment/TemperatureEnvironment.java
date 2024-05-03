package at.fhv.sysarch.lab2.homeautomation.Environment;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.TemperatureSensor;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

import static akka.actor.TypedActor.self;

public class TemperatureEnvironment extends AbstractBehavior<TemperatureEnvironment.TemperatureEnvironmentCommand> {


    public interface TemperatureEnvironmentCommand {}

    public static final class TemperatureChanger implements TemperatureEnvironmentCommand {
        public TemperatureChanger() {
        }
    }

    private double temperature;
    private ActorRef<TemperatureSensor.TemperatureCommand> temperatureSensor;
    private ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> selfRef = self();

    private final TimerScheduler<TemperatureEnvironmentCommand> temperatureTimeScheduler;

    public static final class SetTemperature implements TemperatureEnvironmentCommand {
        final Optional<Double> celsius;

        public SetTemperature(Optional<Double> celsius) {
            this.celsius = celsius;
        }
    }


    //the create() method is used to create the behavior for the Environment actor.
    // It returns a Behavior instance that defines how the actor should behave.
    public static Behavior<TemperatureEnvironmentCommand> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor, int startingTemperature){
        //Behaviors.setup(), which is a factory method used to define the initial behavior of the actor
        //the actor's context, which provides access to various features and utilities
        // for managing the actor's lifecycle and interacting with other actors
        return Behaviors.setup(context ->  Behaviors.withTimers(timers -> new TemperatureEnvironment(context, tempSensor, timers, startingTemperature)));
    }

    private TemperatureEnvironment(ActorContext<TemperatureEnvironmentCommand> context, ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                        TimerScheduler<TemperatureEnvironmentCommand> tempTimer, int startingTemperature) {

        super(context);
        this.temperatureSensor = tempSensor;
        this.temperatureTimeScheduler = tempTimer;
        this.temperature = startingTemperature;
        this.temperatureTimeScheduler.startTimerAtFixedRate(new TemperatureChanger(), Duration.ofSeconds(5));
    }

    //Any Akka actor will extend the AbstractActor abstract class and implement the createReceive() method
    // for handling the incoming messages from other actors:
    @Override
    public Receive<TemperatureEnvironmentCommand> createReceive() {
        // It can receive messages from other actors and will
        // discard them because no matching message patterns are defined in the ReceiveBuilder.
        return newReceiveBuilder()
                //react to incoming messages
                .onMessage(TemperatureChanger.class, this::onChangeTemperature)
                .onMessage(SetTemperature.class, this::onSetTemperature)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<TemperatureEnvironmentCommand> onSetTemperature(SetTemperature command) {
        this.temperature = command.celsius.get();
        getContext().getLog().info("TemperatureEnvironment received new {}", temperature);
        return this;
    }

    private Behavior<TemperatureEnvironmentCommand> onChangeTemperature(TemperatureChanger t) {
        Random random = new Random();
        double randomValue = random.nextDouble() * 2 - 1;
        this.temperature += randomValue;
        getContext().getLog().info("TemperatureEnvironment received {}", temperature);
        this.temperatureSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(temperature)));
        selfRef.tell(new TemperatureEnvironment.SetTemperature(Optional.of(temperature)));
        return this;
    }

    private TemperatureEnvironment onPostStop(){
        getContext().getLog().info("TemperatureEnvironment actor stopped");
        return this;
    }
}


