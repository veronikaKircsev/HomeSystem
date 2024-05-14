package at.fhv.sysarch.lab2.homeautomation.devices.fridge;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.Environment.InternetEnvironment;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {

    public interface FridgeCommand { }

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

    public static final class ReceiveProducts implements FridgeCommand {
        final Optional<Map<Product, Integer>> products;

        public ReceiveProducts(Optional<Map<Product, Integer>> products) {
            this.products = products;
        }
    }

    public static final class ProductsRequest implements FridgeCommand {
        final Optional<String> a;
        public ProductsRequest(Optional<String> a){
            this.a = a;
        }
    }


    public static class ResponseBack implements FridgeCommand{
        final Optional<String> a;
        public ResponseBack(Optional<String> a) {

            this.a = a;
        }
    }

    public static class OrderHistory implements FridgeCommand{
        public OrderHistory() {}
    }

    private final String groupId;
    private final String deviceId;
    private final int maxNumberOfProducts;
    private final double maxWeightLoad;
    private ActorRef<Gateway.GatewayCommand> gateway;
    private ActorRef<OpticSensor.OpticSensorCommand> opticSensor;
    private ActorRef<OrderProcessManager.OrderCommand> orderProcessManager;
    private ActorRef<ProductListMemory.ProductProcessCommand> productListMemory;
    private ActorRef<SpaceMemory.SpaceMemoryCommand> spaceMemory;
    private ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor;
    private ActorRef<WeightMemory.WeightMemoryCommand> weightMemory;
    private ActorRef<WeightSensor.WeightSensorCommand> weightSensor;
    private ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet;
    private ActorRef<OrderHistoryManager.OrderHistoryManagerCommand> orderHistoryManager;
    private final Duration timeout = Duration.ofSeconds(3);



    public Fridge(ActorContext<FridgeCommand> context, String groupId, String deviceId, int maxNumberOfProducts,
                  double maxWeightLoad, ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet) {
        super(context);

        this.internet = internet;
        this.orderHistoryManager = getContext().spawn(OrderHistoryManager.create("8"), "OrderHistoryManager");
        this.gateway = getContext().spawn(Gateway.create("7", groupId, this.internet), "Gateway");
        this.spaceMemory = getContext().spawn(SpaceMemory.create(maxNumberOfProducts, "6"), "SpaceMemory");
        this.weightMemory = getContext().spawn(WeightMemory.create(maxWeightLoad, "4"), "WeightMemory");
        this.spaceSensor = getContext().spawn(SpaceSensor.create("1", this.spaceMemory), "SpaceSensor");
        this.weightSensor = getContext().spawn(WeightSensor.create("2", this.weightMemory), "WeightSensor");
        this.orderProcessManager = getContext().spawn(OrderProcessManager.create(this.spaceMemory, "5", this.weightMemory, this.gateway, this.orderHistoryManager), "OrderProcessManger");
        this.productListMemory = getContext().spawn(ProductListMemory.create(this.orderProcessManager), "ProductListMemory");
        this.opticSensor = getContext().spawn(OpticSensor.create("3", this.productListMemory), "OpticSensor");
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.maxNumberOfProducts = maxNumberOfProducts;
        this.maxWeightLoad = maxWeightLoad;
        getContext().getLog().info("Fridge is started");
    }

    public static Behavior<FridgeCommand> create(String groupId, String deviceId, int maxNumberOfProducts,
                                                        double maxWeightLoad, ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet) {
        return Behaviors.setup(context -> new Fridge(context, groupId, deviceId, maxNumberOfProducts, maxWeightLoad, internet));
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Consume.class, this::consume)
                .onMessage(Order.class, this::order)
                .onMessage(ReceiveProducts.class, this::receiveProducts)
                .onMessage(ProductsRequest.class, this::productRequest)
                .onMessage(ResponseBack.class, this::onResponse)
                .onMessage(OrderHistory.class, this::onOrderHistory)
                .build();
    }

    private Behavior<FridgeCommand> consume(Consume c){
        getContext().getLog().debug("Fridge reading the consume {}", c.product.get().getName());
        spaceSensor.tell(new SpaceSensor.ConsumeProduct(Optional.of(c.product.get())));
        weightSensor.tell(new WeightSensor.ConsumeProduct(Optional.of(c.product.get())));
        opticSensor.tell(new OpticSensor.ConsumeProduct(Optional.of(c.product.get())));
        return this;
    }

    private Behavior<FridgeCommand> order(Order o){
        getContext().getLog().debug("Fridge reading the order {}", o.products.get());
        Map<Product, Integer> productOrder = new HashMap<>();
        int amount = 0;
        for (Product product: o.products.get()) {
            if (productOrder.containsKey(product)){
                amount += productOrder.get(product);
                productOrder.put(product, amount);
            } else {
                productOrder.put(product,1);
            }
        }
        orderProcessManager.tell(new OrderProcessManager.StartOrder(Optional.of(productOrder)));
        return this;
    }

    private Behavior<FridgeCommand> receiveProducts(ReceiveProducts r){
        getContext().getLog().debug("Fridge reading the received products {}", r.products.get());
        weightSensor.tell(new WeightSensor.FillUpProduct(Optional.of(r.products.get())));
        spaceSensor.tell(new SpaceSensor.FillUpProduct(Optional.of(r.products.get())));
        opticSensor.tell(new OpticSensor.FillUpProduct(Optional.of(r.products.get())));
        return this;
    }

    private Behavior<FridgeCommand> productRequest(ProductsRequest r) {
        getContext().ask(
                ProductListMemory.Response.class,
                productListMemory,
                timeout,
                (ActorRef<ProductListMemory.Response> ref) -> new ProductListMemory.RequestProducts(ref),
                (response, throwable) -> {
                    if (response != null) {
                        getContext().getLog().debug("Request get {}", response.result);
                        return new ResponseBack(Optional.ofNullable(response.result));
                    } else {
                        getContext().getLog().debug("Request failed");
                        return new ResponseBack(Optional.empty());
                    }
                });
        return this;
    }

    private Behavior<FridgeCommand> onResponse(ResponseBack a) {
        getContext().getLog().info(a.a.get());
        return this;
    }

    private Behavior<FridgeCommand> onOrderHistory(OrderHistory a) {
        getContext().ask(
                OrderHistoryManager.Response.class,
                orderHistoryManager,
                timeout,
                (ActorRef<OrderHistoryManager.Response> ref) -> new OrderHistoryManager.OrderHistory(ref),
                (response, throwable) -> {
                    if (response != null) {
                        getContext().getLog().info("Request get {}", response.result);
                        return new ResponseBack(Optional.ofNullable(response.result));
                    } else {
                        getContext().getLog().info("Request failed");
                        return new ResponseBack(Optional.empty());
                    }
                });
        return this;
    }

}
