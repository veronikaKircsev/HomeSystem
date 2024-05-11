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


public class TemperatureEnvironment extends AbstractBehavior<TemperatureEnvironment.TemperatureEnvironmentCommand> {


    public interface TemperatureEnvironmentCommand {}

    public static final class TemperatureChanger implements TemperatureEnvironmentCommand {
        public TemperatureChanger() {
        }
    }

    public static final class SetTemperature implements TemperatureEnvironmentCommand {
        final Optional<Double> celsius;

        public SetTemperature(Optional<Double> celsius) {
            this.celsius = celsius;
        }
    }

    private double temperature;
    private ActorRef<TemperatureSensor.TemperatureCommand> temperatureSensor;
    private ActorContext<TemperatureEnvironment.TemperatureEnvironmentCommand> context = getContext();
    private ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> selfRef = context.getSelf();

    private final TimerScheduler<TemperatureEnvironmentCommand> temperatureTimeScheduler;


    public static Behavior<TemperatureEnvironmentCommand> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor, int startingTemperature){

        return Behaviors.setup(context ->  Behaviors.withTimers(timers -> new TemperatureEnvironment(context, tempSensor, timers, startingTemperature)));
    }

    private TemperatureEnvironment(ActorContext<TemperatureEnvironmentCommand> context, ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                        TimerScheduler<TemperatureEnvironmentCommand> tempTimer, int startingTemperature) {

        super(context);
        this.temperatureSensor = tempSensor;
        this.temperatureTimeScheduler = tempTimer;
        this.temperature = startingTemperature;
        this.temperatureTimeScheduler.startTimerAtFixedRate(new TemperatureChanger(), Duration.ofSeconds(30));
    }

    @Override
    public Receive<TemperatureEnvironmentCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(TemperatureChanger.class, this::onChangeTemperature)
                .onMessage(SetTemperature.class, this::onSetTemperature)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<TemperatureEnvironmentCommand> onSetTemperature(SetTemperature command) {
        this.temperature = command.celsius.get();
        getContext().getLog().info("TemperatureEnvironment received {}", temperature);
        return this;
    }

    private Behavior<TemperatureEnvironmentCommand> onChangeTemperature(TemperatureChanger t) {
        Random random = new Random();
        double randomValue = random.nextDouble() * 2 - 1;
        this.temperature += randomValue;
        double roundedValue = temperature >= 0 ? Math.floor(temperature * 100) / 100 : Math.ceil(temperature * 100) / 100;
        temperature = roundedValue;
        getContext().getLog().info("TemperatureEnvironment received {}", temperature);
        this.temperatureSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(temperature), Optional.of("Celsius")));
        selfRef.tell(new TemperatureEnvironment.SetTemperature(Optional.of(temperature)));
        return this;
    }

    private TemperatureEnvironment onPostStop(){
        getContext().getLog().info("TemperatureEnvironment actor stopped");
        return this;
    }
}


