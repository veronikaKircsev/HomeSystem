package at.fhv.sysarch.lab2.homeautomation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.Environment.InternetEnvironment;
import at.fhv.sysarch.lab2.homeautomation.Environment.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.Environment.WeatherEnvironment;
import at.fhv.sysarch.lab2.homeautomation.devices.*;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.ui.UI;

public class HomeAutomationController extends AbstractBehavior<Void>{

    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<Blinds.BlindsCommand> blinds;
    private ActorRef<MediaStation.MediaStationCommand> mediaStation;
    private ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> temperatureEnviroment;
    private ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnvironment;
    private ActorRef<Fridge.FridgeCommand> fridge;
    private ActorRef<InternetEnvironment.InternetEnvironmentCommand> internetEnvironment;

    public static Behavior<Void> create() {
        return Behaviors.setup(HomeAutomationController::new);
    }

    private  HomeAutomationController(ActorContext<Void> context) {

        super(context);
        // TODO: consider guardians and hierarchies. Who should create and communicate with which Actors?
        this.airCondition = getContext().spawn(AirCondition.create("2", "1"), "AirCondition");
        this.blinds = getContext().spawn(Blinds.create("2", "2"), "Blinds");
        this.mediaStation = getContext().spawn(MediaStation.create("2", "3", this.blinds), "MediaStation");
        this.fridge = getContext().spawn(Fridge.create("2", "4", 40, 400), "Fridge");
        this.internetEnvironment = getContext().spawn(InternetEnvironment.create(), "InternetEnvironment");

        this.tempSensor = getContext().spawn(TemperatureSensor.create(this.airCondition, "1", "1"), "temperatureSensor");
        this.weatherSensor = getContext().spawn(WeatherSensor.create("1", "2", this.blinds), "weatherSensor");

        this.temperatureEnviroment = getContext().spawn(TemperatureEnvironment.create(this.tempSensor, 23), "TemperatureEnvironment");
        this.weatherEnvironment = getContext().spawn(WeatherEnvironment.create(this.weatherSensor), "WeatherEnvironment");
        ActorRef<Void> ui = getContext().spawn(UI.create(this.tempSensor, this.airCondition), "UI");

        getContext().getLog().info("HomeAutomation Application started");
    }


    @Override
    public Receive<Void> createReceive() {

        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private HomeAutomationController onPostStop() {
        getContext().getLog().info("HomeAutomation Application stopped");
        return this;
    }
}
