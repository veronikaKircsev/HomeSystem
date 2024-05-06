package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.helpClass.Order;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.util.*;

public class OrderHistoryManager extends AbstractBehavior<OrderHistoryManager.OrderHistoryManagerCommand> {

    public interface OrderHistoryManagerCommand{
    }

    public static class SaveOrder implements OrderHistoryManagerCommand{
        final Optional<Map<Product, Integer>> order;

        public SaveOrder(Optional<Map<Product, Integer>> order) {
            this.order = order;
        }
    }

    public OrderHistoryManager(ActorContext<OrderHistoryManagerCommand> context, String deviceId) {
        super(context);
        this.deviceId = deviceId;
        getContext().getLog().info("OrderHistoryManager is running");
    }

    public static Behavior<OrderHistoryManagerCommand> create(String deviceId){
        return Behaviors.setup(context->new OrderHistoryManager(context, deviceId));
    }

    private List<Order> order = new ArrayList<>();
    private final String deviceId;


    @Override
    public Receive<OrderHistoryManagerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SaveOrder.class, this::saveOrder)
                .build();
    }

    private Behavior<OrderHistoryManagerCommand> saveOrder(SaveOrder o) {
        getContext().getLog().info("Saving order started {}", o.order.get());
        Order thisOrder = new Order();
        thisOrder.setOrder((HashMap<Product, Integer>) o.order.get());
        order.add(thisOrder);
        return this;

    }
}
