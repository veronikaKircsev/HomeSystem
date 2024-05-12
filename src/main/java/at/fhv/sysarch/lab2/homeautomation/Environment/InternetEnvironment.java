package at.fhv.sysarch.lab2.homeautomation.Environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.SpaceMemory;
import at.fhv.sysarch.lab2.homeautomation.helpClass.ProductReceipt;
import at.fhv.sysarch.lab2.homeautomation.helpClass.Receipt;
import at.fhv.sysarch.lab2.homeautomation.products.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InternetEnvironment extends AbstractBehavior<InternetEnvironment.InternetEnvironmentCommand> {

    public interface InternetEnvironmentCommand{}

    public static class ProcessOrder implements InternetEnvironmentCommand{
        final Optional<Map<Product, Integer>> order;

        public ProcessOrder(Optional<Map<Product, Integer>> order) {
            this.order = order;
        }
    }

    public static final class ReadOrder implements InternetEnvironmentCommand {
        final ActorRef<InternetEnvironment.RequiredOrder> replyTo;

        public ReadOrder(ActorRef<RequiredOrder> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class RequiredOrder {
        final Optional<Map<Product, Integer>> order;

        public RequiredOrder(Optional<Map<Product, Integer>> order) {
            this.order = order;
        }
    }

    private final String deviceId;
    private final String groupId;
    private Map<Product, Integer> orderedProducts = new HashMap<Product, Integer>();

    public InternetEnvironment(ActorContext<InternetEnvironmentCommand> context, String deviceId, String groupId) {
        super(context);
        this.deviceId = deviceId;
        this.groupId = groupId;
        getContext().getLog().info("Internet connected");
    }

    public static Behavior<InternetEnvironment.InternetEnvironmentCommand> create(String deviceId, String groupId) {
        return Behaviors.setup(context -> new InternetEnvironment(context, deviceId, groupId));
    }

    @Override
    public Receive<InternetEnvironmentCommand> createReceive() {
        return  newReceiveBuilder()
                .onMessage(ProcessOrder.class, this::processOrder)
                .onMessage(ReadOrder.class, this::onRequiredOrder)
                .build();
    }

    private Behavior<InternetEnvironmentCommand> processOrder(ProcessOrder order) {
        getContext().getLog().info("InternetEnvironment actor read the order{}", order.order.toString());
            Receipt receipt = new Receipt();
        for (Product product : order.order.get().keySet()) {
            ProductReceipt productReceipt = new ProductReceipt();
            productReceipt.setName(product.getName());
            productReceipt.setPrice(product.getPrice());
            productReceipt.setAmount(order.order.get().get(product));
            productReceipt.calculatePrise();
            receipt.addProduct(productReceipt);
        }
        try {
            FileWriter writer = new FileWriter("receipt.txt");
            writer.write(receipt.toString());
            getContext().getLog().info("Receipt are created {}", receipt.toString());
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        orderedProducts = order.order.get();

        return this;
    }

    private Behavior<InternetEnvironmentCommand> onRequiredOrder(ReadOrder r){
        getContext().getLog().info("Internet read request {}", r.replyTo);
        r.replyTo.tell(new RequiredOrder(Optional.of(orderedProducts)));
        orderedProducts = new HashMap<>();
        return this;
    }
}
