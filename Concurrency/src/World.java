import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class World {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("SuperFlexWorld");

        ActorRef superFlex = system.actorOf(SuperFlex.props(1, 1));

        for (int i=0; i<1; i++) {
            system.actorOf(Customer.props(i, superFlex));
        }
    }

}
