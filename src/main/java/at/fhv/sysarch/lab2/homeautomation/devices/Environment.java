package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

public class Environment extends AbstractBehavior<Environment.EnvironmentCommand> {

    public interface EnvironmentCommand {

    }

    public static final class TemperatureChanger implements EnvironmentCommand {
        final Optional<Double> temperature;

        public TemperatureChanger(Optional<Double> temp) {
            this.temperature = temp;
        }
    }

    public static final class WeatherConditionsChanger implements EnvironmentCommand {
        final Optional<Boolean> isSunny;

        public WeatherConditionsChanger(Optional<Boolean> isSunny) {
            this.isSunny = isSunny;
        }

    }

    private double temperature = 15.0;
    private boolean isSunny = false;
    private boolean isRainy=true;
    private boolean isWindy=true;
    private boolean isCloudy=true;
    private boolean isStormy=true;

    private ActorRef<TemperatureSensor.TemperatureCommand> termometer;
    private final TimerScheduler<EnvironmentCommand> temperatureTimeScheduler;
    private final TimerScheduler<EnvironmentCommand> weatherTimeScheduler;

    // TODO: Provide the means for manually setting the temperature
    // TODO: Provide the means for manually setting the weather

    public static Behavior<EnvironmentCommand> create(){
        return Behaviors.setup(context ->  Behaviors.withTimers(timers -> new Environment(context, timers, timers)));
    }

    private Environment(ActorContext<EnvironmentCommand> context,TimerScheduler<EnvironmentCommand> tempTimer, TimerScheduler<EnvironmentCommand> weatherTimer, ActorRef<TemperatureSensor.TemperatureCommand>temp) {
        super(context);
        this.temperatureTimeScheduler = tempTimer;
        this.termometer=temp;
        this.weatherTimeScheduler = weatherTimer;
        this.temperatureTimeScheduler.startTimerAtFixedRate(new TemperatureChanger(Optional.of(temperature)), Duration.ofSeconds(5));
        this.weatherTimeScheduler.startTimerAtFixedRate(new WeatherConditionsChanger(Optional.of(isSunny)), Duration.ofSeconds(35));
    }

    @Override
    public Receive<EnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureChanger.class, this::onChangeTemperature)
                .onMessage(WeatherConditionsChanger.class, this::onChangeWeather)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<EnvironmentCommand> onChangeTemperature(TemperatureChanger t) {
        Random random = new Random();
        double randomValue = random.nextDouble() * 2 - 1;
        this.temperature += randomValue;
        getContext().getLog().info("Environment received {}", temperature);
        // TODO: Handling of temperature change. Are sensors notified or do they read the temperature?
        this.termometer.tell(new TemperatureSensor.GetTemperatur(t.temperature, Optional.of("C")), termometer);
        //=>reading?
        return this;
    }

    private Behavior<EnvironmentCommand> onChangeWeather(WeatherConditionsChanger w) {
        getContext().getLog().info("Environment Change Sun to {}", !isSunny);
        // TODO: Implement behavior for random changes to weather. Include more than just sunny and not sunny
        isSunny = !isSunny;
        // TODO: Handling of weather change. Are sensors notified or do they read the weather information?
        //=>notified
        return this;
    }


    private Environment onPostStop(){
        getContext().getLog().info("Environment actor stopped");
        return this;
    }
}


