package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.Environment.InternetEnvironment;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.Map;
import java.util.Optional;

public class Gateway extends AbstractBehavior<Gateway.GatewayCommand> {

    public interface GatewayCommand{}

    public static final class SendOrder implements GatewayCommand{
        final Optional<Map<Product, Integer>> orderedProducts;

        public SendOrder(Optional<Map<Product, Integer>> orderedProducts) {
            this.orderedProducts = orderedProducts;
        }
    }

    private final String deviceId;
    private final String groupId;
    private ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet;

    public Gateway(ActorContext<GatewayCommand> context, String deviceId, String groupId, ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet) {
        super(context);
        this.deviceId = deviceId;
        this.groupId = groupId;
        this.internet = internet;
        getContext().getLog().info("Gateway is running");
    }

    public static Behavior<GatewayCommand> create(String deviceId, String groupId, ActorRef<InternetEnvironment.InternetEnvironmentCommand> internet) {
        return Behaviors.setup(context -> new Gateway(context, deviceId, groupId, internet));
    }

    @Override
    public Receive<GatewayCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SendOrder.class, this::sendOrder)
                .build();
    }

    private Behavior<GatewayCommand> sendOrder(SendOrder order) {
        getContext().getLog().debug("Getaway process order {}", order.toString());
        internet.tell(new InternetEnvironment.ProcessOrder(order.orderedProducts));
        return this;
    }
}
