package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.Environment.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;



public class OrderProcessManager extends AbstractBehavior<OrderProcessManager.OrderCommand> {


    public interface OrderCommand{}


    public static final class StartOrder implements OrderCommand{
        final Optional<Map<Product, Integer>> order;

        public StartOrder( Optional<Map<Product, Integer>> order) {
            this.order = order;
        }
    }

    public static final class SetSpace implements OrderCommand{
        final Optional<Integer> value;
        final Optional<Map<Product, Integer>> order;

        public SetSpace(Optional<Integer> value, Optional<Map<Product, Integer>> order) {
            this.value = value;
            this.order = order;
        }
    }

    public static final class SetWeight implements OrderCommand{
        final Optional<Double> value;
        final Optional<Map<Product, Integer>> order;

        public SetWeight(Optional<Double> value, Optional<Map<Product, Integer>> order) {
            this.value = value;
            this.order = order;
        }
    }

    public static final class TryOrder implements OrderCommand{
        final Optional<Map<Product, Integer>> order;
        public TryOrder(Optional<Map<Product, Integer>> order) {
            this.order = order;
        }
    }

    private ActorRef<SpaceMemory.SpaceMemoryCommand> space;
    private ActorRef<WeightMemory.WeightMemoryCommand> weight;
    private ActorRef<Gateway.GatewayCommand> gateway;
    private ActorRef<OrderHistoryManager.OrderHistoryManagerCommand> orderHistoryManager;
    private final Duration timeout = Duration.ofSeconds(3);
    private int spaceLeft;
    private double weightSpaceLeft;
    private final String deviceId;
    private ActorContext<OrderProcessManager.OrderCommand> context = getContext();
    private ActorRef<OrderProcessManager.OrderCommand> selfRef = context.getSelf();


    public OrderProcessManager(ActorContext<OrderCommand> context, ActorRef<SpaceMemory.SpaceMemoryCommand> space,
                               String deviceId, ActorRef<WeightMemory.WeightMemoryCommand> weight, ActorRef<Gateway.GatewayCommand> gateway,
                               ActorRef<OrderHistoryManager.OrderHistoryManagerCommand> orderHistoryManager) {
        super(context);
        this.space = space;
        this.deviceId = deviceId;
        this.weight = weight;
        this.gateway = gateway;
        this.orderHistoryManager = orderHistoryManager;
    }


    public static Behavior<OrderCommand> create(ActorRef<SpaceMemory.SpaceMemoryCommand> space,
                                                                    String deviceId, ActorRef<WeightMemory.WeightMemoryCommand> weight,
                                                                    ActorRef<Gateway.GatewayCommand> gateway,
                                                                    ActorRef<OrderHistoryManager.OrderHistoryManagerCommand> orderHistoryManager) {
        return Behaviors.setup(context -> new OrderProcessManager(context, space, deviceId, weight, gateway, orderHistoryManager));
    }

    @Override
    public Receive<OrderCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(SetSpace.class, this::setSpace)
                .onMessage(SetWeight.class, this::setWeight)
                .onMessage(StartOrder.class, this::processFreePlaces)
                .onMessage(TryOrder.class, this::tryOrder)
                .build();
    }

    private Behavior<OrderCommand> processFreePlaces(StartOrder o) {

        getContext().getLog().info("OrderProcess reading {}", o.order.get());

        getContext().ask(
                SpaceMemory.RequiredSpace.class,
                space,
                timeout,
                (ActorRef<SpaceMemory.RequiredSpace> ref) -> new SpaceMemory.ReadSpace(ref),
                (response, throwable) -> {
                    if (response != null) {
                        getContext().getLog().info("Request get {}", response.value);
                        return new SetSpace(response.value, o.order);
                    } else {
                        getContext().getLog().info("Request failed");
                        return new SetSpace(Optional.empty(), o.order);
                    }
                });

        getContext().ask(
                WeightMemory.RequiredWeight.class,
                weight,
                timeout,
                (ActorRef<WeightMemory.RequiredWeight> ref) -> new WeightMemory.ReadWeight(ref),
                (response, throwable) -> {
                    if (response != null) {
                        getContext().getLog().info("Request get {}", response.value);
                        return new SetWeight(response.value, o.order);
                    } else {
                        getContext().getLog().info("Request failed");
                        return new SetWeight(Optional.empty(), o.order);
                    }
                });

        return this;
    }

    private Behavior<OrderCommand> setSpace(SetSpace s) {
        getContext().getLog().info("Setting space{}", s.value.get());
        spaceLeft = s.value.get();
        selfRef.tell(new TryOrder(s.order));
        return this;
    }

    private Behavior<OrderCommand> setWeight(SetWeight s) {
        getContext().getLog().info("Setting weight {}", s.value.get());
        weightSpaceLeft = s.value.get();
        selfRef.tell(new TryOrder(s.order));
        return this;
    }

    private Behavior<OrderCommand> tryOrder(TryOrder o) {
        if (spaceLeft !=0 && weightSpaceLeft !=0) {
            int countSpace = 0;
            double countedWeight = 0;

            for (Product product : o.order.get().keySet()) {
                int productAmount = o.order.get().get(product);
                double weight = product.getWeightInKg();
                double productsWeight = product.getWeightInKg() * productAmount;
                getContext().getLog().info("OrderProcessor reading {} with sumWeight {} and amount {}", product.getName(),
                        productsWeight, productAmount);
                countSpace += productAmount;
                countedWeight += productsWeight;
            }
            //check for order
            if (spaceLeft >= countSpace && weightSpaceLeft >= countedWeight){
                gateway.tell(new Gateway.SendOrder(o.order));
                orderHistoryManager.tell(new OrderHistoryManager.SaveOrder(o.order));
                getContext().getLog().info("Order was sent out");
            } else {
                getContext().getLog().info("Sorry the space left not enough for this order");
            }
            spaceLeft = 0;
            weightSpaceLeft = 0;
        }

        return this;
    }




}
