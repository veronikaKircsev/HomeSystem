package at.fhv.sysarch.lab2.homeautomation.devices.fridge;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.List;
import java.util.Optional;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {


    public interface FridgeCommand {

    }

    public static final class Consume implements FridgeCommand {
        final Optional<Product> product;

        public Consume(Optional<Product> product) {
            this.product = product;
        }
    }

    public static final class Order implements FridgeCommand {
        final Optional<List<Product>> products;

        public Order(Optional<List<Product>> products) {
            this.products = products;
        }
    }



    private final String groupId;
    private final String deviceId;
    private final double maxNumberOfProducts;
    private final double maxWeightLoad;
    private final ActorRef<Gateway.GatewayCommand> gateway;
    private final ActorRef<OpticSensor.OpticSensorCommand> opticSensor;
    private final ActorRef<OrderProcessManager.OrderCommand> orderProcessManager;
    private final ActorRef<ProductListMemory.ProductProcessCommand> productListMemory;
    private final ActorRef<SpaceMemory.SpaceMemoryCommand> spaceMemory;
    private final ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor;
    private final ActorRef<WeightMemory.WeightMemoryCommand> weightMemory;
    private final ActorRef<WeightSensor.WeightSensorCommand> weightSensor;



    public Fridge(ActorContext<FridgeCommand> context, String groupId, String deviceId, double maxNumberOfProducts, double maxWeightLoad) {
        super(context);
        //greeter = context.spawn(Greeter.create(), "greeter");
        this.gateway = getContext().spawn(Gateway.create(), "Gateway");
        this.opticSensor = getContext().spawn(OpticSensor.create(), "OpticSensor");
        this.productListMemory = getContext().spawn(ProductListMemory.create(), "ProductListMemory");
        this.spaceMemory = getContext().spawn(SpaceMemory.create(), "SpaceMemory");
        this.spaceSensor = getContext().spawn(SpaceSensor.create(), "SpaceSensor");
        this.weightMemory = getContext().spawn(WeightMemory.create(), "WeightMemory");
        this.weightSensor = getContext().spawn(WeightSensor.create(), "WeightSensor");
        this.orderProcessManager = getContext().spawn(OrderProcessManager.create(this.spaceMemory), "OrderProcessManger");

        this.groupId = groupId;
        this.deviceId = deviceId;
        this.maxNumberOfProducts = maxNumberOfProducts;
        this.maxWeightLoad = maxWeightLoad;
        getContext().getLog().info("HomeAutomation Application started");
    }

    public static Behavior<Fridge.FridgeCommand> create(String groupId, String deviceId, double maxNumberOfProducts, double maxWeightLoad) {
        return Behaviors.setup(context -> new Fridge(context, groupId, deviceId, maxNumberOfProducts, maxWeightLoad));
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return null;
    }

    private Behavior<FridgeCommand> consume(Consume c){
        //TODO implement
        return this;
    }

    private Behavior<FridgeCommand> order(Order o){
        //TODO implement
        return this;
    }
}
