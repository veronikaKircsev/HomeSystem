package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
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

    public static class OrderHistory implements OrderHistoryManagerCommand {
        public final ActorRef<Response> replyTo;

        public OrderHistory(ActorRef<Response> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class Response {
        public final String result;

        public Response(String result) {
            this.result = result;
        }
    }

    private List<Order> order = new ArrayList<>();
    private final String deviceId;


    @Override
    public Receive<OrderHistoryManagerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SaveOrder.class, this::saveOrder)
                .onMessage(OrderHistory.class, this::onRequest)
                .build();
    }

    private Behavior<OrderHistoryManagerCommand> saveOrder(SaveOrder o) {
        getContext().getLog().info("Saving order started {}", o.order.get());
        Order thisOrder = new Order();
        thisOrder.setOrder((HashMap<Product, Integer>) o.order.get());
        order.add(thisOrder);
        return this;

    }

    private Behavior<OrderHistoryManagerCommand> onRequest(OrderHistory request) {
        StringBuilder sb = new StringBuilder();
        for (Order o : order){
            sb.append(o.getOrderCount()).append("\n");
            for (Product p : o.getOrder().keySet()) {
                sb.append(p.getName()).append(" ").append(o.getOrder().get(p)).append("\n");
            } }
        request.replyTo.tell(new Response(sb.toString()));
        return Behaviors.same();
    }
}
