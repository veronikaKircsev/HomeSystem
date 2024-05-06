package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.helpClass.BlindsActuator;

import java.util.Optional;

public class Blinds extends AbstractBehavior<Blinds.BlindsCommand> {

    public interface BlindsCommand{}

    public static final class ChangeCondition implements BlindsCommand{
        final Optional<Boolean> condition;
        final Optional<BlindsActuator> myActuator;

        public ChangeCondition(Optional<Boolean> condition, Optional<BlindsActuator> myActuator) {
            this.condition = condition;
            this.myActuator = myActuator;
        }
    }
    private final String groupId;
    private final String deviceId;
    private boolean isOpen = true;
    private BlindsActuator lastActuator;

    public Blinds(ActorContext<BlindsCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        getContext().getLog().info("Blinds are standby");
    }

    public static Behavior<Blinds.BlindsCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new Blinds(context, groupId, deviceId));
    }

    private Behavior<BlindsCommand> readCommand(ChangeCondition m) {
        getContext().getLog().info("Blinds reading {} from {}", m.condition.get(), m.myActuator.get());
        if (!lastActuator.equals(BlindsActuator.MediaStation) ||
                (lastActuator.equals(BlindsActuator.MediaStation) && m.myActuator.get().equals(BlindsActuator.MediaStation))
        || (isOpen && lastActuator.equals(BlindsActuator.MediaStation))){
            isOpen = m.condition.get();
            lastActuator = m.myActuator.get();
            String message = m.condition.get() ? "Blinds are open by " + lastActuator : "Blinds are closed by " + lastActuator;
            getContext().getLog().info(message);
        } else {
            getContext().getLog().info("Command {} for {} is not available", m.condition.get(), m.myActuator.get());
        }
        return this;
    }

    @Override
    public Receive<BlindsCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ChangeCondition.class, this::readCommand)
                .build();
    }


}
