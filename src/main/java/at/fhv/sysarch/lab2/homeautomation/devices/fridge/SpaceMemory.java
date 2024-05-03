package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class SpaceMemory extends AbstractBehavior<SpaceMemory.SpaceMemoryCommand> {

    public interface SpaceMemoryCommand{

    }

    /*

    public static final class ReadSpace implements SpaceMemoryCommand {
        final ActorRef<SpaceMemory.RequiredSpace> replyTo;

        public ReadSpace(ActorRef<SpaceMemory.RequiredSpace> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class RequiredSpace {
        final Optional<Integer> value;

        public RequiredSpace(Optional<Integer> value) {
            this.value = value;
        }
    }

     */


    private Optional<Integer> lastSpace = Optional.empty();



    public SpaceMemory(ActorContext<SpaceMemoryCommand> context) {
        super(context);
    }

    public static Behavior<SpaceMemory.SpaceMemoryCommand> create() {
        return Behaviors.setup(context -> new SpaceMemory(context));
    }

    @Override
    public Receive<SpaceMemoryCommand> createReceive() {
        return null;
    }

    /*
    private Behavior<SpaceMemoryCommand> onRequiredSpace(ReadSpace r){
        r.replyTo.tell(new RequiredSpace(lastSpace));
        return this;
    }

     */
}
