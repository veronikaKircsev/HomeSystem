package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.Environment.FridgeOpticEnvironment;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;

import java.util.Optional;
import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {

    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<MediaStation.MediaStationCommand> mediaStation;
    private ActorRef<FridgeOpticEnvironment.FridgeOpticEnvironmentCommand> fridgeOpticEnvironment;
    private ActorRef<Fridge.FridgeCommand> fridge;


    public static Behavior<Void> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                                        ActorRef<AirCondition.AirConditionCommand> airCondition
    , ActorRef<MediaStation.MediaStationCommand> mediaStation, ActorRef<Fridge.FridgeCommand> fridge) {
        return Behaviors.setup(context -> new UI(context, tempSensor, airCondition, mediaStation, fridge));
    }

    private  UI(ActorContext<Void> context, ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                ActorRef<AirCondition.AirConditionCommand> airCondition, ActorRef<MediaStation.MediaStationCommand> mediaStation, ActorRef<Fridge.FridgeCommand> fridge) {
        super(context);
        // TODO: implement actor and behavior as needed
        // TODO: move UI initialization to appropriate place
        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.mediaStation = mediaStation;
        this.fridge = fridge;
        new Thread(() -> this.runCommandLine()).start();

        getContext().getLog().info("UI started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private UI onPostStop() {
        getContext().getLog().info("UI stopped");
        return this;
    }

    public void runCommandLine() {
        // TODO: Create products for fridge and fill up the fridge


        // TODO: Create Actor for UI Input-Handling?
        Scanner scanner = new Scanner(System.in);
        String[] input = null;
        String reader = "";


        while (!reader.equalsIgnoreCase("quit")) {
            reader = scanner.nextLine();
            // TODO: change input handling
            String[] command = reader.split(" ");

            //TODO: implement consume by environment
            //TODO: implement the contained products by fridge
            //TODO: implement the order history by fridge
            // TODO:implement ordering by fridge
            // TODO:implement set weather by environment
            // TODO: implement set temperature by environment
            // TODO: implement set sensor daten by weather sensor
            // TODO: implement the blinds command



            // TODO: implement set sensor daten by temperature sensor
            // it should be better
            if(command[0].equals("t")) {
                this.tempSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(Double.valueOf(command[1])), Optional.of("Celsius")));
            }

            // TODO: it should be better
            if(command[0].equals("a")) {
                this.airCondition.tell(new AirCondition.PowerAirCondition(Optional.of(Boolean.valueOf(command[1]))));
            }

            //TODO: mediaStation play it should be better done
            if (command[0].equals("m")){
                boolean com = command[1].equals("y") ? true : false;
                this.mediaStation.tell(new MediaStation.ChangeCondition(Optional.of(Boolean.valueOf(com))));

            }
            // TODO: it should be better
            if (command[0].equals("s")){
                this.fridge.tell(new Fridge.ProductsRequest(Optional.of(command[0])));
            }
            // TODO: process Input
        }
        getContext().getLog().info("UI done");
    }
}
