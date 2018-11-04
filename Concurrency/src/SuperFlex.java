import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.Routee;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;
import model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuperFlex extends AbstractActor{

    public static Props props(int nrOfLocations, int nrOfRentalAgents) {
        return Props.create(SuperFlex.class, nrOfLocations, nrOfRentalAgents);
    }

    private ActorSystem actorSystem;
    private Router rentalAgentRouter;
    private List<Location> locations;
    private List<ActorRef> locationAgents;
    private int nrOfLocations;
    private int nrOfRentalAgents;

    public SuperFlex(int nrOfLocations, int nrOfRentalAgents) {
        this.actorSystem = getContext().getSystem();

        this.nrOfLocations = nrOfLocations;
        this.nrOfRentalAgents = nrOfRentalAgents;

        locations = new ArrayList<>();
        locationAgents = new ArrayList<>();

        makeLocations();
        makeLocationAgents();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        ArrayList<Routee> routes = new ArrayList<>();

        assert locationAgents.size() > 0;
        for (int i=0; i<nrOfRentalAgents; i++) {
            ActorRef rentalAgent = actorSystem.actorOf(Props.create(RentalAgent.class, i, locationAgents));
            routes.add(new ActorRefRoutee(rentalAgent));
        }

        rentalAgentRouter = new Router(new SmallestMailboxRoutingLogic(), routes);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.LocationListRequest.class, locationListRequest -> {
                    System.out.println("superflex received request to return all locations from ");
                    rentalAgentRouter.route(new Messages.LocationList(locations, getSender()), getSelf());
                })
                .build();
    }

    private void makeLocations() {
        Random random = new Random();
        for (int i=0; i<nrOfLocations; i++) {
            // Make new location with 10 - 50 offices
            locations.add(new Location(i, random.nextInt(40)+10));
        }
    }

    private void makeLocationAgents() {
        for (int i=0; i<locations.size(); i++) {
            // Make new locationAgent
            locationAgents.add(actorSystem.actorOf(Props.create(LocationAgent.class, i, locations.get(i))));
        }
    }

}