package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.helpClass.BlindsActuator;
import at.fhv.sysarch.lab2.homeautomation.helpClass.Weather;

import java.util.Optional;

public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherCommand> {

    public interface WeatherCommand {}

    public static class ChangeWeather implements WeatherCommand{
        final Optional<Weather> weather;

        public ChangeWeather(Optional<Weather> weather) {
            this.weather = weather;
        }
    }

    private final String groupId;
    private final String deviceId;
    private ActorRef<Blinds.BlindsCommand> blinds;


    public static Behavior<WeatherCommand> create(String groupId, String deviceId, ActorRef<Blinds.BlindsCommand> blinds){

        return Behaviors.setup(context -> new WeatherSensor(context, groupId, deviceId, blinds));
    }

    public WeatherSensor(ActorContext<WeatherCommand> context, String groupId, String deviceId, ActorRef<Blinds.BlindsCommand> blinds) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.blinds = blinds;
        getContext().getLog().info("WeatherSensor started");
    }


    @Override
    public Receive<WeatherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ChangeWeather.class, this::onReadWeather)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeatherCommand> onReadWeather(ChangeWeather r) {
        getContext().getLog().info("WeatherSensor received {}", r.weather.get());
        if (!r.weather.get().equals(Weather.SUNNY)) {
            this.blinds.tell(new Blinds.ChangeCondition(Optional.of(true), Optional.of(BlindsActuator.WeatherSensor)));
        } else {
            this.blinds.tell(new Blinds.ChangeCondition(Optional.of(false), Optional.of(BlindsActuator.WeatherSensor)));
        }
        return Behaviors.same();
    }



    private WeatherSensor onPostStop() {
        getContext().getLog().info("WeatherSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }


}
