package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.ActorRef;
import at.fhv.sysarch.lab2.homeautomation.helpClass.BlindsActuator;

import java.util.Optional;

public class MediaStation extends AbstractBehavior<MediaStation.MediaStationCommand> {

    public interface MediaStationCommand {}

    public static final class ChangeCondition implements MediaStationCommand {
        final Optional<Boolean> isOn;

        public ChangeCondition(Optional<Boolean> isOn) {
            this.isOn = isOn;
        }
    }

    private final String groupId;
    private final String deviceId;
    private boolean isOn = false;
    private ActorRef<Blinds.BlindsCommand> blinds;

    public MediaStation(ActorContext<MediaStationCommand> context, String groupId, String deviceId, ActorRef<Blinds.BlindsCommand> blinds) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.blinds = blinds;
        getContext().getLog().info("MediaStation is standBy");
    }


    public static Behavior<MediaStationCommand> create(String groupId, String deviceId, ActorRef<Blinds.BlindsCommand> blinds) {
        return Behaviors.setup(context -> new MediaStation(context, groupId, deviceId, blinds));
    }

    @Override
    public Receive<MediaStationCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ChangeCondition.class, this::turnOnOff)
                .build();
    }


    private Behavior<MediaStationCommand> turnOnOff(ChangeCondition c){
        if (isOn && c.isOn.get()){
            getContext().getLog().info("Sorry a movie is going");
        } else if (isOn && !c.isOn.get()){
            isOn = c.isOn.get();
            blinds.tell(new Blinds.ChangeCondition(Optional.of(true), Optional.of(BlindsActuator.MediaStation)));
            getContext().getLog().info("The movie is finished");
        } else {
            isOn = c.isOn.get();
            blinds.tell(new Blinds.ChangeCondition(Optional.of(false), Optional.of(BlindsActuator.MediaStation)));
            getContext().getLog().info("The movie is started");
        }
        return this;
    }

}
