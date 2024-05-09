package at.fhv.sysarch.lab2.homeautomation.Environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.helpClass.Weather;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

public class WeatherEnvironment extends AbstractBehavior<WeatherEnvironment.WeatherEnvironmentCommand> {


    public interface WeatherEnvironmentCommand {}


    public static final class WeatherConditionsChanger implements WeatherEnvironmentCommand {

        public WeatherConditionsChanger() {

        }
    }

    private Weather weather;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    ActorContext<WeatherEnvironment.WeatherEnvironmentCommand> context = getContext();
    private ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> selfRef = context.getSelf();
    private final TimerScheduler<WeatherEnvironmentCommand> weatherTimeScheduler;


    public static final class SetWeather implements WeatherEnvironmentCommand {
        final Optional<Weather> weather;

        public SetWeather(Optional<Weather> weather) {
            this.weather = weather;
        }
    }

    public static Behavior<WeatherEnvironmentCommand> create(ActorRef<WeatherSensor.WeatherCommand> weatherSensor){

        return Behaviors.setup(context ->  Behaviors.withTimers(timers -> new WeatherEnvironment(context, weatherSensor, timers)));
    }

    private WeatherEnvironment(ActorContext<WeatherEnvironmentCommand> context, ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                        TimerScheduler<WeatherEnvironmentCommand> weatherTimer) {
        super(context);
        this.weatherSensor = weatherSensor;
        this.weatherTimeScheduler = weatherTimer;
        this.weatherTimeScheduler.startTimerAtFixedRate(new WeatherConditionsChanger(), Duration.ofSeconds(35));
    }

    @Override
    public Receive<WeatherEnvironmentCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(WeatherConditionsChanger.class, this::onChangeWeather)
                .onMessage(SetWeather.class, this::onSetWeather)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeatherEnvironmentCommand> onSetWeather(SetWeather command) {
        this.weather = command.weather.get();
        getContext().getLog().info("Environment received {}", weather.toString());
        return this;
    }


    private Behavior<WeatherEnvironmentCommand> onChangeWeather(WeatherConditionsChanger w) {
        Weather[] allWeathers = Weather.values();
        Random random = new Random();
        int randomIndex = random.nextInt(allWeathers.length);
        getContext().getLog().info("Environment Change {} to {}", weather, allWeathers[randomIndex].toString());
        selfRef.tell(new WeatherEnvironment.SetWeather(Optional.of(allWeathers[randomIndex])));
        this.weatherSensor.tell(new WeatherSensor.ChangeWeather(Optional.of(allWeathers[randomIndex])));
        return this;
    }


    private WeatherEnvironment onPostStop(){
        getContext().getLog().info("WeatherEnvironment actor stopped");
        return this;
    }
}
