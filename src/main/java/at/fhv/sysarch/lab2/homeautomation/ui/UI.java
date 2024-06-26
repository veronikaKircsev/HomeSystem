package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.Environment.FridgeOpticEnvironment;
import at.fhv.sysarch.lab2.homeautomation.Environment.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.Environment.WeatherEnvironment;
import at.fhv.sysarch.lab2.homeautomation.devices.*;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.helpClass.BlindsActuator;
import at.fhv.sysarch.lab2.homeautomation.helpClass.Weather;
import at.fhv.sysarch.lab2.homeautomation.products.*;

import java.util.*;

public class UI extends AbstractBehavior<Void> {

    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<MediaStation.MediaStationCommand> mediaStation;
    private ActorRef<FridgeOpticEnvironment.FridgeOpticEnvironmentCommand> fridgeOpticEnvironment;
    private ActorRef<Fridge.FridgeCommand> fridge;
    private ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> temperatureEnvironment;
    private ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnvironment;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<Blinds.BlindsCommand> blinds;


    public static Behavior<Void> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                                        ActorRef<AirCondition.AirConditionCommand> airCondition
    , ActorRef<MediaStation.MediaStationCommand> mediaStation, ActorRef<Fridge.FridgeCommand> fridge,
                                        ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> temperatureEnvironment
    , ActorRef<FridgeOpticEnvironment.FridgeOpticEnvironmentCommand> fridgeOpticEnvironment,
                                        ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnvironment,
                                        ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                                        ActorRef<Blinds.BlindsCommand> blinds) {
        return Behaviors.setup(context -> new UI(context, tempSensor, airCondition, mediaStation, fridge,
                temperatureEnvironment, fridgeOpticEnvironment, weatherEnvironment, weatherSensor, blinds));
    }

    private  UI(ActorContext<Void> context, ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                ActorRef<AirCondition.AirConditionCommand> airCondition, ActorRef<MediaStation.MediaStationCommand> mediaStation,
                ActorRef<Fridge.FridgeCommand> fridge, ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> temperatureEnvironment
                , ActorRef<FridgeOpticEnvironment.FridgeOpticEnvironmentCommand> fridgeOpticEnvironment,
                ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnvironment,
                ActorRef<WeatherSensor.WeatherCommand> weatherSensor, ActorRef<Blinds.BlindsCommand> blinds) {
        super(context);

        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.mediaStation = mediaStation;
        this.fridge = fridge;
        this.temperatureEnvironment = temperatureEnvironment;
        this.fridgeOpticEnvironment = fridgeOpticEnvironment;
        this.weatherEnvironment = weatherEnvironment;
        this.weatherSensor = weatherSensor;
        this.blinds =blinds;
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
        // fridge start filling up
        Map<Product, Integer> products = new HashMap<Product, Integer>();
        products.put(new Butter(), 2);
        products.put(new Cheese(), 2);
        products.put(new Fruit(), 4);
        products.put(new Joghurt(), 2);
        products.put(new Milk(), 2);
        products.put(new Vegetables(), 6);
        this.fridgeOpticEnvironment.tell(new FridgeOpticEnvironment.PlaceProducts(Optional.of(products)));

        Scanner scanner = new Scanner(System.in);
        String[] input = null;
        String reader = "";
        Product product;
        Weather weather = null;
        boolean com;


        while (!reader.equalsIgnoreCase("quit")) {
            System.out.println("Please enter command:\n get {product} (butter/cheese/fruit/joghurt/milk/vegetables)" +
                    "\n history (for order history)" +
                    "\n order {some products} (butter/cheese/fruit/joghurt/milk/vegetables)" +
                    "\n setWeather (set weather environment) {weather} (SUNNY, CLOUDY, RAINY, SNOWY, STORMY, WINDY, FOGGY)" +
                    "\n setWeatherSens (set weather sensor) {weather} (SUNNY, CLOUDY, RAINY, SNOWY, STORMY, WINDY, FOGGY)" +
                    "\n setTemp (set temperature environment) {number}" +
                    "\n setTempSens (set temperature sensor) {number}" +
                    "\n blinds {up/any}" +
                    "\n play (meditation) {on/any}" +
                    "\n ac {on/any}" +
                    "\n acSetTemp (temperature) {number}" +
                    "\n product (to list products)");
            reader = scanner.nextLine();

            String[] command = reader.split(" ");

            switch (command[0].toLowerCase()) {

                case "get":
                    product = setProduct(command[1]);
                    this.fridgeOpticEnvironment.tell(new FridgeOpticEnvironment.ConsumeProducts(Optional.ofNullable(product)));
                    break;
                case "history":
                    this.fridge.tell(new Fridge.OrderHistory());
                    break;
                case "order":
                    List<Product> productsOrder = new ArrayList<>();
                    for (int i = 1; i < command.length; i++) {
                        try {
                            product = setProduct(command[i]);
                            productsOrder.add(product);
                        } catch (Exception e) {
                            System.out.println("not product");
                        }
                    }
                    this.fridge.tell(new Fridge.Order(Optional.of(productsOrder)));
                    break;
                case "setweather":
                    try {
                        weather = setWeather(command[1]);
                    } catch (Exception e) {
                        System.out.println("Failed to set weather");
                    }
                    if (weather != null) {
                        this.weatherEnvironment.tell(new WeatherEnvironment.SetWeather(Optional.of(weather)));
                    }
                    break;
                case "settemp":
                    this.temperatureEnvironment.tell(new TemperatureEnvironment.SetTemperature(Optional.of(Double.valueOf(command[1]))));
                    break;
                case"setweathersens":
                    try {
                        weather = setWeather(command[1]);
                    } catch (Exception e) {
                        System.out.println("Failed to set weather");
                    }
                    if (weather != null) {
                        this.weatherSensor.tell(new WeatherSensor.ChangeWeather(Optional.of(weather)));
                    }
                    break;
                case "blinds":
                    com = command[1].equals("up") ? true : false;
                    this.blinds.tell(new Blinds.ChangeCondition(Optional.of(com), Optional.of(BlindsActuator.WeatherSensor)));
                    break;
                case "settempsens":
                    this.tempSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(Double.valueOf(command[1])), Optional.of("Celsius")));
                    break;
                case "ac":
                    com = command[1].equals("on") ? true : false;
                    this.airCondition.tell(new AirCondition.PowerAirCondition(Optional.of(Boolean.valueOf(com))));
                    break;
                case "acsetemp":
                    try {
                        double temp = Double.parseDouble(command[1]);
                        this.airCondition.tell(new AirCondition.EnrichedTemperature(Optional.of(temp), Optional.of("Celsius")));
                    } catch (Exception e) {
                        System.out.println("wrong command");
                    }
                    break;
                case "play":
                    com = command[1].equals("on") ? true : false;
                    this.mediaStation.tell(new MediaStation.ChangeCondition(Optional.of(Boolean.valueOf(com))));
                    break;
                case "product":
                    this.fridge.tell(new Fridge.ProductsRequest(Optional.of(command[0])));
                    break;
            }
        }
        getContext().getLog().info("UI done");
    }

    private Product setProduct(String p){
        Product product = null;
        switch (p.toLowerCase()){
            case "butter":
                product = new Butter();
                break;
            case "cheese":
                product = new Cheese();
                break;
            case "fruit":
                product = new Fruit();
                break;
            case "joghurt":
                product = new Joghurt();
                break;
            case "milk":
                product = new Milk();
                break;
            case "vegetable":
                product = new Vegetables();
                break;
            default:
                break;
        }
        return product;
    }

    private Weather setWeather(String w){
        Weather weather = null;
        switch (w.toLowerCase()){
            case "sunny":
                weather = Weather.SUNNY;
                break;
            case "cloudy":
                weather = Weather.CLOUDY;
                break;
            case "rainy":
                weather = Weather.RAINY;
                break;
            case "snowy":
                weather = Weather.SNOWY;
                break;
            case "stormy":
                weather = Weather.STORMY;
                break;
            case "windy":
                weather = Weather.WINDY;
                break;
            case "foggy":
                weather = Weather.FOGGY;
                break;
            default:
                break;
        }
        return weather;
    }
}
