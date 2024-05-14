package at.fhv.sysarch.lab2.homeautomation.Environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FridgeOpticEnvironment extends AbstractBehavior<FridgeOpticEnvironment.FridgeOpticEnvironmentCommand> {

    public interface FridgeOpticEnvironmentCommand{}

    public static class PlaceProducts implements FridgeOpticEnvironmentCommand{
        final Optional<Map<Product, Integer>> products;

        public PlaceProducts(Optional<Map<Product, Integer>> products) {
            this.products = products;
        }
    }

    public static class ConsumeProducts implements FridgeOpticEnvironmentCommand{
        final Optional<Product> product;

        public ConsumeProducts(Optional<Product> product) {
            this.product = product;
        }
    }

    public static class ShouldFill implements FridgeOpticEnvironmentCommand{
        public ShouldFill(){}
    }

    private Map<Product, Integer> products = new HashMap<>();
    private ActorRef<Fridge.FridgeCommand> fridge;
    private ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet;
    private final TimerScheduler<FridgeOpticEnvironmentCommand> timeScheduler;
    private final Duration timeout = Duration.ofSeconds(3);


    public FridgeOpticEnvironment(ActorContext<FridgeOpticEnvironmentCommand> context, ActorRef<Fridge.FridgeCommand> fridge,
                                  ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet, TimerScheduler<FridgeOpticEnvironmentCommand> timeScheduler) {
        super(context);
        this.fridge = fridge;
        this.internet = internet;
        this.timeScheduler = timeScheduler;
        this.timeScheduler.startTimerAtFixedRate(new ShouldFill(), Duration.ofSeconds(60));
        getContext().getLog().info("FridgeOpticEnvironment is running");
    }

    public static Behavior<FridgeOpticEnvironmentCommand> create(ActorRef<Fridge.FridgeCommand> fridge, ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet ){
        return Behaviors.setup(context-> Behaviors.withTimers(timers->new FridgeOpticEnvironment(context, fridge, internet, timers)));

    }

    @Override
    public Receive<FridgeOpticEnvironmentCommand> createReceive() {

        return newReceiveBuilder()
                .onMessage(PlaceProducts.class, this::placeProducts)
                .onMessage(ConsumeProducts.class, this::consumeProducts)
                .onMessage(ShouldFill.class, this::shouldFill)
                .build();
    }



    private Behavior<FridgeOpticEnvironmentCommand> placeProducts(PlaceProducts p) {
        getContext().getLog().debug("FridgeOpticEnvironment place product command {}", p.products.get());
        for (Product product : p.products.get().keySet()){
            if (products.isEmpty()){
                products = p.products.get();
            }
            if (products.containsKey(product)){
                products.put(product, products.get(product) + p.products.get().get(product) );
            } else {
                products.put(product, p.products.get().get(product));
            }
        }
        fridge.tell(new Fridge.ReceiveProducts(p.products));

        return this;
    }

    private Behavior<FridgeOpticEnvironmentCommand> consumeProducts(ConsumeProducts p) {
        getContext().getLog().debug("FridgeOpticEnvironment read consume {}", p.product.get().getName());
        if (products.get(p.product.get()) > 0){
            int amount = products.get(p.product.get()) - 1;
            products.put(p.product.get(), amount);
            fridge.tell(new Fridge.Consume(p.product));
        } else {
            getContext().getLog().debug("The {} not there", p.product.get().getName());
        }
        return this;
    }
    private Behavior<FridgeOpticEnvironmentCommand> shouldFill(ShouldFill f){
        getContext().ask(
               InternetEnvironment.RequiredOrder.class,
                internet,
                timeout,
                (ActorRef<InternetEnvironment.RequiredOrder> ref) -> new InternetEnvironment.ReadOrder(ref),
                (response, throwable) -> {
                    if (response != null) {
                        getContext().getLog().debug("Request get {}", response.order);
                        return new PlaceProducts(response.order);
                    } else {
                        getContext().getLog().debug("Request failed");
                    return null;
                    }
                });
        return this;
    }
}
